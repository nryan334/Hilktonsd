package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class SwitchStick extends Ability {

    private static final String HITS_METADATA = "SS_HITS";
    private static final String TARGET_METADATA = "SS_TARGET";

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getDescription() {
        return "Hit 3 times to an enemy to switch their head up.";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.STICK;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Switch Stick";
    }

    @Override
    public List<String> getLore() {
        return getStrings();
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return DTR(location);
    }

    @Override
    public long getCooldown() {
        return 60_000L;
    }

    @NotNull
    static List<String> getStrings() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fHit &c&l3 times &fto a player"));
        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fto up their head"));
        toReturn.add("");

        return toReturn;
    }

    @Override
    public Boolean DTR(Location location) {
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();

        if (!isSimilar(damager.getItemInHand())) {
            return;
        }

        if (hasCooldown(damager)) {
            return;
        }

        if (!isAllowedAtLocation(target.getLocation()) || !isAllowedAtLocation(damager.getLocation())) {
            return;
        }

        int hits = 0;
        if (damager.hasMetadata(HITS_METADATA) && damager.hasMetadata(TARGET_METADATA)) {
            UUID lastTarget = (UUID) damager.getMetadata(TARGET_METADATA).get(0).value();
            if (lastTarget.equals(target.getUniqueId())) {
                hits = damager.getMetadata(HITS_METADATA).get(0).asInt();
            }
        }

        hits++;

        int requiredHits = 3;
        if (hits >= requiredHits) {
            final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, target.getLocation(), this, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

            if (abilityUseEvent.isCancelled()) {
                return;
            }

            rotate(target);
            applyCooldown(damager);

            damager.removeMetadata(HITS_METADATA, Foxtrot.getInstance());
            damager.removeMetadata(TARGET_METADATA, Foxtrot.getInstance());

            if (damager.getItemInHand().getAmount() > 1) {
                damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
            } else {
                damager.setItemInHand(null);
            }

            damager.sendMessage(ChatColor.GREEN + "You have activated your Switch Stick on " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + "!");
            target.sendMessage(ChatColor.RED + "You have been rotated by " + ChatColor.WHITE + damager.getName() + "'s " + ChatColor.GOLD + "Switch Stick" + ChatColor.RED + "!");

            target.playSound(target.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            target.playSound(target.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            damager.playSound(damager.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
            damager.playSound(damager.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        } else {
            damager.setMetadata(HITS_METADATA, new FixedMetadataValue(Foxtrot.getInstance(), hits));
            damager.setMetadata(TARGET_METADATA, new FixedMetadataValue(Foxtrot.getInstance(), target.getUniqueId()));
            damager.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Switch Stick: " + ChatColor.WHITE + hits + "/" + requiredHits);
        }
    }

    private void rotate(Player player) {
        Location loc = player.getLocation();
        loc.setYaw(loc.getYaw() + 180.0F);
        loc.setPitch(-90.0F);
        player.teleport(loc);
    }
}

