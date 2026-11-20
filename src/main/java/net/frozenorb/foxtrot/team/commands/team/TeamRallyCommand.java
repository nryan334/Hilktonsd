package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Optional;

public class TeamRallyCommand {

    @Command(names = {"team rally", "t r", "t rally", "f rally", "f r", "f tl", "t tl", "faction rally", "faction tl", "faction r", "team tl", "team r"}, permission = "")
    public static void teamRally(Player sender) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        final Location location = sender.getLocation();
        team.setRallyPoint(location);

        Waypoint waypoint = Waypoint.builder()
                .name("Rally")
                .location(ApolloBlockLocation.builder()
                        .world(location.getWorld().getName())
                        .x(location.getBlockX())
                        .y(location.getBlockY())
                        .z(location.getBlockZ())
                        .build())
                .color(Color.ORANGE)
                .preventRemoval(false)
                .hidden(false)
                .build();

        // getModule() returns the module directly (not Optional)
        WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);

        if (waypointModule != null) {
            for (Player onlineMember : team.getOnlineMembers()) {
                Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(onlineMember.getUniqueId());
                if (apolloPlayer.isPresent()) {
                    waypointModule.removeWaypoint(apolloPlayer.get(), "Rally");
                    waypointModule.displayWaypoint(apolloPlayer.get(), waypoint);
                }
            }
        }

        sender.sendMessage(ChatColor.GREEN + "You have updated the team's rally point!");
        team.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f" + sender.getName() + " &chas updated the team's rally point!"));
    }

    @Command(names = {"team unrally", "t unrally", "f unrally", "faction unrally"}, permission = "")
    public static void teamUnRally(Player sender) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getRallyPoint() != null) {
            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);

            for (Player onlineMember : team.getOnlineMembers()) {
                Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(onlineMember.getUniqueId());
                if (apolloPlayer.isPresent()) {
                    waypointModule.removeWaypoint(apolloPlayer.get(), "Rally");
                }
            }
        }

        team.setRallyPoint(null);
        team.sendMessage(ChatColor.RED + sender.getName() + " has removed the team's rally point!");
    }

}
