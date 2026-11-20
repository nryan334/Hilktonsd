package net.frozenorb.foxtrot.gameplay.loot.redeem.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedeemMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.AQUA + "Partners";
    }

    @Override
    public int size(Player player) {
        return Foxtrot.getInstance().getRedeemCreatorHandler().getRows() * 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Button glass = new Button() {
            @Override
            public String getName(Player player) {
                return " ";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.STAINED_GLASS_PANE;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 11;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
            }
        };

        int[] glassSlots = { 0, 1, 9, 18, 19, 7, 8, 17, 25, 26 };
        for (int slot : glassSlots) {
            buttons.put(slot, glass);
        }

        Set<String> partners = new HashSet<>(Foxtrot.getInstance().getRedeemCreatorHandler().getCreators().values());

        int[] skullSlots = { 11, 13, 15, 10, 12, 14, 16 };
        int i = 0;

        for (String partner : partners) {
            if (i >= skullSlots.length)
                break;

            buttons.put(skullSlots[i], new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.AQUA + ChatColor.BOLD.toString() + partner;
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Click to redeem " + partner + "'s rewards!");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return 3;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack item = super.getButtonItem(player);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    meta.setOwner(partner);
                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    player.performCommand("redeem " + partner);
                }
            });
            i++;
        }

        return buttons;
    }
}
