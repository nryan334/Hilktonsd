package net.frozenorb.foxtrot.gameplay.events.koth.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.gameplay.events.koth.Koth;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KothColorMenu extends Menu {

    private final Koth koth;

    public KothColorMenu(Koth koth) {
        this.koth = koth;
        this.setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Color: " + koth.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ChatColor[] colors = {
                ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, ChatColor.AQUA,
                ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY,
                ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE,
                ChatColor.DARK_GREEN, ChatColor.DARK_RED, ChatColor.RED, ChatColor.BLACK
        };

        short[] woolData = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

        for (int i = 0; i < colors.length; i++) {
            final ChatColor color = colors[i];
            final short data = woolData[i];

            buttons.put(i, new Button() {
                @Override
                public String getName(Player player) {
                    return color + color.name();
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> desc = new ArrayList<>();
                    desc.add("");
                    desc.add(CC.translate("&eClick to select this color."));
                    return desc;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.WOOL;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) data;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    String baseColor = "";
                    if (koth.getColor().contains("§l")) baseColor += "§l";
                    if (koth.getColor().contains("§o")) baseColor += "§o";

                    koth.setColor(color.toString() + baseColor);
                    koth.save();
                    player.sendMessage(CC.translate("&aUpdated base color to " + color + color.name() + "&a."));
                }
            });
        }

        buttons.put(18, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&f&lToggle Bold");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add("");
                boolean isBold = koth.getColor().contains("§l");
                desc.add(CC.translate("&7Currently: " + (isBold ? "&aEnabled" : "&cDisabled")));
                desc.add("");
                desc.add(CC.translate("&eClick to toggle."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                String currentColor = koth.getColor();
                if (currentColor.contains("§l")) {
                    koth.setColor(currentColor.replace("§l", ""));
                } else {
                    koth.setColor(currentColor + "§l");
                }
                koth.save();
                player.sendMessage(CC.translate("&aToggled Bold formatting."));
            }
        });

        buttons.put(19, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&f&oToggle Italic");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add("");
                boolean isItalic = koth.getColor().contains("§o");
                desc.add(CC.translate("&7Currently: " + (isItalic ? "&aEnabled" : "&cDisabled")));
                desc.add("");
                desc.add(CC.translate("&eClick to toggle."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                String currentColor = koth.getColor();
                if (currentColor.contains("§o")) {
                    koth.setColor(currentColor.replace("§o", ""));
                } else {
                    koth.setColor(currentColor + "§o");
                }
                koth.save();
                player.sendMessage(CC.translate("&aToggled Italic formatting."));
            }
        });

        buttons.put(26, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&cGo Back");
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
                new KothEditorMenu(koth).openMenu(player);
            }
        });

        return buttons;
    }
}
