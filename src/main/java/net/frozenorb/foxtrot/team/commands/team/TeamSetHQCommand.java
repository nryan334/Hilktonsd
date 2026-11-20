package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class TeamSetHQCommand {

    @Command(names = { "team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome",
            "f sethome", "faction sethome", "fac sethome", "sethome", "sethq" }, permission = "")
    public static void teamSetHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())
                || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your faction's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC
                            + "Setting HQ outside of your faction's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }

            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");

            Location loc = team.getHQ();
            Waypoint hqWp = Waypoint.builder()
                    .name("HQ")
                    .location(ApolloBlockLocation.builder()
                            .world(loc.getWorld().getName())
                            .x(loc.getBlockX())
                            .y(loc.getBlockY())
                            .z(loc.getBlockZ())
                            .build())
                    .color(Color.BLUE)
                    .preventRemoval(false)
                    .hidden(false)
                    .build();
            team.setHomeWaypoint(hqWp);

            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
            for (Player member : team.getOnlineMembers()) {
                Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(member.getUniqueId());
                if (apolloPlayer.isPresent()) {
                    waypointModule.removeWaypoint(apolloPlayer.get(), "HQ");
                    waypointModule.displayWaypoint(apolloPlayer.get(), hqWp);
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
        }
    }
}