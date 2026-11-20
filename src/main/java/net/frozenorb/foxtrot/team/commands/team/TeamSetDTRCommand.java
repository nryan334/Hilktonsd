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

public class TeamSetDTRCommand {

    @Command(names = {"f setdtr", "t setdtr", "team setdtr", "faction setdtr"}, permission = "foxtrot.setdtr")
    public static void setDTR(Player sender, @Parameter(name = "team") Team team, @Parameter(name = "dtr") float dtr) {
        team.setDTR(dtr);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " has had its DTR set to " + ChatColor.LIGHT_PURPLE + dtr + ChatColor.YELLOW + ".");
        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.LIGHT_PURPLE + "[MC]" + ChatColor.LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] "
                        + ChatColor.WHITE + sender.getName() + ChatColor.GRAY + " has set the DTR of "
                        + ChatColor.WHITE + team.getName() + ChatColor.GRAY + " to " + ChatColor.WHITE + dtr + ChatColor.GRAY + "."
        ));
    }
}
