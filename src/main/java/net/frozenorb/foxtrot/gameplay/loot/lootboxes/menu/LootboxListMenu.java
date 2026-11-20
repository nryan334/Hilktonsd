package net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Map;

public class LootboxListMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Lootbox List";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;

        for (Lootbox lootbox : Foxtrot.getInstance().getLootboxHandler().getLootboxes().values()) {
            buttons.put(i++, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', lootbox.getDisplayName());
                }

                @Override
                public Material getMaterial(Player player) {
                    return lootbox.getMaterial();
                }

                @Override
                public java.util.List<String> getDescription(Player player) {
                    return new java.util.ArrayList<>();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new LootboxEditorMenu(lootbox).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
