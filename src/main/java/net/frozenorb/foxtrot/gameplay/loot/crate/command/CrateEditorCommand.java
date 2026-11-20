package net.frozenorb.foxtrot.gameplay.loot.crate.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.gameplay.loot.crate.menu.CrateEditorMenu;
import org.bukkit.entity.Player;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CrateEditorCommand {

    @Command(names = {"crates editor", "crate editor"}, permission = "foxtrot.crates.admin")
    public static void editor(Player player) {
        new CrateEditorMenu().openMenu(player);
    }
}
