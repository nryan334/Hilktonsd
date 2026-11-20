package net.frozenorb.foxtrot.gameplay.events.motw;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class MOTWManager {

    private long startTime;
    private long durationMs;
    private final Set<UUID> flyingPlayers = new HashSet<>();
    private final Set<UUID> enabledPlayers = new HashSet<>();

    public MOTWManager() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new MOTWListener(this),
                Foxtrot.getInstance());
    }

    public void start(long durationMs) {
        this.startTime = System.currentTimeMillis();
        this.durationMs = durationMs;
        this.flyingPlayers.clear();
        this.enabledPlayers.clear();

        Foxtrot.getInstance().getReclaimMap().wipe();

        Bukkit.broadcastMessage(CC.translate(
                "&d&l[MOTW] &eMap of the Week has started! Reclaims have been reset. Type /motw fly to fly."));
    }

    public void stop() {
        this.startTime = 0L;
        this.durationMs = 0L;

        for (UUID uuid : flyingPlayers) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.setAllowFlight(false);
                p.setFlying(false);
            }
        }

        this.flyingPlayers.clear();
        this.enabledPlayers.clear();

        Bukkit.broadcastMessage(
                CC.translate("&d&l[MOTW] &eMap of the Week has ended! PvP is now enabled and flight is disabled."));
    }

    public boolean isActive() {
        if (startTime == 0L)
            return false;

        if (System.currentTimeMillis() - startTime > durationMs) {
            stop();
            return false;
        }

        return true;
    }

    public long getRemainingTime() {
        return (startTime + durationMs) - System.currentTimeMillis();
    }

    public void toggleFly(Player player) {
        if (!isActive()) {
            player.sendMessage(CC.translate("&cMOTW is not active."));
            return;
        }

        if (flyingPlayers.contains(player.getUniqueId())) {
            flyingPlayers.remove(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(CC.translate("&eMOTW Fly disabled."));
        } else {
            flyingPlayers.add(player.getUniqueId());
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(CC.translate("&eMOTW Fly enabled."));
        }
    }

    public boolean hasEnabled(Player player) {
        return enabledPlayers.contains(player.getUniqueId());
    }

    public void enablePvP(Player player) {
        enabledPlayers.add(player.getUniqueId());

        if (flyingPlayers.contains(player.getUniqueId())) {
            flyingPlayers.remove(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(CC.translate("&6MOTW Fly &edisabled because you enabled PvP."));
        }
    }
}
