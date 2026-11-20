package net.frozenorb.foxtrot.gameplay.events.king;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class KingListener implements Listener {

    private final KingManager manager;

    public KingListener(KingManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (manager.isActive() && manager.getKing().equals(player)) {
            manager.stopKing(false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (manager.isActive() && manager.getKing().equals(player)) {
            player.setHealth(0.0);
            manager.stopKing(false);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!manager.isActive() || !manager.getKing().equals(player))
            return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (manager.isLaser(player.getItemInHand())) {
                event.setCancelled(true);

                if (player.hasMetadata("laser_cooldown")
                        && player.getMetadata("laser_cooldown").get(0).asLong() > System.currentTimeMillis()) {
                    return;
                }

                player.setMetadata("laser_cooldown",
                        new FixedMetadataValue(Foxtrot.getInstance(), System.currentTimeMillis() + 5000L));

                Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setVelocity(player.getLocation().getDirection().multiply(4.0));
                arrow.setMetadata("king_laser", new FixedMetadataValue(Foxtrot.getInstance(), true));
                player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);

            } else if (manager.isDash(player.getItemInHand())) {
                event.setCancelled(true);

                if (player.hasMetadata("dash_cooldown")
                        && player.getMetadata("dash_cooldown").get(0).asLong() > System.currentTimeMillis()) {
                    return;
                }

                player.setMetadata("dash_cooldown",
                        new FixedMetadataValue(Foxtrot.getInstance(), System.currentTimeMillis() + 5000L));

                Vector dir = player.getLocation().getDirection();
                player.setVelocity(dir.multiply(2.5).setY(0.8));
                player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onDamageByLaser(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getDamager().hasMetadata("king_laser")) {
            event.setDamage(6.0);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!manager.isActive() || !manager.getKing().equals(player))
            return;

        Location loc = player.getLocation();
        int highestBlock = loc.getWorld().getHighestBlockYAt(loc);

        if (loc.getY() > highestBlock + manager.getFlightLimit()) {
            player.setVelocity(player.getVelocity().setY(-0.5));
        }
    }
}
