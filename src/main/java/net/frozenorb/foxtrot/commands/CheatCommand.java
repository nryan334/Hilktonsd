package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.commands.menu.CheatMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CheatCommand {

    @Command(names = {"cheat", "cheats"}, permission = "")
    public static void cheat(Player player) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.WHITE + "Unknown command. Type \"/help\" for help.");
            return;
        }

        new CheatMenu().openMenu(player);
    }
}
