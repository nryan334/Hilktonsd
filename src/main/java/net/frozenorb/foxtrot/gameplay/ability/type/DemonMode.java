package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class DemonMode extends Ability {

    private final Set<UUID> restrictedPlayers = new HashSet<>();

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getName() {
        return "DemonMode";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Demon Mode";
    }

    @Override
    public Material getMaterial() {
        return Material.NETHER_STAR;
    }

    @Override
    public String getDescription() {
        return "Unleash your inner demon!";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fGain &c&lStrength I&f, &c&lResistance I&f, and &c&lSpeed II"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &ffor &c&l8 seconds&f."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &c&lCAUTION: &fYou cannot use potions during this time!"));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &c2m"));
        return lore;
    }

    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        Player player = event.getPlayer();

        if (restrictedPlayers.contains(player.getUniqueId())) {
            if (event.getItem() != null && event.getItem().getType() == Material.POTION) {
                player.sendMessage(ChatColor.RED + "You cannot use potions while in Demon Mode!");
                event.setCancelled(true);
                return;
            }
        }

        if (!isSimilar(event.getItem())) {
            return;
        }

        AbilityUseEvent useEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled()) {
            return;
        }

        this.takeItem(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8 * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 8 * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 20, 1));

        restrictedPlayers.add(player.getUniqueId());
        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEMON MODE ACTIVATED!");

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            restrictedPlayers.remove(player.getUniqueId());
            if (player.isOnline()) {
                player.sendMessage(ChatColor.GRAY + "Demon Mode has ended.");
            }
        }, 8 * 20L);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (restrictedPlayers.contains(shooter.getUniqueId())) {
                shooter.sendMessage(ChatColor.RED + "You cannot use potions while in Demon Mode!");
                event.setCancelled(true);
            }
        }
    }
}
