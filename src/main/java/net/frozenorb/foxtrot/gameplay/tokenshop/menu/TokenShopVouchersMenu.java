package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.command.VoucherCommand;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import net.frozenorb.foxtrot.team.menu.TokenShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenShopVouchersMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Token Shop - Life Vouchers";
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

        buttons.put(20, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "1 Life Voucher";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Purchase a Voucher that grants");
                lore.add(ChatColor.GRAY + "1 Life to whoever redeems it.");
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + "2 Tokens");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to purchase!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens < 2) {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need 2 tokens.");
                    return;
                }

                Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - 2);
                VoucherTemplate template = Foxtrot.getInstance().getVoucherHandler().fromName("1_life_voucher").orElseGet(() -> {
                    VoucherTemplate t = new VoucherTemplate("1_life_voucher");
                    t.setCategory("LIVES");
                    t.setValue("1");
                    t.setDisplayName("&4&l1 Life &7┃ &bVoucher");
                    t.save();
                    Foxtrot.getInstance().getVoucherHandler().getTemplates().add(t);
                    return t;
                });
                ItemStack voucher = VoucherCommand.createPhysicalVoucher(template);
                player.getInventory().addItem(voucher);
                player.sendMessage(ChatColor.GREEN + "You purchased a " + ChatColor.WHITE + "1 Life Voucher"
                        + ChatColor.GREEN + " for 2 tokens!");
                net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, "1 Life Voucher");
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + ChatColor.BOLD.toString() + "5 Lives Voucher";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Purchase a Voucher that grants");
                lore.add(ChatColor.GRAY + "5 Lives to whoever redeems it.");
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + "5 Tokens");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to purchase!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens < 5) {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need 5 tokens.");
                    return;
                }

                Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - 5);
                VoucherTemplate template = Foxtrot.getInstance().getVoucherHandler().fromName("5_lives_voucher").orElseGet(() -> {
                    VoucherTemplate t = new VoucherTemplate("5_lives_voucher");
                    t.setCategory("LIVES");
                    t.setValue("5");
                    t.setDisplayName("&4&l5 Lives &7┃ &bVoucher");
                    t.save();
                    Foxtrot.getInstance().getVoucherHandler().getTemplates().add(t);
                    return t;
                });
                ItemStack voucher = VoucherCommand.createPhysicalVoucher(template);
                player.getInventory().addItem(voucher);
                player.sendMessage(ChatColor.GREEN + "You purchased a " + ChatColor.WHITE + "5 Lives Voucher"
                        + ChatColor.GREEN + " for 5 tokens!");
                net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, "5 Lives Voucher");
            }
        });

        buttons.put(24, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + ChatColor.BOLD.toString() + "10 Lives Voucher";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Purchase a Voucher that grants");
                lore.add(ChatColor.GRAY + "10 Lives to whoever redeems it.");
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + "8 Tokens");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to purchase!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens < 8) {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need 8 tokens.");
                    return;
                }

                Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - 8);
                VoucherTemplate template = Foxtrot.getInstance().getVoucherHandler().fromName("10_lives_voucher").orElseGet(() -> {
                    VoucherTemplate t = new VoucherTemplate("10_lives_voucher");
                    t.setCategory("LIVES");
                    t.setValue("10");
                    t.setDisplayName("&4&l10 Lives &7┃ &bVoucher");
                    t.save();
                    Foxtrot.getInstance().getVoucherHandler().getTemplates().add(t);
                    return t;
                });
                ItemStack voucher = VoucherCommand.createPhysicalVoucher(template);
                player.getInventory().addItem(voucher);
                player.sendMessage(ChatColor.GREEN + "You purchased a " + ChatColor.WHITE + "10 Lives Voucher"
                        + ChatColor.GREEN + " for 8 tokens!");
                net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, "10 Lives Voucher");
            }
        });

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
