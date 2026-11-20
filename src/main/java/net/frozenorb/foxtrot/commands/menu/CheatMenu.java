package net.frozenorb.foxtrot.commands.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheatMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Cheat Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new CheatButton("Reach (8 Blocks)", Material.DIAMOND_SWORD, "CHEAT_REACH"));
        buttons.put(2, new CheatButton("FastBow", Material.BOW, "CHEAT_FASTBOW"));
        buttons.put(4, new CheatButton("Bypass Claims", Material.GRASS, "build"));
        buttons.put(6, new CheatButton("Bypass Abilities", Material.BLAZE_POWDER, "CHEAT_ABILITY"));
        buttons.put(8, new CheatButton("Phase (Shift+Click)", Material.COMPASS, "CHEAT_PHASE"));

        return buttons;
    }

    private class CheatButton extends Button {
        private String name;
        private Material material;
        private String metadata;

        public CheatButton(String name, Material material, String metadata) {
            this.name = name;
            this.material = material;
            this.metadata = metadata;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.RED + ChatColor.BOLD.toString() + name;
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();
            description.add("");
            boolean enabled = player.hasMetadata(metadata);
            description.add(ChatColor.GRAY + "Status: " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
            description.add("");
            description.add(ChatColor.YELLOW + "Click to toggle!");
            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return material;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (player.hasMetadata(metadata)) {
                player.removeMetadata(metadata, Foxtrot.getInstance());
                if (metadata.equals("CHEAT_ABILITY")) {
                    player.removeMetadata("DEBUG", Foxtrot.getInstance());
                    player.removeMetadata("NO_COOLDOWN", Foxtrot.getInstance());
                }
                player.sendMessage(ChatColor.RED + name + " disabled.");
            } else {
                player.setMetadata(metadata, new FixedMetadataValue(Foxtrot.getInstance(), true));
                if (metadata.equals("CHEAT_ABILITY")) {
                    player.setMetadata("DEBUG", new FixedMetadataValue(Foxtrot.getInstance(), true));
                    player.setMetadata("NO_COOLDOWN", new FixedMetadataValue(Foxtrot.getInstance(), true));
                }
                player.sendMessage(ChatColor.GREEN + name + " enabled.");
            }
        }
    }
}
