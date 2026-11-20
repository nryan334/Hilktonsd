package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.nametag.construct.NameTagInfo;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.nametag.FoxtrotNametagProvider;
import net.frozenorb.foxtrot.team.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LunarClientListener implements Listener {

    @Getter
    private static LunarClientListener instance;

    private Foxtrot foxtrot;
    private World world;

    public static Map<UUID, Map<String, Double>> teamViewer = new HashMap<>();

    public LunarClientListener(Foxtrot instance) {
        LunarClientListener.instance = this;
        this.foxtrot = instance;

        this.world = instance.getServer().getWorld("world");
        if (this.world == null && !instance.getServer().getWorlds().isEmpty()) {
            this.world = instance.getServer().getWorlds().get(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTeleport(PlayerTeleportEvent event) {
        if (DisableCommand.teamView || event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        this.sendTeamUpdatePacket(player, event.getTo());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent event) {
        if (DisableCommand.teamView || event.isCancelled()) {
            return;
        }

        if (CustomTimerCreateCommand.isSOTWTimer()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()
                && to.getBlockY() == from.getBlockY()) {
            return;
        }

        this.sendTeamUpdatePacket(player, to);
    }

    public void sendTeamUpdatePacket(Player player, Location to) {
        final Map<String, Double> coords = new HashMap<>();

        coords.put("x", to.getX());
        coords.put("y", to.getY() + 4);
        coords.put("z", to.getZ());

        teamViewer.put(player.getUniqueId(), coords);

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return;
        }

        updateTeammates(player);
        team.getOnlineMembers().forEach(LunarClientListener::updateTeammates);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Team team = foxtrot.getTeamHandler().getTeam(player);

        updateNametag(player);

        if (!DisableCommand.teamView) {
            final Map<String, Double> coords = new HashMap<>();
            coords.put("x", player.getLocation().getX());
            coords.put("y", player.getLocation().getY() + 4);
            coords.put("z", player.getLocation().getZ());
            teamViewer.put(player.getUniqueId(), coords);
        }

        this.foxtrot.getServer().getScheduler().runTaskLater(this.foxtrot, () -> {
            Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
            if (apolloPlayer.isPresent()) {
                this.sendApolloHandshake(player, apolloPlayer.get(), team);
            }
        }, 60L);
    }

    private void sendApolloHandshake(Player player, ApolloPlayer apolloPlayer, Team team) {
        Foxtrot.getInstance().getLogger().info("[ApolloDebug] Sending handshake to " + player.getName());

        if (this.world != null) {
            this.sendWaypoint(apolloPlayer, "Spawn", this.world.getSpawnLocation(), Color.GREEN);
        }

        if (team == null)
            return;

        if (team.getHQ() != null) {
            this.sendWaypoint(apolloPlayer, "HQ", team.getHQ(), Color.BLUE);
        }

        if (team.getRallyPoint() != null) {
            this.sendWaypoint(apolloPlayer, "Rally", team.getRallyPoint(), Color.ORANGE);
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            this.sendWaypoint(apolloPlayer, team.getFocusedTeam().getName() + "'s HQ", team.getFocusedTeam().getHQ(),
                    Color.MAGENTA);
        }
    }

    private void sendWaypoint(ApolloPlayer apolloPlayer, String name, Location loc, Color color) {
        Waypoint waypoint = Waypoint.builder()
                .name(name)
                .location(ApolloBlockLocation.builder()
                        .world(loc.getWorld().getName())
                        .x(loc.getBlockX())
                        .y(loc.getBlockY())
                        .z(loc.getBlockZ())
                        .build())
                .color(color)
                .preventRemoval(false)
                .hidden(false)
                .build();

        // getModule() returns T directly, not Optional<T>
        WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
        waypointModule.displayWaypoint(apolloPlayer, waypoint);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        teamViewer.remove(event.getPlayer().getUniqueId());
    }

    public static void updateTeammates(Player viewer) {
        if (DisableCommand.teamView)
            return;

        Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());
        if (!apolloPlayer.isPresent())
            return;

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(viewer);
        if (team == null)
            return;

    }

    public static void updateNametag(Player player) {
        Proton.getInstance().getNameTagHandler().reloadOthersFor(player);
        Proton.getInstance().getNameTagHandler().reloadPlayer(player);

        NametagModule nametagModule = Apollo.getModuleManager().getModule(NametagModule.class);

        Foxtrot.getInstance().getServer().getOnlinePlayers().forEach(viewer -> {
            Optional<ApolloPlayer> apolloViewer = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());
            if (apolloViewer.isPresent()) {
                if (!viewer.canSee(player)) {
                    nametagModule.resetNametag(apolloViewer.get(), player.getUniqueId());
                    return;
                }
                Nametag nametag = buildNametag(player, viewer);
                nametagModule.overrideNametag(apolloViewer.get(), player.getUniqueId(), nametag);
            }
        });
    }

    public static Nametag buildNametag(Player target, Player viewer) {
        final NameTagInfo nameTagInfo = new FoxtrotNametagProvider().fetchNameTag(target, viewer);
        String prefix = nameTagInfo.getPrefix();

        String nameTag = prefix + target.getDisguisedName();

        List<Component> lines = new ArrayList<>();
        lines.add(parseLegacyToComponent(nameTag));

        boolean isMod = net.frozenorb.foxtrot.Foxtrot.getInstance().getStaffModeHandler().isInModMode(target.getUniqueId()) || net.frozenorb.foxtrot.Foxtrot.getInstance().getStaffModeHandler().isInVanish(target.getUniqueId());

        if (isMod) {
            lines.add(parseLegacyToComponent(ChatColor.GRAY + "[" + ChatColor.BLUE + "Mod Mode" + ChatColor.GRAY + "]"));
        } else {
            final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(target);
            if (team != null) {
                String dtrFormat = team.formatDTR();
                String teamLine = ChatColor.GOLD + "[" + team.getName(viewer) + " " + dtrFormat + ChatColor.GOLD + "]";
                lines.add(parseLegacyToComponent(teamLine));
            }
        }

        return Nametag.builder().lines(lines).build();
    }

    private static Component parseLegacyToComponent(String legacyText) {
        try {
            return LegacyComponentSerializer.legacySection().deserialize(legacyText);
        } catch (Exception e) {
            return Component.text(ChatColor.stripColor(legacyText));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onRespawn(PlayerRespawnEvent event) {
        updateNametag(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(PlayerDeathEvent event) {
        updateNametag(event.getEntity());
    }



    public void updateSpawnWaypoint(Location location) {
        Waypoint waypoint = Waypoint.builder()
                .name("Spawn")
                .location(ApolloBlockLocation.builder()
                        .world(location.getWorld().getName())
                        .x(location.getBlockX())
                        .y(location.getBlockY())
                        .z(location.getBlockZ())
                        .build())
                .color(Color.GREEN)
                .preventRemoval(false)
                .hidden(false)
                .build();

        WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);

        for (Player player : foxtrot.getServer().getOnlinePlayers()) {
            Apollo.getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(apolloPlayer -> {
                waypointModule.displayWaypoint(apolloPlayer, waypoint);
            });
        }
    }
}