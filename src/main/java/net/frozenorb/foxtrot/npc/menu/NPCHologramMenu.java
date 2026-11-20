package net.frozenorb.foxtrot.npc.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
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

public class NPCHologramMenu extends Menu {
    private final NPC npc;

    public NPCHologramMenu(NPC npc) {
        this.npc = npc;
    }

    @Override
    public String getTitle(Player player) {
        return "Edit Holograms: " + npc.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Add New Line";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(ChatColor.GRAY + "Click to add a new line to the hologram.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.YELLOW + "To add a line, use: " + ChatColor.GOLD + "/npc addline " + npc.getId() + " <text>");
                player.sendMessage("");
            }
        });

        int slot = 9;
        for (int i = 0; i < npc.getHologramLines().size(); i++) {
            final int index = i;
            String line = npc.getHologramLines().get(i);
            
            buttons.put(slot++, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', line);
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Arrays.asList(
                        ChatColor.GRAY + "Line #" + (index + 1),
                        "",
                        ChatColor.YELLOW + "Left-Click to edit text.",
                        ChatColor.RED + "Right-Click to remove line."
                    );
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType.isRightClick()) {
                        npc.getHologramLines().remove(index);
                        Foxtrot.getInstance().getNpcHandler().updateHolograms(npc);
                        openMenu(player);
                    } else {
                        player.closeInventory();
                        player.sendMessage("");
                        player.sendMessage(ChatColor.YELLOW + "To edit this line, use: " + ChatColor.GOLD + "/npc setline " + npc.getId() + " " + index + " <text>");
                        player.sendMessage("");
                    }
                }
            });
            
            if (slot >= 54) break;
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
