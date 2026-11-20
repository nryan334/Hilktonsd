package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;
import net.frozenorb.foxtrot.team.menu.TokenShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenShopGKitsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Token Shop - GKits";
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
        for (Kit kit : Suge.getInstance().getKitHandler().getKits().values()) {
            if (!kit.isTokenShopSellable()) continue;

            if (slot > 24 && slot < 29)
                slot = 29;

            if (slot >= 36) break;

            final int price = kit.getTokenShopPrice();
            final int discount = Foxtrot.getInstance().getTokenShopHandler().getGlobalPromoDiscount();
            final int finalPrice;
            if (discount > 0) {
                finalPrice = Math.max(1, price - (price * discount / 100));
            } else {
                finalPrice = price;
            }

            buttons.put(slot++, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', kit.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Purchase the " + ChatColor.WHITE
                            + ChatColor.translateAlternateColorCodes('&', kit.getDisplayName()) + ChatColor.GRAY + " GKit.");
                    lore.add("");
                    if (discount > 0) {
                        lore.add(ChatColor.GOLD + "Cost: " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + price + ChatColor.YELLOW + " " + finalPrice + " Tokens " + ChatColor.RED + "(" + discount + "% OFF!)");
                    } else {
                        lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + price + " Tokens");
                    }
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "Left-Click to purchase!");
                    lore.add(ChatColor.AQUA + "Right-Click to preview!");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return kit.getMaterial();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) kit.getDamage();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType.isRightClick()) {
                        new org.cavepvp.suge.kit.menu.KitPreviewMenu(kit, TokenShopGKitsMenu.this).openMenu(player);
                        return;
                    }

                    int tokens = net.frozenorb.foxtrot.Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                    if (tokens >= finalPrice) {
                        net.frozenorb.foxtrot.Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - finalPrice);
                        player.sendMessage(ChatColor.GREEN + "You have purchased the " + kit.getDisplayName() + ChatColor.GREEN + " kit for " + finalPrice + " tokens!");
                        kit.apply(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need " + finalPrice + " tokens.");
                    }
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
