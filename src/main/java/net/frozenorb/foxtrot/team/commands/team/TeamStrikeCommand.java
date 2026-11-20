package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamStrikeCommand {

    @Command(names={ "team strike", "t strike", "f strike", "faction strike", "fac strike" }, permission="foxtrot.strike")
    public static void teamStrike(CommandSender sender, @Parameter(name = "team") final Team team, @Parameter(name = "amount") int amount, @Parameter(name = "reason", wildcard = true) String reason) {
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive!");
            return;
        }

        team.setStrikes(team.getStrikes() + amount);
        team.flagForSave();

        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "★ FACTION STRIKE ★");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "Faction " + ChatColor.YELLOW + team.getName() + ChatColor.RED + " has received " + ChatColor.YELLOW + amount + ChatColor.RED + " strike(s).");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "Reason: " + ChatColor.WHITE + reason);
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "Total Strikes: " + ChatColor.YELLOW + team.getStrikes() + "/3");

        if (team.getStrikes() >= 3) {
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Faction " + team.getName() + " has been disqualified due to 3 or more strikes!");
        }
    }

    @Command(names={ "team unstrike", "t unstrike", "f unstrike", "faction unstrike", "fac unstrike" }, permission="foxtrot.strike")
    public static void teamUnstrike(CommandSender sender, @Parameter(name = "team") final Team team, @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive!");
            return;
        }

        int current = team.getStrikes();
        int next = Math.max(0, current - amount);
        team.setStrikes(next);
        team.flagForSave();

        sender.sendMessage(ChatColor.GREEN + "You removed " + amount + " strike(s) from " + team.getName() + ". Total strikes: " + next + "/3");
    }

    @Command(names={ "team strikes", "t strikes", "f strikes", "faction strikes", "fac strikes" }, permission = "")
    public static void teamStrikes(CommandSender sender, @Parameter(name = "team") final Team team) {
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Faction " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + " currently has " + ChatColor.RED + team.getStrikes() + "/3" + ChatColor.YELLOW + " strikes.");
    }
}
