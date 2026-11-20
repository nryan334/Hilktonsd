package net.frozenorb.foxtrot.gameplay.treasurebeach;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TreasureBeachInfoMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', "&4&lTreasure Beach");
    }

    @Override
    public int size(Player player) {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            if (i < 9 || i > 35 || i % 9 == 0 || (i + 1) % 9 == 0) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 1, " "));
            }
        }

        TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translateAlternateColorCodes('&', "&4&lTreasure Beach Info");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (handler == null || !handler.isClaimed()) {
                    lore.add(c(" &cThe Treasure Beach has not been"));
                    lore.add(c(" &cset up yet by an administrator."));
                } else {
                    lore.add(c(" &7World: &b" + "Overworld"));
                    lore.add(c(" &7Coordinates 2: &f" + "500, 699"));
                    lore.add("");
                    lore.add(c(" &7Custom Loot Items: &e" + handler.getCustomLoot().size()));
                }
                lore.add("");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translateAlternateColorCodes('&', "&a&lRefill Timer");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (handler == null || !handler.isClaimed()) {
                    lore.add(c(" &7Not available."));
                } else {
                    long cooldownMs = handler.getRefillCooldownMs();
                    long elapsed = System.currentTimeMillis() - handler.getLastRefillTime();
                    long remaining = cooldownMs - elapsed;
                    if (remaining < 0)
                        remaining = 0;

                    long hours = TimeUnit.MILLISECONDS.toHours(remaining);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining) % 60;

                    long cdHours = TimeUnit.MILLISECONDS.toHours(cooldownMs);

                    lore.add(c(" &7Refill Interval: &e" + cdHours + " hour(s)"));
                    lore.add("");
                    if (remaining == 0) {
                        lore.add(c(" &a&l✦ &aChests are being refilled!"));
                    } else {
                        lore.add(c(" &7Next Refill In:"));
                        lore.add(c(" &e&l" + hours + "h " + minutes + "m " + seconds + "s"));
                    }
                }
                lore.add("");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WATCH;
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translateAlternateColorCodes('&', "&6&lRefill Schedule");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (handler == null || !handler.isClaimed()) {
                    lore.add(c(" &7Not available."));
                } else {
                    long cooldownMs = handler.getRefillCooldownMs();
                    long cdHours = TimeUnit.MILLISECONDS.toHours(cooldownMs);

                    long lastRefill = handler.getLastRefillTime();
                    if (lastRefill == 0)
                        lastRefill = System.currentTimeMillis();
                    long now = System.currentTimeMillis();

                    lore.add(c(" &7Chests refill automatically every"));
                    lore.add(c(" &e" + cdHours + " hours &7from the last refill."));
                    lore.add("");
                    lore.add(c(" &e&lUpcoming Refills:"));
                    lore.add("");

                    long nextRefill = lastRefill + cooldownMs;
                    while (nextRefill < now) {
                        nextRefill += cooldownMs;
                    }

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("E h:mm a", java.util.Locale.ENGLISH);
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));
                    for (int i = 0; i < 5; i++) {
                        String formatted = sdf.format(new java.util.Date(nextRefill));
                        String prefix = i == 0 ? " &a&l▸ " : " &7&l▸ ";
                        String color = i == 0 ? "&a" : "&f";
                        lore.add(c(prefix + color + formatted + " EST"));
                        nextRefill += cooldownMs;
                    }
                }
                lore.add("");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translateAlternateColorCodes('&', "&d&lLoot Preview");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (handler == null || !handler.isClaimed()) {
                    lore.add(c(" &7Not available."));
                } else if (handler.getCustomLoot().isEmpty()) {
                    lore.add(c(" &7Using &edefault loot&7:"));
                    lore.add("");
                    lore.add(c("  &b▪ &fAbility Items &7(random)"));
                    lore.add(c("  &b▪ &fDiamond Helmet &7(Prot II)"));
                    lore.add(c("  &b▪ &fDiamond Chestplate &7(Prot II)"));
                    lore.add(c("  &b▪ &fDiamond Leggings &7(Prot II)"));
                    lore.add(c("  &b▪ &fDiamond Boots &7(Prot II)"));
                } else {
                    lore.add(c(" &7Using &acustom loot pool&7:"));
                    lore.add("");
                    int shown = 0;
                    for (ItemStack item : handler.getCustomLoot()) {
                        if (shown >= 8) {
                            lore.add(c("  &8... and " + (handler.getCustomLoot().size() - shown) + " more items."));
                            break;
                        }
                        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                ? item.getItemMeta().getDisplayName()
                                : formatMaterial(item.getType());
                        lore.add(c("  &b▪ &f" + name + " &7x" + item.getAmount()));
                        shown++;
                    }
                }
                lore.add("");
                lore.add(c(" &7Items per chest: &e2-5 random"));
                lore.add("");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GOLD_INGOT;
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translateAlternateColorCodes('&', "&4&lHow It Works");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(c(" &7The Treasure Beach is a dangerous"));
                lore.add(c(" &7area filled with loot chests that"));
                lore.add(c(" &7refill automatically on a timer."));
                lore.add("");
                lore.add(c(" &e&lRules:"));
                lore.add(c("  &6▪ &fPvP is &cenabled &fin the area"));
                lore.add(c("  &6▪ &cAbility Items &fare not allowed in the area"));
                lore.add(c("  &6▪ &fAnyone can loot the chests"));
                lore.add(c("  &6▪ &fBe prepared for fights!"));
                lore.add(c("  &6▪ &fEnderpearls are not allowed in the area"));
                lore.add(c("  &6▪ &fIf you die you will get &c15m &fof &cdeathban&f!"));
                lore.add("");
                lore.add(c(" &a&lTIP: &7Check the &eRefill Schedule"));
                lore.add(c(" &7to know exactly when chests reset!"));
                lore.add("");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }
        });

        return buttons;
    }

    private static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }



    private static String formatMaterial(Material mat) {
        String name = mat.name().replace('_', ' ').toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }
}
