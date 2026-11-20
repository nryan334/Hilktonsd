package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.util.SkullUtils;
import org.bukkit.Bukkit;
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

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class TokenShopConfirmMenu extends Menu {
    private final Crate crate;

    public TokenShopConfirmMenu(Crate crate) {
        this.crate = crate;
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
            }

            else if (i < 9 || i >= 36) {
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
                return ChatColor.translateAlternateColorCodes('&', crate.getDisplayName() + " Key");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + "3 Tokens");
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
                    + (confirm ? "Purchase this key for 3 Tokens." : "Cancel the purchase and go back."));
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
        public ItemStack getButtonItem(Player player) {
            return super.getButtonItem(player);
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (confirm) {
                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens >= 3) {
                    Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - 3);
                    player.sendMessage(ChatColor.GREEN + "You have purchased a "
                            + ChatColor.translateAlternateColorCodes('&', crate.getDisplayName()) + ChatColor.GREEN + " key for 3 tokens!");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "crates give " + player.getName() + " " + crate.getId() + " 1");
                    net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, ChatColor.translateAlternateColorCodes('&', crate.getDisplayName()) + " Key");
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need 3 tokens.");
                    player.closeInventory();
                }
            } else {
                new TokenShopKeysMenu().openMenu(player);
            }
        }
    }
}
