package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Category;
import org.cavepvp.suge.kit.menu.KitCategoryMenu;

public class FreeKitsCommand {

    @Command(names = {"freekits"}, permission = "")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(ChatColor.RED + "This command is disabled on Kitmap!");
            return;
        }

        Category category = Suge.getInstance().getCategoryHandler().getCategory("FREE");
        
        if (category == null) {
            player.sendMessage(ChatColor.RED + "The FREE category has not been created yet!");
            return;
        }

        new KitCategoryMenu(category).openMenu(player);
    }

}
