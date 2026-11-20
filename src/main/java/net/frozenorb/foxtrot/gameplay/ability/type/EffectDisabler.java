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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EffectDisabler extends Ability {

    private final Map<UUID, Map<UUID, Integer>> hits = new HashMap<>();

    public EffectDisabler() {
        this.hassanStack = ItemBuilder.of(this.getMaterial()).name(this.getDisplayName()).setLore(this.getLore())
                .build();
    }

    @Override
    public String getName() {
        return "EffectDisabler";
    }

    @Override
    public Material getMaterial() {
        return Material.SLIME_BALL;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Effect Disabler";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fHit a player 3 times within 10"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fseconds while holding this to temporarily"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fdisable all of their active potion effects!"));
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
        return 120_000L;
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "You have disabled your target's effects!";
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

            Collection<PotionEffect> activeEffects = new ArrayList<>(victim.getActivePotionEffects());

            attacker.sendMessage(
                    ChatColor.DARK_GREEN + "You have disabled " + victim.getName() + "'s effects for 8 seconds!");
            victim.sendMessage(ChatColor.DARK_GREEN + attacker.getName() + " has disabled your effects for 8 seconds!");

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (!victim.isOnline() || victim.isDead() || ticks >= 20 * 8) {
                        this.cancel();
                        if (victim.isOnline()) {
                            victim.addPotionEffects(activeEffects);
                            victim.sendMessage(ChatColor.GREEN + "Your previous potion effects have returned!");
                        }
                        return;
                    }

                    for (PotionEffect effect : victim.getActivePotionEffects()) {
                        victim.removePotionEffect(effect.getType());
                    }

                    ticks += 5;
                }
            }.runTaskTimer(Foxtrot.getInstance(), 0L, 5L);

        } else {
            attacker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cYou have to hit &f" + victim.getName() + " &c" + (3 - currentHits) + " more time" + (3 - currentHits == 1 ? "" : "s") + "!"));
        }
    }
}
