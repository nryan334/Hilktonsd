package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.map.menu.MapEditorMenu;
import org.bukkit.entity.Player;

public class HiltonMapEditorCommand {

    @Command(names = { "hilton map editor", "map editor" }, permission = "hilton.mapeditor")
    public static void execute(Player sender) {
        new MapEditorMenu().openMenu(sender);
    }
}
