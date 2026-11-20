package net.frozenorb.foxtrot.gameplay.events.motw;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MOTWListener implements Listener {

    private final MOTWManager manager;

    public MOTWListener(MOTWManager manager) {
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!manager.isActive()) return;

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!manager.hasEnabled(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!manager.isActive()) return;

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            if (!manager.hasEnabled(victim) || !manager.hasEnabled(attacker)) {
                event.setCancelled(true);
            }
        }
    }
}
