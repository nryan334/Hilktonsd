package net.frozenorb.foxtrot.gameplay.enchantment;

import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CEAdminCommand {

    @Command(names = {"ce admin"}, permission = "ce.menu.settings")
    public static void execute(Player player) {
        new CEAdminMenu().openMenu(player);
    }
}
