package net.frozenorb.foxtrot.npc.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCManageMenu extends Menu {
    private final NPC npc;

    public NPCManageMenu(NPC npc) {
        this.npc = npc;
    }

    @Override
    public String getTitle(Player player) {
        return "Manage NPC: " + npc.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Hologram Editor";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(ChatColor.GRAY + "Configure the text lines above the NPC.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SIGN;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NPCHologramMenu(npc).openMenu(player);
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Command Editor";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(ChatColor.GRAY + "Assign commands to execute on interaction.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NPCCommandMenu(npc).openMenu(player);
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return (npc.isLookAtPlayer() ? ChatColor.GREEN : ChatColor.RED) + "Look at Player";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        ChatColor.GRAY + "Status: "
                                + (npc.isLookAtPlayer() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
                        "",
                        ChatColor.YELLOW + "Click to toggle NPC tracking nearby players.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EYE_OF_ENDER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                npc.setLookAtPlayer(!npc.isLookAtPlayer());
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Copy My Equipment";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        ChatColor.GRAY + "Copies your current armor and held item",
                        ChatColor.GRAY + "to the NPC instantly.",
                        "",
                        ChatColor.YELLOW + "Click to apply equipment.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_CHESTPLATE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                npc.setHandItem(player.getItemInHand() == null ? null : player.getItemInHand().clone());
                npc.setArmor(player.getInventory().getArmorContents().clone());
                player.sendMessage(ChatColor.GREEN + "NPC equipment updated from your inventory.");
            }
        });

        return buttons;
    }
}
