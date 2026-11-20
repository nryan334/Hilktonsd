package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GemFlipCommand {

    @Command(names = { "gemflip" }, permission = "")
    public static void execute(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cGem Flips have been disabled"));
    }

}
