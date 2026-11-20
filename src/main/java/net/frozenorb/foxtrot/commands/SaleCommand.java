package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaleCommand {

    @Command(names = { "salestart", "sale start" }, permission = "foxtrot.command.startsale")
    public static void saleStart(CommandSender sender, @Parameter(name = "time") long time) {
        if (time < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time");
            return;
        }

        CustomTimerCreateCommand.getCustomTimers().put("&b&ki&9&c&l3x1 Keys&b&ki", System.currentTimeMillis() + time);
        CustomTimerCreateCommand.getCustomTimers().put("&e&ki&9&b&l3x1 Airdrops&e&ki",
                System.currentTimeMillis() + time);
        CustomTimerCreateCommand.getCustomTimers().put("&b&ki&9&e&l3x1 Perk Key&b&ki",
                System.currentTimeMillis() + time);
        CustomTimerCreateCommand.getCustomTimers().put("&b&ki&9&4&l3x Tokens&b&ki", System.currentTimeMillis() + time);

        sender.sendMessage(ChatColor.GREEN + "Started the sale custom timers");
    }

    @Command(names = { "salestop", "sale end" }, permission = "foxtrot.command.endsale")
    public static void saleStop(CommandSender sender) {
        CustomTimerCreateCommand.getCustomTimers().remove("&b&ki&9&c&l3x1 Keys&b&ki");
        CustomTimerCreateCommand.getCustomTimers().remove("&e&ki&9&b&l3x1 Airdrops&e&ki");
        CustomTimerCreateCommand.getCustomTimers().remove("&b&ki&9&e&l3x1 Perk Key&b&ki");
        CustomTimerCreateCommand.getCustomTimers().remove("&b&ki&9&4&l3x Tokens&b&ki");

        sender.sendMessage(ChatColor.RED + "Stopped the sale custom timers");
    }
}
