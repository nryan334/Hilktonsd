package net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.LootboxItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LootboxRewardsMenu extends Menu {
    private final Lootbox lootbox;

    @Override
    public String getTitle(Player player) {
        return "Editing Rewards: " + lootbox.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < lootbox.getItems().size(); i++) {
            LootboxItem item = lootbox.getItems().get(i);
            buttons.put(i, new RewardButton(item));
        }

        buttons.put(45, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "Bulk Edit Layout (Drag & Drop)";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Click to open a standard chest GUI.");
                desc.add(ChatColor.GRAY + "You can drag and drop items in bulk.");
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
                org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, "Lootbox Drop: " + lootbox.getId());

                for (LootboxItem item : lootbox.getItems()) {
                    inv.addItem(item.getItemStack().clone());
                }

                player.openInventory(inv);
            }
        });

        buttons.put(53, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Back";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new LootboxEditorMenu(lootbox).openMenu(player);
            }
        });

        return buttons;
    }

    @AllArgsConstructor
    private class RewardButton extends Button {
        private final LootboxItem item;

        @Override
        public String getName(Player player) {
            return item.getItemStack().getItemMeta().hasDisplayName() ? item.getItemStack().getItemMeta().getDisplayName() : item.getItemStack().getType().name();
        }

        @Override
        public Material getMaterial(Player player) {
            return item.getItemStack().getType();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
            lore.add(ChatColor.YELLOW + "Chance: " + ChatColor.WHITE + item.getChance() + "%");
            lore.add(ChatColor.YELLOW + "Type: " + ChatColor.WHITE + item.getExecutionType().name());
            if (!item.getCommands().isEmpty()) {
                lore.add(ChatColor.YELLOW + "Commands:");
                for (String cmd : item.getCommands()) {
                    lore.add(ChatColor.GRAY + " - " + ChatColor.WHITE + cmd);
                }
            }
            lore.add("");
            lore.add(ChatColor.GREEN + "Left-Click to edit settings");
            lore.add(ChatColor.RED + "Right-Click to remove");
            lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
            return lore;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isRightClick()) {
                lootbox.getItems().remove(item);
                Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                player.sendMessage(ChatColor.RED + "Reward removed.");
                openMenu(player);
            } else {
                new LootboxItemEditMenu(lootbox, item).openMenu(player);
            }
        }
    }
}
