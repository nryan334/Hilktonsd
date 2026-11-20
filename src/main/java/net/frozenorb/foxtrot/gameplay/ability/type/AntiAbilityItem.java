package net.frozenorb.foxtrot.gameplay.ability.type;

import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class AntiAbilityItem extends Ability {

    private final Map<UUID, CombatEntry> lastHit = new HashMap<>();

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getName() {
        return "AntiAbility";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Anti-Ability";
    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_ROD;
    }

    @Override
    public String getDescription() {
        return "Blocks enemy ability items!";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fClick to apply an &c&l8s &fAbility Cooldown"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &f to the last player you hit in the last 15s."));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &c1m30s"));
        return lore;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        lastHit.put(damager.getUniqueId(), new CombatEntry(victim.getUniqueId(), System.currentTimeMillis()));
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

        CombatEntry entry = lastHit.get(player.getUniqueId());
        if (entry == null || System.currentTimeMillis() - entry.getTimestamp() > 15_000L) {
            player.sendMessage(ChatColor.RED + "You haven't hit anyone in the last 15 seconds!");
            return;
        }

        Player target = Foxtrot.getInstance().getServer().getPlayer(entry.getTargetId());
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Your last target is no longer online!");
            return;
        }

        AbilityUseEvent useEvent = new AbilityUseEvent(player, target, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled()) {
            return;
        }

        this.takeItem(player);

        Foxtrot.getInstance().getMapHandler().getAbilityHandler().getGlobalCooldowns().put(target.getUniqueId(),
                System.currentTimeMillis() + 8_000L);

        target.sendMessage("");
        target.sendMessage(ChatColor.RED + "You have been hit by an " + getDisplayName() + ChatColor.RED + "!");
        target.sendMessage(ChatColor.RED + "You cannot use ability items for 8 seconds!");
        target.sendMessage("");

        player.sendMessage(ChatColor.GREEN + "You have applied an ability cooldown to " + ChatColor.WHITE
                + target.getName() + ChatColor.GREEN + "!");

        this.applyCooldown(player);
    }

    @Data
    @AllArgsConstructor
    private static class CombatEntry {
        private UUID targetId;
        private long timestamp;
    }
}
