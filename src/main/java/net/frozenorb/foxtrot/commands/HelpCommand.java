package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.map.menu.HelpMenu;
import org.bukkit.entity.Player;

public class HelpCommand {

    @Command(names = { "help", "menu", "soulhelp" }, permission = "")
    public static void execute(Player player) {
        new HelpMenu().openMenu(player);
    }

}
