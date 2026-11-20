package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import net.frozenorb.foxtrot.server.voucher.command.VoucherCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyVoucherCommand {

    @Command(names = { "moneyvoucher give", "mv give" }, permission = "command.moneyvoucher.give", hidden = true)
    public static void give(CommandSender sender, @Parameter(name = "target") Player target,
            @Parameter(name = "amount") double amount) {

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than 0.");
            return;
        }

        String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(amount);
        String displayName = "&2$&a" + formattedAmount + " &6&lMoney Voucher";

        String templateId = "money_" + (int) amount;
        VoucherTemplate template = Foxtrot.getInstance().getVoucherHandler().fromName(templateId).orElseGet(() -> {
            VoucherTemplate t = new VoucherTemplate(templateId);
            t.setCategory("MONEY");
            t.setValue(String.valueOf((int) amount));
            t.setDisplayName(displayName);
            t.save();
            Foxtrot.getInstance().getVoucherHandler().getTemplates().add(t);
            return t;
        });

        ItemStack item = VoucherCommand.createPhysicalVoucher(template);

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItem(target.getLocation(), item);
        } else {
            target.getInventory().addItem(item);
            target.updateInventory();
        }

        sender.sendMessage(ChatColor.GREEN + "Gave a " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + formattedAmount
                + " Money Voucher" + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName() + ChatColor.GREEN
                + ".");
        target.sendMessage(ChatColor.GREEN + "You have received a " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN
                + formattedAmount
                + " Money Voucher" + ChatColor.GREEN + "! Right-click to redeem.");
    }

    @Command(names = { "moneyvoucher help", "mov help" }, permission = "command.moneyvoucher.give", hidden = true)
    public static void help(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Money Voucher Commands");
        sender.sendMessage(
                ChatColor.YELLOW + "/moneyvoucher give <player> <amount>" + ChatColor.GRAY + " - Give a money voucher");
    }
}
