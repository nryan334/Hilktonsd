package net.frozenorb.foxtrot.server.reclaim.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.reclaim.Reclaim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class ReclaimItemEditMenu extends Menu {

    private final Reclaim reclaim;

    public ReclaimItemEditMenu(Reclaim reclaim) {
        this.reclaim = reclaim;
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Items: " + reclaim.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "Bulk Edit Layout (Drag & Drop)";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Click to open a standard chest GUI.");
                lore.add(ChatColor.GRAY + "You can drag and drop items in bulk.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Changes save automatically when you close it.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Inventory inv = Bukkit.createInventory(null, 54, "Reclaim Items: " + reclaim.getName());

                for (ItemStack item : reclaim.getItems()) {
                    inv.addItem(item.clone());
                }

                player.openInventory(inv);
            }
        });

        buttons.put(8, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Go Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BED;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ReclaimEditMenu(reclaim).openMenu(player);
            }
        });

        int slot = 9;
        for (int i = 0; i < reclaim.getItems().size(); i++) {
            ItemStack item = reclaim.getItems().get(i);
            int finalI = i;
            buttons.put(slot++, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack itemStack = item.clone();
                    return itemStack;
                }

                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    reclaim.getItems().remove(finalI);
                    Foxtrot.getInstance().getReclaimHandler().saveReclaim(reclaim);
                    player.sendMessage(ChatColor.RED + "Removed item from reclaim.");
                }
            });
        }

        return buttons;
    }
}
