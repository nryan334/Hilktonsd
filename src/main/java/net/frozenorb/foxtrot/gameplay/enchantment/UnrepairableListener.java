package net.frozenorb.foxtrot.gameplay.enchantment;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class UnrepairableListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onBookApply(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack bookItem = event.getCursor();
        final ItemStack applyToItem = event.getCurrentItem();

        if (applyToItem == null || bookItem == null || applyToItem.getType() == Material.AIR
                || bookItem.getType() == Material.AIR) {
            return;
        }

        if (!isUnrepairableBook(bookItem)) {
            return;
        }

        if (!isRepairable(applyToItem.getType())) {
            player.sendMessage(ChatColor.RED + "Can't use a book on that item!");
            return;
        }

        final ItemMeta applyToMeta = applyToItem.getItemMeta();
        final List<String> applyToLore = applyToMeta.hasLore() ? applyToMeta.getLore() : new ArrayList<>();

        if (applyToLore.stream().anyMatch(it -> it.contains("Unrepairable"))) {
            player.sendMessage(ChatColor.RED + "You already have that custom enchant on that piece!");
            return;
        }

        applyToLore.add(ChatColor.RED + "Unrepairable");
        applyToMeta.setLore(applyToLore);
        applyToItem.setItemMeta(applyToMeta);

        event.setCurrentItem(applyToItem);
        event.setCancelled(true);

        player.setItemOnCursor(null);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Applied custom enchant!");
    }

    private boolean isUnrepairableBook(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.BOOK || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName()
                .equalsIgnoreCase(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Unrepairable Book");
    }

    private boolean isRepairable(Material type) {
        String name = type.name();
        return name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS")
                || name.contains("BOOTS") ||
                name.contains("SWORD") || name.contains("BOW") || name.contains("AXE") || name.contains("PICKAXE")
                || name.contains("SPADE");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFixCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        String command = message.split(" ")[0].replace("/", "");
        if (command.equals("fix") || command.equals("repair") || command.endsWith(":fix")
                || command.endsWith(":repair")) {
            Player player = event.getPlayer();

            event.setCancelled(true);

            if (!player.hasPermission("foxtrot.fix") && !player.isOp()) {
                player.sendMessage(ChatColor.RED + "No permission.");
                return;
            }

            boolean fixAll = message.contains("all");

            if (fixAll) {
                handleFixAll(player);
            } else {
                handleFixSingle(player);
            }
        }
    }

    private void handleFixSingle(Player player) {
        ItemStack inHand = player.getItemInHand();
        if (inHand == null || inHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding an item!");
            return;
        }

        if (!isRepairable(inHand.getType())) {
            player.sendMessage(ChatColor.RED + "This item cannot be repaired!");
            return;
        }

        if (hasUnrepairable(inHand)) {
            player.sendMessage(ChatColor.RED + "This item has the Unrepairable enchantment and cannot be fixed!");
            return;
        }

        if (inHand.getDurability() == 0) {
            player.sendMessage(ChatColor.RED + "This item is already at full durability!");
            return;
        }

        inHand.setDurability((short) 0);
        player.updateInventory();
        player.sendMessage(ChatColor.GREEN + "Item in hand has been repaired.");
    }

    private void handleFixAll(Player player) {
        int repaired = 0;
        boolean skipped = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || item.getDurability() == 0)
                continue;
            if (isRepairable(item.getType())) {
                if (hasUnrepairable(item)) {
                    skipped = true;
                    continue;
                }
                item.setDurability((short) 0);
                repaired++;
            }
        }

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null || item.getType() == Material.AIR || item.getDurability() == 0)
                continue;
            if (hasUnrepairable(item)) {
                skipped = true;
                continue;
            }
            item.setDurability((short) 0);
            repaired++;
        }

        if (repaired > 0) {
            player.sendMessage(ChatColor.GREEN + "All of your items have been repaired"
                    + (skipped ? " except the ones that have the Unrepairable enchantment." : "."));
        } else if (skipped) {
            player.sendMessage(
                    ChatColor.RED + "Your items could not be repaired because they have the Unrepairable enchantment.");
        } else {
            player.sendMessage(ChatColor.RED + "You have no items that need repairing!");
        }
    }

    private boolean hasUnrepairable(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return false;
        }
        for (String lore : item.getItemMeta().getLore()) {
            if (lore.contains("Unrepairable")) {
                return true;
            }
        }
        return false;
    }
}
