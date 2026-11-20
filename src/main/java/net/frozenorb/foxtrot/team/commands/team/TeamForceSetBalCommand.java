package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.universe.UniverseAPI;

public class TeamForceSetBalCommand {

    @Command(names = {"f forcesetbal", "t forcesetbal", "team forcesetbal", "faction forcesetbal"}, permission = "foxtrot.forcesetbal")
    public static void forceSetBal(Player sender, @Parameter(name = "team") Team team, @Parameter(name = "balance") double balance) {
        team.setBalance(balance);
        sender.sendMessage(ChatColor.YELLOW + "You set the balance of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " to " + ChatColor.LIGHT_PURPLE + "$" + balance + ChatColor.YELLOW + ".");
        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.LIGHT_PURPLE + "[MC]" + ChatColor.LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] "
                        + ChatColor.WHITE + sender.getName() + ChatColor.GRAY + " has set the balance of "
                        + ChatColor.WHITE + team.getName() + ChatColor.GRAY + " to " + ChatColor.WHITE + "$" + balance + ChatColor.GRAY + "."
        ));
    }
}
