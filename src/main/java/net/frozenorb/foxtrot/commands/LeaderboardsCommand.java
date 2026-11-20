package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.commands.menu.LeaderboardMenu;
import org.bukkit.entity.Player;

public class LeaderboardsCommand {

    @Command(names = {"leaderboards", "leaderboard"}, permission = "")
    public static void leaderboards(Player player) {
        new LeaderboardMenu().openMenu(player);
    }
}
