package net.frozenorb.foxtrot.gameplay.loot.partnercrate;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.PartnerCrateMenu;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PartnerCrateCommand {

    @Command(names = {"partnercrate"}, permission = "")
    public static void execute(Player player) {
        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You must be at " + ChatColor.GREEN + "Spawn" + ChatColor.RED + " to use this!");
            return;
        }

        new PartnerCrateMenu().openMenu(player);
    }

    public static boolean isSimilar(PartnerType partnerType, ItemStack itemStack) {

        if (itemStack == null || itemStack.getType() != Material.TRIPWIRE_HOOK || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null || itemStack.getItemMeta().getLore() == null) {
            return false;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.getDisplayName().equalsIgnoreCase(partnerType.getChatColor() + ChatColor.BOLD.toString() + partnerType.getCrateName() + " Key")) {
            return false;
        }

        return itemMeta.getLore().size() == 1 && ChatColor.stripColor(itemMeta.getLore().get(0)).equalsIgnoreCase("Right click a " + partnerType.getCrateName() + " Crate to receive rewards!");
    }

//    public static boolean isSimilar(PartnerType partnerType, ItemStack itemStack) {
//
//        if (itemStack == null || itemStack.getType() != Material.TRIPWIRE_HOOK || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null || itemStack.getItemMeta().getLore() == null) {
//            return false;
//        }
//
//        final ItemMeta itemMeta = itemStack.getItemMeta();
//
//        if (!itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&d&lPartner Key"))) {
//            return false;
//        }
//
//        return itemMeta.getLore().size() == 1 && ChatColor.stripColor(itemMeta.getLore().get(0)).equalsIgnoreCase("Right click a Partner Crate to receive rewards!");
//    }
}
