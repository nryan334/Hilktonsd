package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KillTheKingCommand {

    @Command(names = { "ktk", "killtheking", "king start" }, permission = "command.ktk")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getKingManager().isActive()) {
            Foxtrot.getInstance().getKingManager().stopKing(true);
            return;
        }

        Foxtrot.getInstance().getKingManager().startKing(player);
        player.sendMessage(ChatColor.GREEN + "Set yourself as the king!");
    }

    @Command(names = { "king stop" }, permission = "command.ktk")
    public static void stop(Player player) {
        if (!Foxtrot.getInstance().getKingManager().isActive()) {
            player.sendMessage(ChatColor.RED + "There is no active King Event.");
            return;
        }
        
        Foxtrot.getInstance().getKingManager().stopKing(true);
    }
}
