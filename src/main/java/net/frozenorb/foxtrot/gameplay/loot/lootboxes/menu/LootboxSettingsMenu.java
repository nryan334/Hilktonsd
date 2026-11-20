package net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LootboxSettingsMenu extends Menu {
    private final Lootbox lootbox;

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Lootbox Settings: " + lootbox.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Reward Count";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GOLD_INGOT;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + lootbox.getRewardCount());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to change how many rewards");
                lore.add(ChatColor.GREEN + "a player gets when opening this box.");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new NumericPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter number of rewards (1-10):";
                            }

                            @Override
                            protected Prompt acceptValidatedInput(ConversationContext ctx, Number input) {
                                int count = input.intValue();
                                if (count < 1 || count > 10) {
                                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Count must be between 1 and 10.");
                                } else {
                                    lootbox.setRewardCount(count);
                                    Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                    ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Reward count updated to " + count + ".");
                                }
                                new LootboxSettingsMenu(lootbox).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Store URL";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + lootbox.getStoreUrl());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to change the store URL.");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter new Store URL:";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext ctx, String input) {
                                lootbox.setStoreUrl(input);
                                Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Store URL updated.");
                                new LootboxSettingsMenu(lootbox).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Change Display Name";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.translateAlternateColorCodes('&', lootbox.getDisplayName()));
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to change the display name.");
                return lore;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter new display name (use & for colors). Type 'cancel' to stop:";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext ctx, String input) {
                                if (!input.equalsIgnoreCase("cancel")) {
                                    lootbox.setDisplayName(input);
                                    Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                    ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Display name updated.");
                                }
                                new LootboxSettingsMenu(lootbox).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Back";
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
                new LootboxEditorMenu(lootbox).openMenu(player);
            }
        });

        return buttons;
    }
}
