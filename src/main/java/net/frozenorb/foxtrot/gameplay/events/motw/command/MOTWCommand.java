package net.frozenorb.foxtrot.gameplay.events.motw.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MOTWCommand {

    @Command(names = {"motw start"}, permission = "op")
    public static void start(CommandSender sender, @Parameter(name = "minutes") int minutes) {
        if (Foxtrot.getInstance().getMotwManager().isActive()) {
            sender.sendMessage(CC.translate("&cMOTW is already active!"));
            return;
        }

        Foxtrot.getInstance().getMotwManager().start((60 * 1000L) * minutes);
        sender.sendMessage(CC.translate("&aMOTW started for " + minutes + " minutes."));
    }

    @Command(names = {"motw stop"}, permission = "op")
    public static void stop(CommandSender sender) {
        if (!Foxtrot.getInstance().getMotwManager().isActive()) {
            sender.sendMessage(CC.translate("&cMOTW is not active!"));
            return;
        }

        Foxtrot.getInstance().getMotwManager().stop();
        sender.sendMessage(CC.translate("&aMOTW stopped."));
    }

    @Command(names = {"motw fly"}, permission = "")
    public static void fly(Player player) {
        Foxtrot.getInstance().getMotwManager().toggleFly(player);
    }

    @Command(names = {"motw enable"}, permission = "")
    public static void enable(Player player) {
        if (!Foxtrot.getInstance().getMotwManager().isActive()) {
            player.sendMessage(CC.translate("&cMOTW is not active."));
            return;
        }

        if (Foxtrot.getInstance().getMotwManager().hasEnabled(player)) {
            player.sendMessage(CC.translate("&cYou have already enabled PvP."));
            return;
        }

        Foxtrot.getInstance().getMotwManager().enablePvP(player);
        player.sendMessage(CC.translate("&aYou have enabled PvP for the remainder of MOTW. Your flight has been disabled."));
    }
}
