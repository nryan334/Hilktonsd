package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.team.menu.TokenShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.frozenorb.foxtrot.util.SkullUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class TokenShopKeysMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Token Shop - Crate Keys";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            if (i % 9 == 0 || i % 9 == 8) {
                buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(1));
            } else if (i < 9 || i >= 36) {
                if (i == 2 || i == 6 || i == 38 || i == 42 || i == 39 || i == 40 || i == 41) {
                    buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(4));
                } else if (i == 1 || i == 7 || i == 37 || i == 43) {
                    buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(1));
                } else {
                    buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(7));
                }
            } else {
                buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(7));
            }
        }

        int slot = 20;
        for (Crate crate : Foxtrot.getInstance().getCrateHandler().getCrates()) {
            if (!crate.isSellableOnTokenShop()) continue;

            if (slot > 24 && slot < 29)
                slot = 29;

            buttons.put(slot++, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', crate.getDisplayName() + " Key");
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Purchase a key for the");
                    lore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', crate.getDisplayName()) + ChatColor.GRAY + ".");
                    lore.add("");
                    lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + "3 Tokens");
                    lore.add("");
                    lore.add(ChatColor.GREEN + "Click to purchase!");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return crate.getMaterial();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) crate.getData();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new TokenShopConfirmMenu(crate).openMenu(player);
                }
            });
        }

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + ChatColor.BOLD.toString() + "Back to Categories";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new TokenShopMenu().openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 45;
    }
}
