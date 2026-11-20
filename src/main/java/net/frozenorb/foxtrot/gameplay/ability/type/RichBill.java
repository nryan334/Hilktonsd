package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.ItemBuilder;
import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Table;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Fyre Games (JavaProject)
 * @Reworked Hxmilton
 */

public class RichBill extends Ability {

    private final Table<UUID, UUID, Integer> hits = HashBasedTable.create();
    private final int requiredHits = 3;
    private final int DURATION_SECONDS = 8;

    private final Map<UUID, ItemStack> cachedHelmets = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    private final ItemStack HOLY_COIN = ItemBuilder.of(Material.GOLD_NUGGET)
            .name(ChatColor.GOLD + "Holy Coin")
            .build();

    @Override
    public String getName() {
        return "RichBill";
    }

    @Override
    public Material getMaterial() {
        return Material.GOLD_NUGGET;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + "Rich Bill";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(CC.translate("&6❙ &fHit a player &c&l" + requiredHits + " times &fto"));
        lore.add(CC.translate("&6❙ &fchange their helmet to a"));
        lore.add(CC.translate("&6❙ &fGold Helmet for &e&l" + DURATION_SECONDS + " seconds&f!"));
        lore.add("");
        return lore;
    }

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getDescription() {
        return "You have changed their helmet to a Gold Helmet!";
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location)
                    && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location)
                    && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location)
                && location.getWorld().getEnvironment() == World.Environment.NORMAL
                && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location)
                && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location)
                && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return TimeUnit.MINUTES.toMillis(2L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();

        ItemStack itemInHand = damager.getItemInHand();
        if (!isSimilar(itemInHand)) {
            return;
        }

        if (this.hasCooldown(damager)) {
            return;
        }

        if (cachedHelmets.containsKey(target.getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "This player is already under the effect of Rich Bill!");
            return;
        }

        ItemStack helmet = target.getInventory().getHelmet();
        if (helmet == null || helmet.getType() == Material.AIR) {
            damager.sendMessage(ChatColor.RED + target.getName() + " is not wearing a helmet!");
            return;
        }

        if (PvPClassHandler.getPvPClass(target) != null) {
            damager.sendMessage(ChatColor.RED + "You cannot use Rich Bill on players in a PvP Class!");
            return;
        }

        UUID damagerId = damager.getUniqueId();
        UUID targetId = target.getUniqueId();

        int currentHits = hits.contains(damagerId, targetId) ? hits.get(damagerId, targetId) : 0;

        if (currentHits + 1 >= requiredHits) {
            AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);
            Bukkit.getPluginManager().callEvent(abilityUseEvent);

            if (abilityUseEvent.isCancelled()) {
                return;
            }

            hits.remove(damagerId, targetId);
            activateEffect(damager, target);
        } else {
            currentHits++;
            hits.put(damagerId, targetId, currentHits);
            damager.sendMessage(ChatColor.YELLOW + "You have hit " + ChatColor.GOLD + target.getName() + " "
                    + ChatColor.RED + currentHits + "/" + requiredHits + ChatColor.YELLOW + " times!");
        }
    }

    private void activateEffect(Player damager, Player target) {
        if (damager.getItemInHand().getAmount() > 1) {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        } else {
            damager.setItemInHand(null);
        }
        damager.updateInventory();

        this.applyCooldown(damager);

        damager.sendMessage(ChatColor.GOLD + "You have activated " + getDisplayName() + ChatColor.GOLD + " on "
                + ChatColor.WHITE + target.getName() + ChatColor.GOLD + "!");
        target.sendMessage(ChatColor.RED + "You have been hit with a " + getDisplayName() + ChatColor.RED + " by "
                + ChatColor.WHITE + damager.getName() + ChatColor.RED + "!");

        cachedHelmets.put(target.getUniqueId(), target.getInventory().getHelmet());
        target.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
        target.updateInventory();

        for (int i = 0; i < target.getInventory().getSize(); i++) {
            ItemStack item = target.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                target.getInventory().setItem(i, HOLY_COIN.clone());
            }
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                revertEffect(target);
            }
        }.runTaskLater(Foxtrot.getInstance(), DURATION_SECONDS * 20L);

        activeTasks.put(target.getUniqueId(), task);
    }

    private void revertEffect(Player player) {
        if (!cachedHelmets.containsKey(player.getUniqueId())) {
            return;
        }

        ItemStack originalHelmet = cachedHelmets.remove(player.getUniqueId());
        player.getInventory().setHelmet(originalHelmet);
        player.updateInventory();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.isSimilar(HOLY_COIN)) {
                player.getInventory().setItem(i, null);
            }
        }

        activeTasks.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.ARMOR && event.getSlot() != 39) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (cachedHelmets.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (cachedHelmets.containsKey(player.getUniqueId())) {
            revertEffect(player);
            if (activeTasks.containsKey(player.getUniqueId())) {
                activeTasks.get(player.getUniqueId()).cancel();
                activeTasks.remove(player.getUniqueId());
            }
        }

        event.getDrops().removeIf(it -> it.isSimilar(HOLY_COIN));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (cachedHelmets.containsKey(event.getPlayer().getUniqueId())
                && event.getItemDrop().getItemStack().isSimilar(HOLY_COIN)) {
            event.setCancelled(true);
        }
    }

    @Override
    public Boolean DTR(Location location) {
        return isAllowedAtLocation(location);
    }
}
