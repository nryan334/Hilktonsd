package net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.event.inventory.ClickType;

@AllArgsConstructor
public class LootboxEditorMenu extends Menu {
    private final Lootbox lootbox;

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Editing " + lootbox.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Display Name";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.translateAlternateColorCodes('&', lootbox.getDisplayName()));
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to change the lootbox name.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new org.bukkit.conversations.ConversationFactory(net.frozenorb.foxtrot.Foxtrot.getInstance())
                        .withFirstPrompt(new org.bukkit.conversations.StringPrompt() {
                            @Override
                            public String getPromptText(org.bukkit.conversations.ConversationContext ctx) {
                                return ChatColor.YELLOW
                                        + "Enter new display name (use & for colors). Type 'cancel' to stop. Current: "
                                        + lootbox.getDisplayName();
                            }

                            @Override
                            public org.bukkit.conversations.Prompt acceptInput(
                                    org.bukkit.conversations.ConversationContext ctx, String input) {
                                if (input.equalsIgnoreCase("cancel")) {
                                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Rename cancelled.");
                                } else {
                                    lootbox.setDisplayName(input);
                                    net.frozenorb.foxtrot.Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                    ctx.getForWhom().sendRawMessage(
                                            ChatColor.GREEN + "Updated display name to: " + ChatColor.translateAlternateColorCodes('&', input));
                                }

                                Bukkit.getScheduler().runTask(net.frozenorb.foxtrot.Foxtrot.getInstance(), () -> {
                                    new LootboxEditorMenu(lootbox).openMenu((Player) ctx.getForWhom());
                                });
                                return org.bukkit.conversations.Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Lore";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                for (String line : lootbox.getLore()) {
                    desc.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                desc.add("");
                desc.add(ChatColor.GREEN + "Left-Click to add a line.");
                desc.add(ChatColor.RED + "Right-Click to remove last line.");
                desc.add(ChatColor.GRAY + "Shift-Click to clear all.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isShiftClick()) {
                    lootbox.getLore().clear();
                } else if (clickType.isRightClick()) {
                    if (!lootbox.getLore().isEmpty()) {
                        lootbox.getLore().remove(lootbox.getLore().size() - 1);
                    }
                } else {
                    player.closeInventory();
                    new org.bukkit.conversations.ConversationFactory(net.frozenorb.foxtrot.Foxtrot.getInstance())
                            .withFirstPrompt(new org.bukkit.conversations.StringPrompt() {
                                @Override
                                public String getPromptText(org.bukkit.conversations.ConversationContext ctx) {
                                    return ChatColor.YELLOW + "Enter new lore line (use & for colors):";
                                }

                                @Override
                                public org.bukkit.conversations.Prompt acceptInput(
                                        org.bukkit.conversations.ConversationContext ctx, String input) {
                                    lootbox.getLore().add(input);
                                    net.frozenorb.foxtrot.Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                                    new LootboxEditorMenu(lootbox).openMenu((Player) ctx.getForWhom());
                                    return org.bukkit.conversations.Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false).buildConversation(player).begin();
                    return;
                }
                net.frozenorb.foxtrot.Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                openMenu(player);
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Material";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + lootbox.getMaterial().name() + ":"
                        + lootbox.getData());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click with item in hand to set it.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return lootbox.getMaterial();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
                    lootbox.setMaterial(player.getItemInHand().getType());
                    lootbox.setData(player.getItemInHand().getDurability());
                    net.frozenorb.foxtrot.Foxtrot.getInstance().getLootboxHandler().save(lootbox);
                    player.sendMessage(
                            ChatColor.GREEN + "Lootbox icon updated to " + lootbox.getMaterial().name() + ".");
                    openMenu(player);
                } else {
                    player.closeInventory();
                    player.sendMessage(ChatColor.YELLOW
                            + "Click an item in your inventory to set it as the icon, or hold it and click the button again.");
                    Bukkit.getScheduler().runTaskLater(net.frozenorb.foxtrot.Foxtrot.getInstance(),
                            () -> openMenu(player), 40L);
                }
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Rewards";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Total Rewards: " + ChatColor.WHITE + lootbox.getItems().size());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to manage lootbox rewards.");
                return desc;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new LootboxRewardsMenu(lootbox).openMenu(player);
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Lootbox Settings";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE_COMPARATOR;
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Reward Count: " + ChatColor.WHITE + lootbox.getRewardCount());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to edit general settings.");
                return desc;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new LootboxSettingsMenu(lootbox).openMenu(player);
            }
        });

        return buttons;
    }
}
