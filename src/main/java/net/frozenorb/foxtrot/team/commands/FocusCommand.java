package net.frozenorb.foxtrot.team.commands;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.StaffUtilsListener;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.Proton;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public class FocusCommand {

    @Command(names = { "focus" }, description = "Focus a player", permission = "")
    public static void focus(Player sender, @Parameter(name = "player") Player target) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your faction.");
            return;
        }

        senderTeam.setFocused(target.getUniqueId());
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by "
                + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                LunarClientListener.updateNametag(onlinePlayer);
            }
        }
    }

    @Command(names = "unfocus", description = "Remove player focus", permission = "")
    public static void unfocus(Player sender) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        UUID focused = senderTeam.getFocused();
        if (focused == null) {
            sender.sendMessage(ChatColor.RED + "You are not focusing anyone!");
            return;
        }

        senderTeam.setFocused(null);
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + Proton.getInstance().getUuidCache().name(focused)
                + ChatColor.YELLOW + " has been unfocused by " + ChatColor.LIGHT_PURPLE + sender.getName()
                + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                LunarClientListener.updateNametag(onlinePlayer);
            }
        }
    }

    @Command(names = { "team focus", "t focus", "f focus", "faction focus", "fac focus" }, permission = "")
    public static void execute(Player player, @Parameter(name = "team") String focusString) {

        final Player target = Foxtrot.getInstance().getServer().getPlayer(focusString);
        Team focusTeam;

        if (target != null) {
            focusTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);
        } else {
            focusTeam = Foxtrot.getInstance().getTeamHandler().getTeam(focusString);
        }

        if (focusTeam == null && focusString.equalsIgnoreCase("koth")
                && !Foxtrot.getInstance().getKothManager().getActiveKoths().isEmpty()) {
            player.chat("/f focus " + Foxtrot.getInstance().getKothManager().getActiveKoths().get(0).getName());
            return;
        }

        if (focusTeam == null && StaffUtilsListener.lastPlayerHit.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "No team or member with the name " + focusString
                    + " found. Attempting to focus the last player you hit...");
            focusTeam = Foxtrot.getInstance().getTeamHandler()
                    .getTeam(StaffUtilsListener.lastPlayerHit.get(player.getUniqueId()));
        }

        if (focusTeam == null) {
            player.sendMessage(ChatColor.RED + "No team or member with the name " + focusString + " found.");
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (focusTeam == team) {
            player.sendMessage(ChatColor.RED + "You can't focus yourself!");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null && team.getFocusWaypoint() != null) {
            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
            for (Player it : team.getOnlineMembers()) {
                Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(it.getUniqueId());
                if (apolloPlayer.isPresent()) {
                    waypointModule.removeWaypoint(apolloPlayer.get(), team.getFocusWaypoint());
                }
            }
        }

        final Team passFocusedTeam = team.getFocusedTeam();
        team.setFocusedTeam(focusTeam == team.getFocusedTeam() ? null : focusTeam);

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            final Location location = team.getFocusedTeam().getHQ();
            Waypoint focusWp = Waypoint.builder()
                    .name(ChatColor.RED + team.getFocusedTeam().getName() + "'s HQ")
                    .location(ApolloBlockLocation.builder()
                            .world(location.getWorld().getName())
                            .x(location.getBlockX())
                            .y(location.getBlockY())
                            .z(location.getBlockZ())
                            .build())
                    .color(Color.RED)
                    .preventRemoval(true)
                    .hidden(false)
                    .build();
            team.setFocusWaypoint(focusWp);

            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
            for (Player it : team.getOnlineMembers()) {
                Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(it.getUniqueId());
                if (apolloPlayer.isPresent()) {
                    waypointModule.displayWaypoint(apolloPlayer.get(), focusWp);
                }
            }
        }

        if (passFocusedTeam != null) {
            passFocusedTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);
        }

        focusTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);

        team.sendMessage(ChatColor.YELLOW + "Team " + ChatColor.LIGHT_PURPLE + focusTeam.getName() + ChatColor.YELLOW
                + " has been " + (team.getFocusedTeam() == null ? "unfocused" : "focused") + " by "
                + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + ".");
    }

    @Command(names = { "team unfocus", "t unfocus", "f unfocus", "faction unfocus", "fac unfocus" }, permission = "")
    public static void unfocusTeam(Player player) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        final Team focusedTeam = team.getFocusedTeam();

        if (focusedTeam == null) {
            player.sendMessage(ChatColor.RED + "You don't have any teams focused!");
            return;
        }

        if (focusedTeam.getHQ() != null && team.getFocusWaypoint() != null) {
            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
            for (Player it : team.getOnlineMembers()) {
                Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(it.getUniqueId());
                if (apolloPlayer.isPresent()) {
                    waypointModule.removeWaypoint(apolloPlayer.get(), team.getFocusWaypoint());
                }
            }
        }

        team.setFocusedTeam(null);

        focusedTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);

        team.sendMessage(ChatColor.YELLOW + "Team " + ChatColor.LIGHT_PURPLE + focusedTeam.getName() + ChatColor.YELLOW
                + " has been unfocused by " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + ".");
    }
}