package net.frozenorb.foxtrot.server.voucher.command;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VoucherTemplateParameter implements ParameterType<VoucherTemplate> {

    @Override
    public VoucherTemplate transform(CommandSender sender, String source) {
        VoucherTemplate template = Foxtrot.getInstance().getVoucherHandler().fromName(source).orElse(null);
        if (template == null) {
            sender.sendMessage(ChatColor.RED + "No voucher with ID '" + source + "' found.");
            return null;
        }

        return template;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return Foxtrot.getInstance().getVoucherHandler().getTemplates().stream()
                .map(VoucherTemplate::getId)
                .filter(id -> id.toLowerCase().startsWith(source.toLowerCase()))
                .collect(Collectors.toList());
    }
}
