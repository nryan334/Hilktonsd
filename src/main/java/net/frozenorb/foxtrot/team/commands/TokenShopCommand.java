package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.team.menu.TokenShopMenu;
import org.bukkit.entity.Player;

public class TokenShopCommand {

    @Command(names = { "tokenshop", "tokenstore" }, permission = "")
    public static void execute(Player player) {
        new TokenShopMenu().openMenu(player);
    }

    @Command(names = { "tokenshop editor", "tokenstore editor" }, permission = "")
    public static void editor(Player player) {
        if (!player.isOp() && !player.hasPermission("foxtrot.tokenshop.admin")) {
            player.sendMessage(org.bukkit.ChatColor.RED + "No permission.");
            return;
        }
        new net.frozenorb.foxtrot.gameplay.tokenshop.menu.editor.TokenShopEditorMenu().openMenu(player);
    }
}
