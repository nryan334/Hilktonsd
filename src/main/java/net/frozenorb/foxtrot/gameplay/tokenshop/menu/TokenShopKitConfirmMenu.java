package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.cavepvp.suge.kit.data.Kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenShopKitConfirmMenu extends Menu {
    private final Kit kit;
    private final int price = 5;

    public TokenShopKitConfirmMenu(Kit kit) {
        this.kit = kit;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Confirm Purchase";
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
                int col = i % 9;
                if (col >= 1 && col <= 3) {
                    buttons.put(i, new ConfirmButton(true));
                } else if (col >= 5 && col <= 7) {
                    buttons.put(i, new ConfirmButton(false));
                } else {
                    buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(7));
                }
            }
        }

        buttons.put(20, new ConfirmButton(true));
        buttons.put(24, new ConfirmButton(false));

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translateAlternateColorCodes('&', kit.getDisplayName());
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + price + " Tokens");
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
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 45;
    }

    private class ConfirmButton extends Button {
        private final boolean confirm;

        public ConfirmButton(boolean confirm) {
            this.confirm = confirm;
        }

        @Override
        public String getName(Player player) {
            return confirm ? ChatColor.GREEN + ChatColor.BOLD.toString() + "Confirm"
                    : ChatColor.RED + ChatColor.BOLD.toString() + "Cancel";
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY
                    + (confirm ? "Purchase this GKit for " + price + " Tokens." : "Cancel the purchase and go back."));
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.STAINED_CLAY;
        }

        @Override
        public byte getDamageValue(Player player) {
            return confirm ? (byte) 5 : (byte) 14;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (confirm) {
                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens >= price) {
                    Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - price);
                    player.sendMessage(ChatColor.GREEN + "You have purchased the "
                            + ChatColor.translateAlternateColorCodes('&', kit.getDisplayName()) + ChatColor.GREEN + " GKit for " + price + " tokens!");
                    kit.apply(player);
                    net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, ChatColor.translateAlternateColorCodes('&', kit.getDisplayName()) + " GKit");
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need " + price + " tokens.");
                    player.closeInventory();
                }
            } else {
                new TokenShopGKitsMenu().openMenu(player);
            }
        }
    }
}
