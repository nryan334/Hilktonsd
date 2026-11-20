package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamSystemCommand {

    @Command(names = {"team system", "t system", "f system", "faction system", "fac system"}, permission = "op")
    public static void teamSystem(Player sender, @Parameter(name = "team") Team team) {
        for (UUID member : team.getMembers()) {
            Foxtrot.getInstance().getTeamHandler().setTeam(member, null);
        }

        team.setOwner(null);
        team.getMembers().clear();
        team.getCaptains().clear();
        team.getColeaders().clear();

        sender.sendMessage(ChatColor.GOLD + "The faction " + ChatColor.WHITE + team.getName() + ChatColor.GOLD + " is now a system faction.");
    }

}
