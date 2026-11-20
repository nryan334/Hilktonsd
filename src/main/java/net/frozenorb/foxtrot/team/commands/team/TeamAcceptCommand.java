package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.util.UUIDUtils;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Role;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeamAcceptCommand {

    @Command(names = { "team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a",
            "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join", "team j", "t j", "f j",
            "faction j", "fac j" }, permission = "", async = true)
    public static void teamAccept(Player sender, @Parameter(name = "team") Team team) {
        if (team.getInvitations().contains(sender.getUniqueId())
                || team.getRoster().containsKey(sender.getUniqueId())) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (DTRHandler.isOnCooldown(team) && !Foxtrot.getInstance().getServerHandler().isPreEOTW()
                    && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            if (team.getMembers().size() >= 15 && Foxtrot.getInstance().getTeamHandler().isRostersLocked()) {
                sender.sendMessage(
                        ChatColor.RED + team.getName() + " cannot be joined: Team rosters are locked server-wide!");
                return;
            }

            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: You are combat tagged!");
                return;
            }

            if (!canJoin(sender, team)
                    && team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
                FullTeamBypassEvent attemptEvent = new FullTeamBypassEvent(sender, team,
                        team.getRoster().containsKey(sender.getUniqueId()));
                Foxtrot.getInstance().getServer().getPluginManager().callEvent(attemptEvent);

                if (!attemptEvent.isAllowBypass()) {
                    sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                    return;
                }
            }

            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);

            if (team.getRoster().containsKey(sender.getUniqueId())) {
                if (team.getRoster().get(sender.getUniqueId()).equals(Role.CAPTAIN)) {
                    team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(sender.getUniqueId())
                            + " has been promoted to Captain!");
                    team.addCaptain(sender.getUniqueId());
                } else if (team.getRoster().get(sender.getUniqueId()).equals(Role.COLEADER)) {
                    team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(sender.getUniqueId())
                            + " has been promoted to Co-Leader!");
                    team.addCoLeader(sender.getUniqueId());
                }
            }

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");

            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
            Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(sender.getUniqueId());

            if (apolloPlayer.isPresent()) {
                if (team.getHQ() != null) {
                    Waypoint hqWp = buildWaypoint("HQ", team.getHQ(), Color.BLUE);
                    team.setHomeWaypoint(hqWp);
                    waypointModule.displayWaypoint(apolloPlayer.get(), hqWp);
                }

                if (team.getRallyPoint() != null) {
                    Waypoint rallyWp = buildWaypoint("Rally", team.getRallyPoint(), Color.ORANGE);
                    team.setRallyWaypoint(rallyWp);
                    waypointModule.displayWaypoint(apolloPlayer.get(), rallyWp);
                }

                if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
                    final Location location = team.getFocusedTeam().getHQ();
                    Waypoint focusWp = buildWaypoint(team.getFocusedTeam().getName() + "'s HQ", location, Color.RED);
                    team.setFocusWaypoint(focusWp);
                    waypointModule.displayWaypoint(apolloPlayer.get(), focusWp);
                }
            }

            LunarClientListener.updateNametag(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }

    private static Waypoint buildWaypoint(String name, Location loc, Color color) {
        return Waypoint.builder()
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
    }

    public static boolean canJoin(Player sender, Team team) {

        if (!team.getRoster().containsKey(sender.getUniqueId())) {
            return false;
        }

        boolean done = false;

        for (UUID offlineMember : team.getOfflineMembers()) {

            if (team.getColeaders().contains(offlineMember) || team.getOwner().equals(offlineMember)
                    || team.getCaptains().contains(offlineMember)) {
                continue;
            }

            if (Foxtrot.getInstance().getServer().getPlayer(offlineMember) != null) {
                continue;
            }

            final Profile profile = Neutron.getInstance().getProfileHandler().fromName(UUIDUtils.name(offlineMember), true, true);

            if (profile == null) {
                continue;
            }

            if (profile.getServerProfile().isOnline()) {
                continue;
            }

            if (profile.getServerProfile().getLastSeen() < TimeUnit.MINUTES.toMillis(30)) {
                continue;
            }

            team.sendMessage(
                    ChatColor.DARK_AQUA + UUIDUtils.name(offlineMember) + " was kicked by " + sender.getName() + "!");

            TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                    "playerId", offlineMember,
                    "kickedById", sender.getUniqueId(),
                    "kickedByName", sender.getName(),
                    "usedForceKick", "false"));

            team.removeMember(offlineMember);
            team.flagForSave();

            Foxtrot.getInstance().getTeamHandler().setTeam(offlineMember, null);
            done = true;
            break;
        }

        return done;
    }

}