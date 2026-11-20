package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.universe.UniverseAPI;

public class ForceDisbandCommand {

    @Command(names={ "f forcedisband", "t forcedisband", "team forcedisband", "faction forcedisband", "forcedisband" }, permission="foxtrot.forcedisband")
    public static void forceDisband(Player sender, @Parameter(name="team") Team team) {
        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        team.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.LIGHT_PURPLE + "[MC]" + ChatColor.LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] "
                        + ChatColor.WHITE + sender.getName() + ChatColor.GRAY + " has forcefully disbanded "
                        + ChatColor.WHITE + team.getName() + ChatColor.GRAY + "."
        ));
    }

}