package net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.LootboxItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LootboxItemEditMenu extends Menu {
    private final Lootbox lootbox;
    private final LootboxItem item;

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Editing Item Settings";
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
            public Material getMaterial(Player player) {
                return Material.SUGAR;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + item.getChance() + "%");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to change the drop chance.");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new NumericPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter new chance (0.01 - 100.0):";
                            }

                            @Override
                            protected Prompt acceptValidatedInput(ConversationContext ctx, Number input) {
                                double chance = input.doubleValue();
                                item.setChance(chance);
                                Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Chance updated to " + chance + "%.");
                                new LootboxItemEditMenu(lootbox, item).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Toggle Broadcast";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.JUKEBOX;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + (item.isBroadcast() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to toggle chat broadcast.");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                item.setBroadcast(!item.isBroadcast());
                Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                openMenu(player);
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Execution Type";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + item.getExecutionType().name());
                lore.add("");
                lore.add(ChatColor.YELLOW + "Left-Click " + ChatColor.GRAY + "to cycle type.");
                lore.add(ChatColor.GRAY + "(CONSOLE, PLAYER, ITEM_ONLY)");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                LootboxItem.ExecutionType[] types = LootboxItem.ExecutionType.values();
                int current = Arrays.asList(types).indexOf(item.getExecutionType());
                int next = (current + 1) % types.length;
                item.setExecutionType(types[next]);
                Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                openMenu(player);
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Commands";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                if (item.getCommands().isEmpty()) {
                    lore.add(ChatColor.RED + "No commands set.");
                } else {
                    for (String cmd : item.getCommands()) {
                        lore.add(ChatColor.GRAY + " - " + ChatColor.WHITE + cmd);
                    }
                }
                lore.add("");
                lore.add(ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to add a command.");
                lore.add(ChatColor.RED + "Right-Click " + ChatColor.GRAY + "to remove last command.");
                lore.add(ChatColor.GRAY + "Shift-Click to clear all.");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isShiftClick()) {
                    item.getCommands().clear();
                } else if (clickType.isRightClick()) {
                    if (!item.getCommands().isEmpty()) {
                        item.getCommands().remove(item.getCommands().size() - 1);
                    }
                } else {
                    player.closeInventory();
                    new ConversationFactory(Foxtrot.getInstance())
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext ctx) {
                                    return ChatColor.YELLOW + "Enter command (without /, use %player%):";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext ctx, String input) {
                                    item.getCommands().add(input);
                                    Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                    new LootboxItemEditMenu(lootbox, item).openMenu((Player) ctx.getForWhom());
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false).buildConversation(player).begin();
                    return;
                }
                Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                openMenu(player);
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Back to Rewards";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new LootboxRewardsMenu(lootbox).openMenu(player);
            }
        });

        return buttons;
    }
}
