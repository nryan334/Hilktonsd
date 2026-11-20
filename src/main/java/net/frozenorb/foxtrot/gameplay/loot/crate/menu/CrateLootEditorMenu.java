package net.frozenorb.foxtrot.gameplay.loot.crate.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CrateLootEditorMenu extends PaginatedMenu {
    private final Crate crate;

    public CrateLootEditorMenu(Crate crate) {
        this.crate = crate;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Loot Editor: " + crate.getId();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (CrateItem item : crate.getItems()) {
            buttons.put(buttons.size(), new CrateItemButton(crate, item));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "Bulk Edit Layout (Drag & Drop)";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Click to open a standard chest GUI.");
                desc.add(ChatColor.GRAY + "You can drag and drop items in bulk");
                desc.add(ChatColor.GRAY + "and arrange the visual layout.");
                desc.add("");
                desc.add(ChatColor.YELLOW + "Changes save automatically when you close it.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 54,
                        "Drag & Drop: " + crate.getId());

                for (CrateItem item : crate.getItems()) {
                    if (item.getSlot() >= 0 && item.getSlot() < 54) {
                        inv.setItem(item.getSlot(), item.getItemStack().clone());
                    } else {
                        inv.addItem(item.getItemStack().clone());
                    }
                }

                player.openInventory(inv);
            }
        });

        return buttons;
    }

    private static class CrateItemButton extends Button {
        private final Crate crate;
        private final CrateItem crateItem;

        public CrateItemButton(Crate crate, CrateItem crateItem) {
            this.crate = crate;
            this.crateItem = crateItem;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemStack item = crateItem.getItemStack().clone();
            List<String> lore = new ArrayList<>();
            if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
                lore.addAll(item.getItemMeta().getLore());
            }
            lore.add("");
            lore.add(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crate Settings:");
            lore.add(ChatColor.YELLOW + "Chance: " + ChatColor.WHITE + crateItem.getChance() + "%");
            lore.add(ChatColor.YELLOW + "Command: " + ChatColor.WHITE + crateItem.getCommand());
            lore.add(ChatColor.YELLOW + "Give Item: " + ChatColor.WHITE + crateItem.isGiveItem());
            lore.add(ChatColor.YELLOW + "Broadcast: " + ChatColor.WHITE + crateItem.isBroadcast());
            lore.add("");
            lore.add(ChatColor.GREEN + "Left-Click to edit settings.");
            lore.add(ChatColor.RED + "Shift-Click to remove.");

            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
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
            if (clickType.isShiftClick()) {
                crate.getItems().remove(crateItem);
                player.sendMessage(ChatColor.RED + "Removed item from loot.");
                new CrateLootEditorMenu(crate).openMenu(player);
                return;
            }

            new CrateItemEditMenu(crate, crateItem).openMenu(player);
        }
    }
}
