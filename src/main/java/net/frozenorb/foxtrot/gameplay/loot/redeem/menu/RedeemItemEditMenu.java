package net.frozenorb.foxtrot.gameplay.loot.redeem.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
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

public class RedeemItemEditMenu extends Menu {
    private final CrateItem crateItem;

    public RedeemItemEditMenu(CrateItem crateItem) {
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
                desc.add(ChatColor.GREEN + "Click to set the drop chance.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new NumericPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter chance (e.g. 50.5).";
                            }

                            @Override
                            protected Prompt acceptValidatedInput(ConversationContext ctx, Number input) {
                                crateItem.setChance(input.doubleValue());
                                Foxtrot.getInstance().getRedeemCreatorHandler().save();
                                ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Chance updated.");
                                new RedeemItemEditMenu(crateItem).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Command";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + (crateItem.getCommand().isEmpty() ? "None" : "/" + crateItem.getCommand()));
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to set command executed on drop.");
                desc.add(ChatColor.RED + "Shift-Click to clear.");
                desc.add(ChatColor.GRAY + "Use %player% for player name.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isShiftClick()) {
                    crateItem.setCommand("");
                    Foxtrot.getInstance().getRedeemCreatorHandler().save();
                    player.sendMessage(ChatColor.GREEN + "Command cleared.");
                    return;
                }
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter command (without /). Type 'cancel' to exit.";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext ctx, String input) {
                                if (input.equalsIgnoreCase("cancel")) return Prompt.END_OF_CONVERSATION;
                                crateItem.setCommand(input);
                                Foxtrot.getInstance().getRedeemCreatorHandler().save();
                                ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Command updated.");
                                new RedeemItemEditMenu(crateItem).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return (crateItem.isGiveItem() ? ChatColor.GREEN : ChatColor.RED) + "Give Item";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + (crateItem.isGiveItem() ? "Yes" : "No"));
                desc.add("");
                desc.add(ChatColor.YELLOW + "Click to toggle whether the actual");
                desc.add(ChatColor.YELLOW + "item is given to the player.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                crateItem.setGiveItem(!crateItem.isGiveItem());
                Foxtrot.getInstance().getRedeemCreatorHandler().save();
                player.sendMessage(ChatColor.YELLOW + "Give item toggled.");
                new RedeemItemEditMenu(crateItem).openMenu(player);
            }
        });

        buttons.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return (crateItem.isBroadcast() ? ChatColor.GREEN : ChatColor.RED) + "Broadcast on Win";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + (crateItem.isBroadcast() ? "Yes" : "No"));
                desc.add("");
                desc.add(ChatColor.YELLOW + "Click to toggle global broadcast.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                crateItem.setBroadcast(!crateItem.isBroadcast());
                Foxtrot.getInstance().getRedeemCreatorHandler().save();
                player.sendMessage(ChatColor.YELLOW + "Broadcast toggled.");
                new RedeemItemEditMenu(crateItem).openMenu(player);
            }
        });

        buttons.put(26, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + ChatColor.BOLD.toString() + "Go Back";
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
                new RedeemLootEditorMenu().openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
