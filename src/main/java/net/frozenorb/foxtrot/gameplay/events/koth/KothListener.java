package net.frozenorb.foxtrot.gameplay.events.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class KothListener implements Listener {

    private final KothManager manager;

    public KothListener(KothManager manager) {
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
                e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

        this.checkMove(e.getPlayer(), e.getFrom(), e.getTo());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
                e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

        this.checkMove(e.getPlayer(), e.getFrom(), e.getTo());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Location location = e.getPlayer().getLocation();
        this.checkMove(e.getPlayer(), location, location);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Koth koth = manager.getZone(player.getLocation());

        if (koth != null && koth.isActive() && koth.getCapturing() == player) {
            koth.setCapturing(null);
            koth.setRemaining(koth.getMinutes());
            koth.getOnCap().remove(player);
        }
    }

    private void checkMove(Player player, Location from, Location to) {
        Koth kothFrom = manager.getZone(from);
        Koth kothTo = manager.getZone(to);

        if (kothFrom != null && kothTo == null) {
            if (kothFrom.getCapturing() == player) {
                if (kothFrom.isActive()) {
                    kothFrom.checkLostCapMessage(player);
                }

                kothFrom.setCapturing(null);
                kothFrom.setRemaining(kothFrom.getMinutes());
            }

            kothFrom.getOnCap().remove(player);

            if (kothFrom.isActive() && kothFrom.getOnCap().isEmpty() && kothFrom.getBukkitTask() != null) {
                kothFrom.getBukkitTask().cancel();
                kothFrom.setBukkitTask(null);
            }
            return;
        }

        if (kothTo != null) {
            if (player.hasMetadata("invisible")) return;
            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) return;

            if (kothTo.getCapturing() == null) {
                kothTo.setCapturing(player);

                if (kothTo.getBukkitTask() == null) {
                    kothTo.setBukkitTask(new KothTickTask(kothTo));
                }

                if (kothTo.isActive()) {
                    player.sendMessage(CC.translate("&6[KingOfTheHill] &eAttempting to control &9" + kothTo.getName() + "&e."));
                }
            }

            if (!kothTo.getOnCap().contains(player)) {
                kothTo.getOnCap().add(player);
            }
        }
    }
}
