package net.frozenorb.foxtrot.gameplay.tokenshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.TokenShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.CoinPurchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class TokenShopTeamMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Token Shop - Base & Traps";
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
                return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Base Token";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Purchase a Base Token for your team.");
                lore.add(ChatColor.GRAY + "This token allows you to generate a base");
                lore.add(ChatColor.GRAY + "using " + ChatColor.WHITE + "/blueprints" + ChatColor.GRAY + ".");
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + "10 Tokens");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to purchase!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.STAINED_GLASS;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 14;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                if (team == null) {
                    player.sendMessage(ChatColor.RED + "You must be in a team to purchase tokens!");
                    return;
                }

                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens < 3) {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need 3 tokens.");
                    return;
                }

                Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - 3);
                team.setBaseTokens(team.getBaseTokens() + 1);
                team.flagForSave();

                player.sendMessage(ChatColor.GREEN + "You have purchased a " + ChatColor.LIGHT_PURPLE + "Base Token"
                        + ChatColor.GREEN + " for your team!");
                net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, "Base Token");
            }
        });

        buttons.put(24, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Faller Token";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Purchase a Faller Token for your team.");
                lore.add(ChatColor.GRAY + "This token allows you to generate a faller trap");
                lore.add(ChatColor.GRAY + "using " + ChatColor.WHITE + "/blueprints" + ChatColor.GRAY + ".");
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + "20 Tokens");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to purchase!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                if (team == null) {
                    player.sendMessage(ChatColor.RED + "You must be in a team to purchase tokens!");
                    return;
                }

                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(player.getUniqueId());
                if (tokens < 5) {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens! You need 5 tokens.");
                    return;
                }

                Foxtrot.getInstance().getKitmapTokensMap().setTokens(player.getUniqueId(), tokens - 5);
                team.setFallTrapTokens(team.getFallTrapTokens() + 1);
                team.flagForSave();

                player.sendMessage(ChatColor.GREEN + "You have purchased a " + ChatColor.DARK_RED + "Faller Token"
                        + ChatColor.GREEN + " for your team!");
                net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler.announcePurchase(player, "Faller Token");
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

    private void spendCoins(Player player, int amount) {
        PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(),
                player.getName());
        List<CoinPurchase> purchases = profile.getSortedCoinPurchases();

        int remainingToSpend = amount;
        for (CoinPurchase purchase : purchases) {
            int canSpend = purchase.coinsRemaining();
            if (canSpend <= 0)
                continue;

            if (canSpend >= remainingToSpend) {
                purchase.setCoinsSpent(purchase.getCoinsSpent() + remainingToSpend);
                remainingToSpend = 0;
                break;
            } else {
                purchase.setCoinsSpent(purchase.getCoinsSpent() + canSpend);
                remainingToSpend -= canSpend;
            }
        }
        profile.save();
    }

    @Override
    public int size(Player player) {
        return 45;
    }
}
