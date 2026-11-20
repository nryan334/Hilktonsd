package net.frozenorb.foxtrot.server.reclaim.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.reclaim.Reclaim;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class ReclaimEditorMenu extends Menu {

    public ReclaimEditorMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Reclaim Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (Reclaim reclaim : Foxtrot.getInstance().getReclaimHandler().getReclaims()) {
            buttons.put(i++, new ReclaimButton(reclaim));
        }

        return buttons;
    }

    private static class ReclaimButton extends Button {

        private final Reclaim reclaim;

        public ReclaimButton(Reclaim reclaim) {
            this.reclaim = reclaim;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + reclaim.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Priority: " + ChatColor.WHITE + reclaim.getPriority());
            lore.add(ChatColor.YELLOW + "Permission: " + ChatColor.WHITE + reclaim.getPermission());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Commands: " + ChatColor.WHITE + reclaim.getCommands().size());
            lore.add(ChatColor.YELLOW + "Items: " + ChatColor.WHITE + reclaim.getItems().size());
            lore.add("");
            lore.add(ChatColor.GREEN + "Click to edit this reclaim.");
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.NAME_TAG;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            new ReclaimEditMenu(reclaim).openMenu(player);
        }
    }
}
