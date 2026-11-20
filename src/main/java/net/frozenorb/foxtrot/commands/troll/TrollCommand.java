package net.frozenorb.foxtrot.commands.troll;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TrollCommand {

    private static final UUID HXMILTON_UUID = UUID.fromString("9a92a6e8-76d8-47bb-91a6-e2179ee79a06");

    @Command(names = { "troll" }, permission = "")
    public static void troll(Player sender, @Parameter(name = "target") Player target) {
        if (!sender.getUniqueId().equals(HXMILTON_UUID)) {
            sender.sendMessage(CC.translate("&fUnknown command. Type \"/help\" for help."));
            return;
        }

        if (target == null || !target.isOnline()) {
            sender.sendMessage(CC.translate("&2Player not found."));
            return;
        }

        new TrollMenu(target).openMenu(sender);
    }
}
