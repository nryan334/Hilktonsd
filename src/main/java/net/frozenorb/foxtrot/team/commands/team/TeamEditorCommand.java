package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.team.menu.system.SystemEditorMenu;
import org.bukkit.entity.Player;

public class TeamEditorCommand {

    @Command(names = {"team editor", "t editor", "f editor", "faction editor", "fac editor"}, permission = "foxtrot.command.editor")
    public static void teamEditor(Player sender) {
        new SystemEditorMenu().openMenu(sender);
    }

}
