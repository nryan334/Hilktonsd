package net.frozenorb.foxtrot.gameplay.loot.airdrop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDrop;
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

public class AirDropEditorMenu extends Menu {

    public AirDropEditorMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "AirDrop Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (AirDrop airDrop : Foxtrot.getInstance().getAirDropHandler().getAirDrops().values()) {
            buttons.put(i++, new AirDropButton(airDrop));
        }

        return buttons;
    }

    private static class AirDropButton extends Button {
        private final AirDrop airDrop;

        public AirDropButton(AirDrop airDrop) {
            this.airDrop = airDrop;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.AQUA + ChatColor.BOLD.toString() + airDrop.getId();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Display Name: " + ChatColor.WHITE + airDrop.getDisplayName());
            lore.add(ChatColor.YELLOW + "Rewards: " + ChatColor.WHITE + airDrop.getRewards().size());
            lore.add("");
            lore.add(ChatColor.GREEN + "Click to edit rewards.");
            lore.add(ChatColor.RED + "Right-click to delete.");
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return airDrop.getMaterial();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isRightClick()) {
                Foxtrot.getInstance().getAirDropHandler().deleteAirDrop(airDrop);
                player.sendMessage(ChatColor.RED + "Deleted airdrop: " + airDrop.getId());
                return;
            }
            new AirDropSettingsMenu(airDrop).openMenu(player);
        }
    }
}
