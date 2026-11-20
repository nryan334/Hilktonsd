package net.frozenorb.foxtrot.server.reclaim.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
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

public class ReclaimEditMenu extends Menu {

    private final Reclaim reclaim;

    public ReclaimEditMenu(Reclaim reclaim) {
        this.reclaim = reclaim;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing " + reclaim.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + "Edit Items";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Add or remove items to be");
                lore.add(ChatColor.GRAY + "given when reclaimed.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Current Items: " + ChatColor.WHITE + reclaim.getItems().size());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ReclaimItemEditMenu(reclaim).openMenu(player);
            }
        });

        buttons.put(3, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + "View Commands";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                for (String cmd : reclaim.getCommands()) {
                    lore.add(ChatColor.GRAY + "- " + cmd);
                }
                lore.add("");
                lore.add(ChatColor.YELLOW + "Note: To add or remove commands,");
                lore.add(ChatColor.YELLOW + "use the /reclaimadmin command.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }
        });

        buttons.put(5, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + "Change Priority";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Current Priority: " + ChatColor.WHITE + reclaim.getPriority());
                lore.add("");
                lore.add(ChatColor.GREEN + "Right-Click to increase.");
                lore.add(ChatColor.RED + "Left-Click to decrease.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isRightClick()) {
                    reclaim.setPriority(reclaim.getPriority() + 1);
                } else if (clickType.isLeftClick()) {
                    reclaim.setPriority(reclaim.getPriority() - 1);
                }
                net.frozenorb.foxtrot.Foxtrot.getInstance().getReclaimHandler().saveReclaim(reclaim);
            }
        });

        buttons.put(7, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Go Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BED;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ReclaimEditorMenu().openMenu(player);
            }
        });

        return buttons;
    }
}
