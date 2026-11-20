package net.frozenorb.foxtrot.gameplay.loot.crate.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CrateItemEditMenu extends Menu {
    private final Crate crate;
    private final CrateItem crateItem;

    public CrateItemEditMenu(Crate crate, CrateItem crateItem) {
        this.crate = crate;
        this.crateItem = crateItem;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Item Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Chance";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + crateItem.getChance() + "%");
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to change.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter new chance (double). Current: "
                                        + crateItem.getChance();
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    double chance = Double.parseDouble(input);
                                    crateItem.setChance(chance);
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated chance.");
                                } catch (Exception e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new CrateItemEditMenu(crate, crateItem).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Command";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + crateItem.getCommand());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to change.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW
                                        + "Enter command (use {player} and {displayName}). Type 'none' for no command.";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                crateItem.setCommand(input);
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated command.");
                                new CrateItemEditMenu(crate, crateItem).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Toggle Give Item";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: "
                        + (crateItem.isGiveItem() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to toggle.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.LEVER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                crateItem.setGiveItem(!crateItem.isGiveItem());
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Toggle Broadcast";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: "
                        + (crateItem.isBroadcast() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to toggle.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                crateItem.setBroadcast(!crateItem.isBroadcast());
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new CrateLootEditorMenu(crate).openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
