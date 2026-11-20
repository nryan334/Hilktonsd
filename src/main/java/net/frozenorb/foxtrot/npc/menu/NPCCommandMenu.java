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

public class NPCCommandMenu extends Menu {
    private final NPC npc;

    public NPCCommandMenu(NPC npc) {
        this.npc = npc;
    }

    @Override
    public String getTitle(Player player) {
        return "Command Editor - " + npc.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Add Command";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                    ChatColor.GRAY + "Add a command to execute on click.",
                    ChatColor.GRAY + "Use " + ChatColor.WHITE + "{player}" + ChatColor.GRAY + " as placeholder.",
                    "",
                    ChatColor.YELLOW + "Click to see instruction."
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.YELLOW + "To add a command, use: " + ChatColor.GOLD + "/npc addcommand " + npc.getId() + " <command>");
                player.sendMessage(ChatColor.GRAY + "Example: /npc addcommand " + npc.getId() + " spawn");
                player.sendMessage("");
            }
        });

        buttons.put(6, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Execution Mode";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                    ChatColor.GRAY + "Current: " + (npc.isExecuteAsPlayer() ? ChatColor.BLUE + "Execute as Player" : ChatColor.DARK_RED + "Execute as Console"),
                    "",
                    ChatColor.YELLOW + "Click to toggle execution mode."
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                npc.setExecuteAsPlayer(!npc.isExecuteAsPlayer());
                Foxtrot.getInstance().getNpcHandler().updateNPC(npc);
            }
        });

        int slot = 9;
        for (int i = 0; i < npc.getCommands().size(); i++) {
            final int index = i;
            String cmd = npc.getCommands().get(i);

            buttons.put(slot++, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.AQUA + "/" + cmd;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Arrays.asList(
                        "",
                        ChatColor.RED + "Right-Click to remove this command."
                    );
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType.isRightClick()) {
                        npc.getCommands().remove(index);
                        Foxtrot.getInstance().getNpcHandler().updateNPC(npc);
                        openMenu(player);
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
