package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.tokenshop.TokenShopCategory;
import net.frozenorb.foxtrot.gameplay.tokenshop.TokenShopProduct;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.TokenShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenShopDynamicCategoryMenu extends Menu {
    private final TokenShopCategory category;

    public TokenShopDynamicCategoryMenu(TokenShopCategory category) {
        this.category = category;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', category.getDisplayName());
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

        int autoSlot = 20;
        for (TokenShopProduct product : category.getProducts()) {
            int slot = product.getSlot();
            if (slot <= 0 || slot >= 45 || buttons.get(slot) == null
                    || !(buttons.get(slot) instanceof net.frozenorb.foxtrot.util.GlassButton)) {
                while (autoSlot < 36 && (autoSlot % 9 == 0 || autoSlot % 9 == 8)) {
                    autoSlot++;
                }
                slot = autoSlot++;
            }

            final int finalSlot = slot;

            final int price = product.getCost();
            final int promoPrice = product.getPromoPrice();
            final int globalDiscount = Foxtrot.getInstance().getTokenShopHandler().getGlobalPromoDiscount();

            final int finalPrice;
            final boolean isPromo;
            if (promoPrice >= 0) {
                finalPrice = promoPrice;
                isPromo = true;
            } else if (globalDiscount > 0) {
                finalPrice = Math.max(1, price - (price * globalDiscount / 100));
                isPromo = true;
            } else {
                finalPrice = price;
                isPromo = false;
            }

            buttons.put(finalSlot, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', product.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    for (String line : product.getLore()) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', line));
                    }
                    if (!lore.isEmpty()) {
                        lore.add("");
                    }

                    if (isPromo) {
                        int discountPercent = (promoPrice >= 0) ? (int) ((1.0 - ((double) promoPrice / price)) * 100)
                                : globalDiscount;
                        lore.add(ChatColor.GOLD + "Cost: " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + price
                                + ChatColor.YELLOW + " " + finalPrice + " Tokens " + ChatColor.RED + "("
                                + discountPercent + "% OFF!)");
                    } else {
                        lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + price + " Tokens");
                    }

                    lore.add("");
                    lore.add(ChatColor.GREEN + "Click to purchase!");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return product.getMaterial();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) product.getDamage();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

                    for (String cmd : product.getCommands()) {
                        if (cmd.contains("%player_team%") && team == null) {
                            player.sendMessage(ChatColor.RED + "You must be in a team to purchase this item!");
                            return;
                        }
                    }

                    int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                    if (tokens >= finalPrice) {
                        Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - finalPrice);

                        for (String cmd : product.getCommands()) {
                            String processedCmd = cmd
                                    .replace("%player%", player.getName())
                                    .replace("%player_uuid%", player.getUniqueId().toString())
                                    .replace("%player_team%", team != null ? team.getName() : "");

                            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(),
                                    ChatColor.stripColor(processedCmd));
                        }

                        player.sendMessage(ChatColor.GREEN + "You have successfully purchased "
                                + ChatColor.translateAlternateColorCodes('&', product.getDisplayName()) + "!");
                    } else {
                        player.sendMessage(
                                ChatColor.RED + "You do not have enough tokens! You need " + finalPrice + " tokens.");
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
