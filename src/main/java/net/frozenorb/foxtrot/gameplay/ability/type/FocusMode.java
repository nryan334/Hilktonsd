package net.frozenorb.foxtrot.gameplay.ability.type;

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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class FocusMode extends Ability {

    private static final String FOCUS_METADATA = "FOCUS_MODE_ACTIVE";
    private static final String FOCUS_TARGET_METADATA = "FOCUS_TARGET";
    private static final int DURATION_SECONDS = 8;

    private final Set<UUID> activePlayers = new HashSet<>();

    @Override
    public String getName() {
        return "FocusMode";
    }

    @Override
    public Material getMaterial() {
        return Material.REDSTONE_TORCH_ON;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Focus Mode";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fRight-Click to activate for &c&l" + DURATION_SECONDS + " seconds&f!"));
        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fWhile active, deal &c&l10% more &fdamage"));
        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fto your current target but take &c&l5% more"));
        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fdamage from other players."));
        toReturn.add("");

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return DTR(location);
    }

    @Override
    public long getCooldown() {
        return TimeUnit.SECONDS.toMillis(150);
    }

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getDescription() {
        return "Deal 10% more damage to your target but take 5% more from others for " + DURATION_SECONDS + " seconds.";
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final Player player = event.getPlayer();

        event.setCancelled(true);

        if (activePlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Focus Mode is already active!");
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        activePlayers.add(player.getUniqueId());
        player.setMetadata(FOCUS_METADATA, new FixedMetadataValue(Foxtrot.getInstance(), true));

        player.sendMessage("");
        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Focus Mode " + ChatColor.GREEN + "activated"
                + ChatColor.GRAY + " for " + ChatColor.WHITE + DURATION_SECONDS + " seconds" + ChatColor.GRAY + "!");
        player.sendMessage(ChatColor.GRAY + "Deal " + ChatColor.RED + "+10%" + ChatColor.GRAY
                + " damage to your target, but take " + ChatColor.RED + "+5%" + ChatColor.GRAY + " from others.");
        player.sendMessage("");

        this.applyCooldown(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                deactivate(player);
            }
        }.runTaskLater(Foxtrot.getInstance(), DURATION_SECONDS * 20L);
    }

    private void deactivate(Player player) {
        if (!activePlayers.remove(player.getUniqueId())) {
            return;
        }

        player.removeMetadata(FOCUS_METADATA, Foxtrot.getInstance());
        player.removeMetadata(FOCUS_TARGET_METADATA, Foxtrot.getInstance());

        if (player.isOnline()) {
            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Focus Mode " + ChatColor.GRAY + "has "
                    + ChatColor.RED + "expired" + ChatColor.GRAY + ".");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (activePlayers.contains(damager.getUniqueId())) {
            if (isAllowedAtLocation(damager.getLocation()) && isAllowedAtLocation(victim.getLocation())) {
                damager.setMetadata(FOCUS_TARGET_METADATA,
                        new FixedMetadataValue(Foxtrot.getInstance(), victim.getUniqueId()));
                event.setDamage(event.getDamage() * 1.10);
            }
        }

        if (activePlayers.contains(victim.getUniqueId())) {
            if (victim.hasMetadata(FOCUS_TARGET_METADATA)) {
                UUID focusTarget = (UUID) victim.getMetadata(FOCUS_TARGET_METADATA).get(0).value();
                if (!focusTarget.equals(damager.getUniqueId())) {
                    event.setDamage(event.getDamage() * 1.05);
                }
            } else {
                event.setDamage(event.getDamage() * 1.05);
            }
        }
    }
}
