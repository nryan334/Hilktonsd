package net.frozenorb.foxtrot.gameplay.extra.settings;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class SettingsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Settings";
    }

    public int size(Map<Integer, Button> buttons) {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        for (SettingType type : SettingType.values()) {
            buttons.put(type.getSlot(), new Button() {
                @Override
                public String getName(Player p) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + type.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player p) {
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    for (String desc : type.getDescription()) {
                        lore.add(desc);
                    }
                    lore.add("");
                    lore.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "\u2503 " + ChatColor.WHITE + "Status: " + (type.isEnabled(p) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
                    lore.add("");
                    if (!type.isEnabled(p)) {
                        lore.add(ChatColor.GREEN + "Click to enable " + ChatColor.stripColor(type.getDisplayName()));
                    } else {
                        lore.add(ChatColor.GREEN + "Click to disable " + ChatColor.stripColor(type.getDisplayName()));
                    }
                    return lore;
                }

                @Override
                public Material getMaterial(Player p) {
                    return type.getMaterial();
                }

                @Override
                public void clicked(Player p, int slot, ClickType clickType) {
                    type.toggle(p);
                }
            });
        }

        return buttons;
    }
}
