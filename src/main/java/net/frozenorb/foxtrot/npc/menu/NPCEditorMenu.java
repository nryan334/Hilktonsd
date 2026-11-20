package net.frozenorb.foxtrot.npc.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "NPC Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 0;
        for (NPC npc : Foxtrot.getInstance().getNpcHandler().getNpcs().values()) {
            buttons.put(slot++, new NPCButton(npc));
        }

        return buttons;
    }

    private static class NPCButton extends Button {
        private final NPC npc;

        public NPCButton(NPC npc) {
            this.npc = npc;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD + npc.getDisplayName();
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                ChatColor.GRAY + "ID: " + ChatColor.WHITE + npc.getId(),
                ChatColor.GRAY + "Location: " + ChatColor.WHITE + npc.getLocation().getBlockX() + ", " + npc.getLocation().getBlockY() + ", " + npc.getLocation().getBlockZ(),
                "",
                ChatColor.YELLOW + "Click to manage this NPC."
            );
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.SKULL_ITEM;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 3;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            new NPCManageMenu(npc).openMenu(player);
        }
    }
}
