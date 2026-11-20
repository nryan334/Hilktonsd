package net.frozenorb.foxtrot.gameplay.abilitytrader;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.server.ServerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class AbilityTraderHandler implements Listener {

    @Getter
    private Villager activeNPC = null;

    public AbilityTraderHandler() {
        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public void spawnNPC() {
        despawnNPC();

        Location loc = getRandomWarzoneLocation();
        if (loc == null) {
            loc = Bukkit.getWorlds().get(0).getSpawnLocation();
        }

        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.setCustomName(ChatColor.GREEN + ChatColor.BOLD.toString() + "★ Ability Trader NPC ★");
        villager.setCustomNameVisible(true);
        villager.setProfession(Villager.Profession.LIBRARIAN);

        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100, false));

        villager.setMaxHealth(120.0);
        villager.setHealth(120.0);
        villager.setMetadata("ability_trader", new FixedMetadataValue(Foxtrot.getInstance(), true));

        this.activeNPC = villager;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4&lAbility Trader"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&7spawned at &b" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                        + " &7in Warzone."));
        Bukkit.broadcastMessage("");
    }

    public void despawnNPC() {
        if (activeNPC != null && activeNPC.isValid()) {
            activeNPC.remove();
        }

        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(Villager.class)) {
                if (entity.hasMetadata("ability_trader")) {
                    entity.remove();
                }
            }
        }
        this.activeNPC = null;
    }

    private Location getRandomWarzoneLocation() {
        org.bukkit.World world = Bukkit.getWorlds().get(0);
        Random random = new Random();
        int warzoneRadius = ServerHandler.WARZONE_RADIUS;

        for (int attempts = 0; attempts < 150; attempts++) {
            int x = -warzoneRadius + random.nextInt(warzoneRadius * 2);
            int z = -warzoneRadius + random.nextInt(warzoneRadius * 2);
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y, z + 0.5);

            Team team = LandBoard.getInstance().getTeam(loc);
            if (team != null && team.getName().equalsIgnoreCase("Warzone")) {
                Material blockType = loc.getBlock().getRelative(0, -1, 0).getType();
                if (blockType != Material.STATIONARY_LAVA && blockType != Material.LAVA
                        && blockType != Material.STATIONARY_WATER
                        && blockType != Material.WATER) {
                    return loc;
                }
            }
        }

        int x = -300 + random.nextInt(600);
        int z = -300 + random.nextInt(600);
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x + 0.5, y, z + 0.5);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();
            if (villager.hasMetadata("ability_trader")) {
                event.setCancelled(true);
                openTradeMenu(event.getPlayer());
            }
        }
    }

    private void openTradeMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GREEN + "Ability Trader");

        List<Ability> metaAbilities = new ArrayList<>();
        for (Ability ability : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
            if (ability.hasMetadata("META")
                    && Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability)) {
                metaAbilities.add(ability);
            }
        }

        int slot = 0;
        for (Ability ab : metaAbilities) {
            if (slot >= 27)
                break;
            ItemStack item = ab.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GOLD + "Trade Requirements:");
            lore.add(ChatColor.YELLOW + "- $5,000 Faction/Player Balance");
            lore.add(ChatColor.YELLOW + "- 1x Any OTHER Active Ability");
            lore.add("");
            lore.add(ChatColor.GREEN + "Click to trade!");
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null
                && event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Ability Trader")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR)
                return;

            String displayName = clicked.getItemMeta().getDisplayName();
            Ability matched = null;
            for (Ability ab : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
                if (ab.getItem().getItemMeta().getDisplayName().equals(displayName)) {
                    matched = ab;
                    break;
                }
            }

            if (matched == null)
                return;

            double balance = Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId());
            if (balance < 5000) {
                player.sendMessage(ChatColor.RED + "You do not have enough money! You need at least $5,000.");
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1.2f);
                return;
            }

            ItemStack otherAbilityItem = findOtherAbilityInInventory(player, matched);
            if (otherAbilityItem == null) {
                player.sendMessage(ChatColor.RED
                        + "You do not have any other HCF abilities to trade! You must give an ability in exchange.");
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1.2f);
                return;
            }

            // Deduct
            Foxtrot.getInstance().getEconomyHandler().withdraw(player.getUniqueId(), 5000);
            String tradedName = otherAbilityItem.getItemMeta().getDisplayName();
            removeOneItem(player, otherAbilityItem);

            player.getInventory().addItem(matched.getItem().clone());
            player.sendMessage(
                    ChatColor.GREEN + "Trade completed! Traded 1x " + tradedName + ChatColor.GREEN + " and $5,000 for "
                            + matched.getDisplayName() + ChatColor.GREEN + ".");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
            player.closeInventory();
        }
    }

    private ItemStack findOtherAbilityInInventory(Player player, Ability abilityToBuy) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            for (Ability ab : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
                if (ab.getDisplayName().equalsIgnoreCase(abilityToBuy.getDisplayName()))
                    continue;
                if (ab.getItem().isSimilar(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    private void removeOneItem(Player player, ItemStack targetItem) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.isSimilar(targetItem)) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    contents[i] = null;
                }
                break;
            }
        }
        player.getInventory().setContents(contents);
        player.updateInventory();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            if (villager.hasMetadata("ability_trader")) {
                event.getDrops().clear();

                for (Ability ability : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities()
                        .values()) {
                    if (Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability)) {
                        event.getDrops().add(ability.getItem().clone());
                    }
                }

                Player killer = villager.getKiller();
                String killerName = killer != null ? killer.getName() : "a player";

                if (killer != null) {
                    int currentTokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(killer.getUniqueId());
                    Foxtrot.getInstance().getKitmapTokensMap().setTokens(killer.getUniqueId(), currentTokens + 64);
                }

                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4&lAbility Trader"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7has been killed by &b" + killerName + "&7! 64 Tokens awarded to killer."));
                Bukkit.broadcastMessage("");

                spawnNPC();
            }
        }
    }
}
