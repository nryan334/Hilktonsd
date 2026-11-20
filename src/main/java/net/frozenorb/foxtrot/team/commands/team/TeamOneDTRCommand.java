package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.menu.ConfirmMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.universe.UniverseAPI;

public class TeamOneDTRCommand {

    @Command(names = {"f 1dtr", "t 1dtr", "team 1dtr", "faction 1dtr"}, permission = "")
    public static void oneDTR(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only team captains or higher can do this.");
            return;
        }

        new ConfirmMenu("Set DTR to 0.1?", (b) -> {
            if (b) {
                team.setDTR(0.1);
                team.setDTRCooldown(System.currentTimeMillis() + (25L * 60L * 1000L));
                sender.sendMessage(ChatColor.YELLOW + "You have set the DTR of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " to 0.1 and frozen it for 25 minutes.");
                Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                        NeutronConstants.MANAGER_PERMISSION,
                        ChatColor.LIGHT_PURPLE + "[MC]" + ChatColor.LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] "
                                + ChatColor.WHITE + sender.getName() + ChatColor.GRAY + " has set the DTR of "
                                + ChatColor.WHITE + team.getName() + ChatColor.GRAY + " to " + ChatColor.WHITE + "0.1" + ChatColor.GRAY + "."
                ));
            } else {
                sender.sendMessage(ChatColor.RED + "Action cancelled.");
            }
        }).openMenu(sender);
    }
}
