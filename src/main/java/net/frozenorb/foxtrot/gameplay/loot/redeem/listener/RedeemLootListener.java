package net.frozenorb.foxtrot.gameplay.loot.redeem.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import net.frozenorb.foxtrot.gameplay.loot.redeem.menu.RedeemLootEditorMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class RedeemLootListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != null && event.getInventory().getTitle().equals("Drag & Drop: Redeem")) {
            Player player = (Player) event.getPlayer();
            Inventory inv = event.getInventory();
            List<CrateItem> newItems = new ArrayList<>();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    int finalI = i;
                    CrateItem existing = Foxtrot.getInstance().getRedeemCreatorHandler().getRewards().stream()
                            .filter(ci -> ci.getSlot() == finalI && ci.getItemStack().isSimilar(item))
                            .findFirst().orElse(null);

                    if (existing != null) {
                        existing.setItemStack(item.clone());
                        newItems.add(existing);
                    } else {
                        CrateItem newItem = new CrateItem(item.clone(), 10.0, "none", true, false, i);
                        newItems.add(newItem);
                    }
                }
            }

            Foxtrot.getInstance().getRedeemCreatorHandler().getRewards().clear();
            Foxtrot.getInstance().getRedeemCreatorHandler().getRewards().addAll(newItems);
            Foxtrot.getInstance().getRedeemCreatorHandler().save();

            player.sendMessage(ChatColor.GREEN + "Saved " + newItems.size() + " items to the redeem rewards.");

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                new RedeemLootEditorMenu().openMenu(player);
            }, 2L);
        }
    }
}
