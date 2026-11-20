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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CloseCallAbility extends Ability {

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }


    @Override
    public String getName() {
        return "CloseCall";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Close Call";
    }

    @Override
    public Material getMaterial() {
        return Material.COOKIE;
    }

    @Override
    public String getDescription() {
        return "Grants Regeneration V and Strength II when at low health!";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fRight-click to gain &a&lRegeneration V &fand &c&lStrength II"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &ffor &7&l8 seconds&f."));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &cMust be used below 3.5 hearts!"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &c2m30s"));
        return lore;
    }

    @Override
    public long getCooldown() {
        return 150_000L;
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
        if (!isSimilar(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        if (this.hasCooldown(player)) {
            return;
        }

        if (!isAllowedAtLocation(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You cannot use this ability in the spawn!");
            return;
        }

        this.takeItem(player);

        if (player.getHealth() > 7.0) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ZOMBIE_WOODBREAK, 1, 1);
            player.sendMessage(ChatColor.RED + "You didn't do it in time (You must have less than 3.5 hearts).");
            player.sendMessage(ChatColor.RED + "The " + this.getDisplayName() + ChatColor.RED + " has been consumed!");
            return;
        }

        AbilityUseEvent useEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled()) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 8 * 20, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8 * 20, 1));

        player.playSound(player.getLocation(), org.bukkit.Sound.LEVEL_UP, 1, 1);
        player.sendMessage(ChatColor.RED + "You have activated the " + this.getDisplayName() + ChatColor.RED + "!");
        player.sendMessage(ChatColor.RED + "You have received Regeneration V and Strength II due to the " + this.getDisplayName() + "!");

        this.applyCooldown(player);
    }
}
