package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoveBomb extends Ability {

    private final Map<UUID, HitRecord> lastHit = new HashMap<>();

    private static class HitRecord {
        UUID target;
        long timestamp;

        HitRecord(UUID target, long timestamp) {
            this.target = target;
            this.timestamp = timestamp;
        }
    }

    public LoveBomb() {
        this.hassanStack = ItemBuilder.of(this.getMaterial()).name(this.getDisplayName()).setLore(this.getLore())
                .build();
    }

    @Override
    public String getName() {
        return "LoveBomb";
    }

    @Override
    public Material getMaterial() {
        return Material.DIAMOND;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Love Bomb";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fRight-click to strike the last player"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fyou hit with 5 true-damage explosions."));
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
        return "You have launched a Love Bomb at your target!";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            lastHit.put(attacker.getUniqueId(), new HitRecord(victim.getUniqueId(), System.currentTimeMillis()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !this.isSimilar(event.getPlayer().getItemInHand())) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You may not use " + this.getDisplayName() + ChatColor.RED
                    + " whilst your " + ChatColor.GREEN + ChatColor.BOLD + "PvP Timer " + ChatColor.RED + "is active!");
            event.setCancelled(true);
            return;
        }

        if (this.hasCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        HitRecord record = lastHit.get(player.getUniqueId());
        if (record == null || System.currentTimeMillis() - record.timestamp > 30_000L) {
            player.sendMessage(
                    ChatColor.RED + "You must have hit a player within the last 30 seconds to use this ability.");
            event.setCancelled(true);
            return;
        }

        Player target = Foxtrot.getInstance().getServer().getPlayer(record.target);
        if (target == null || !target.isOnline() || target.isDead()) {
            player.sendMessage(ChatColor.RED + "Your target is no longer online or alive.");
            event.setCancelled(true);
            return;
        }

        if (target.getLocation().distance(player.getLocation()) > 50) {
            player.sendMessage(ChatColor.RED + "Your target is too far away to launch the Love Bomb.");
            event.setCancelled(true);
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, target, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        if (!this.isAllowedAtLocation(player.getLocation()) || !this.isAllowedAtLocation(target.getLocation())) {
            player.sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED
                    + " here or your target is in a safe zone.");
            event.setCancelled(true);
            return;
        }

        this.applyCooldown(player);
        this.takeItem(player);
        event.setCancelled(true);

        new BukkitRunnable() {
            int explosions = 0;

            @Override
            public void run() {
                if (explosions >= 5 || target.isDead() || !target.isOnline()) {
                    this.cancel();
                    return;
                }

                Location loc = target.getLocation();
                target.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.EXPLOSION_HUGE, 1);
                target.getWorld().playSound(loc, org.bukkit.Sound.EXPLODE, 1.0F, 1.0F);

                target.damage(0.0);
                double health = target.getHealth();
                if (health > 2.0) {
                    target.setHealth(health - 2.0);
                } else {
                    target.setHealth(0.0);
                }

                explosions++;
            }
        }.runTaskTimer(Foxtrot.getInstance(), 0L, 5L);

        player.sendMessage(ChatColor.LIGHT_PURPLE + "You dropped a Love Bomb on " + target.getName() + "!");
        target.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + " dropped a Love Bomb on you!");
    }
}
