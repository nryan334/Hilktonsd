package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.tokenshop.TokenShopCategory;
import net.frozenorb.foxtrot.gameplay.tokenshop.menu.TokenShopKeysMenu;
import net.frozenorb.foxtrot.util.SkullUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenShopMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Token Shop - Categories";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        boolean hasCrates = !Foxtrot.getInstance().getCrateHandler().getCrates().isEmpty();
        boolean hasKits = false;

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

        try {
            hasKits = org.bukkit.Bukkit.getPluginManager().getPlugin("Suge") != null && org.bukkit.Bukkit.getPluginManager().getPlugin("Suge").isEnabled() && !org.cavepvp.suge.Suge.getInstance().getKitHandler().getKits().isEmpty();
        } catch (NoClassDefFoundError ignored) {
        }


        for (TokenShopCategory category : Foxtrot.getInstance().getTokenShopHandler().getCategories()) {
            if (category.isGkits() && !hasKits) continue;
            if (category.isKeys() && !hasCrates) continue;

            buttons.put(category.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', category.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Browse items inside the");
                    lore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', category.getDisplayName()) + ChatColor.GRAY + " category.");
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "Click to browse category!");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return category.getMaterial();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) category.getDamage();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (category.isGkits()) {
                        new net.frozenorb.foxtrot.gameplay.tokenshop.menu.TokenShopGKitsMenu().openMenu(player);
                    } else if (category.isKeys()) {
                        new net.frozenorb.foxtrot.gameplay.tokenshop.menu.TokenShopKeysMenu().openMenu(player);
                    } else {
                        new net.frozenorb.foxtrot.gameplay.tokenshop.menu.TokenShopDynamicCategoryMenu(category).openMenu(player);
                    }
                }
            });
        }

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Info";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Welcome to the " + ChatColor.WHITE + "Token Shop" + ChatColor.GRAY + "!");
                lore.add(ChatColor.GRAY + "Here you can spend your " + ChatColor.DARK_AQUA + "Souls Tokens");
                lore.add(ChatColor.GRAY + "on exclusive items and crate keys.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Select a category below to browse!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 45;
    }
}
