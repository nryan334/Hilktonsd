package net.frozenorb.foxtrot.gameplay.events.purge.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.purge.PurgeManager;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class PurgeCommand {

    @Command(names = { "purge start" }, permission = "hilton.purge.admin")
    public static void start(CommandSender sender, @Parameter(name = "minutes") int minutes) {
        if (PurgeManager.isPurgeActive()) {
            sender.sendMessage(CC.translate("&cPurge is already active!"));
            return;
        }

        PurgeManager.startPurge((60 * 1000L) * minutes);
        Bukkit.broadcastMessage(CC.translate("&4&l[Purge] &eThe Purge has started for &c" + minutes + " minutes&e!"));
        Bukkit.broadcastMessage(CC.translate("&4&l[Purge] &eYou can now interact with blocks in enemy territory!"));
    }

    @Command(names = { "purge stop" }, permission = "op")
    public static void stop(CommandSender sender) {
        if (!PurgeManager.isPurgeActive()) {
            sender.sendMessage(CC.translate("&cPurge is not active!"));
            return;
        }

        PurgeManager.stopPurge();
        Bukkit.broadcastMessage(CC.translate("&4&l[Purge] &eThe Purge has ended!"));
    }
}
