package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LifeSteal extends Ability {

    private final Map<UUID, Map<UUID, Integer>> hits = new HashMap<>();

    public LifeSteal() {
        this.hassanStack = ItemBuilder.of(this.getMaterial()).name(this.getDisplayName()).setLore(this.getLore())
                .build();
    }

    @Override
    public String getName() {
        return "LifeSteal";
    }

    @Override
    public Material getMaterial() {
        return Material.REDSTONE;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Life Steal";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fHit a player 3 times within 10"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fseconds while holding this to steal"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fhealth and apply nasty debuffs!"));
        lore.add("");
        return lore;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (location.getWorld().getName().equalsIgnoreCase("sg")) {
            return true;
        }
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
        return 85_000L;
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "You have drained your target's life!";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (attacker.getItemInHand() == null || !this.isSimilar(attacker.getItemInHand())) {
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(attacker.getUniqueId())) {
            attacker.sendMessage(ChatColor.RED + "You may not use " + this.getDisplayName() + ChatColor.RED
                    + " whilst your " + ChatColor.GREEN + ChatColor.BOLD + "PvP Timer " + ChatColor.RED + "is active!");
            event.setCancelled(true);
            return;
        }

        if (this.hasCooldown(attacker)) {
            return;
        }

        hits.putIfAbsent(attacker.getUniqueId(), new HashMap<>());
        Map<UUID, Integer> targetHits = hits.get(attacker.getUniqueId());

        int currentHits = targetHits.getOrDefault(victim.getUniqueId(), 0) + 1;
        
        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(attacker, victim, attacker.getLocation(), this,
                false);
        if (currentHits != 3) {
            abilityUseEvent.setOneHit(true);
        }
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (!this.isAllowedAtLocation(attacker.getLocation()) || !this.isAllowedAtLocation(victim.getLocation())) {
            attacker.sendMessage(
                    ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " here.");
            return;
        }

        targetHits.put(victim.getUniqueId(), currentHits);

        if (currentHits == 1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Map<UUID, Integer> tHits = hits.get(attacker.getUniqueId());
                    if (tHits != null) {
                        tHits.remove(victim.getUniqueId());
                    }
                }
            }.runTaskLater(Foxtrot.getInstance(), 20L * 10);
        }

        if (currentHits >= 3) {
            targetHits.remove(victim.getUniqueId());

            this.applyCooldown(attacker);
            this.takeItem(attacker);

            double newHealth = Math.min(attacker.getMaxHealth(), attacker.getHealth() + 3.0);
            attacker.setHealth(newHealth);

            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 8, 0), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 8, 0), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 4, 0), true);

            attacker.sendMessage(
                    ChatColor.DARK_RED + "You have successfully used Life Steal on " + victim.getName() + "!");
            victim.sendMessage(ChatColor.DARK_RED + attacker.getName() + " has used Life Steal on you!");
        } else {
            attacker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cYou have to hit &f" + victim.getName() + " &c" + (3 - currentHits) + " more time" + (3 - currentHits == 1 ? "" : "s") + "!"));
        }
    }
}
