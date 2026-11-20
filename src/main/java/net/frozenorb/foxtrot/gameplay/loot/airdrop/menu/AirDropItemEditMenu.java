package net.frozenorb.foxtrot.gameplay.loot.airdrop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDrop;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
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

public class AirDropItemEditMenu extends Menu {
    private final AirDrop airDrop;
    private final AirDropReward reward;

    public AirDropItemEditMenu(AirDrop airDrop, AirDropReward reward) {
        this.airDrop = airDrop;
        this.reward = reward;
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Reward Settings";
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
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + reward.getChance() + "%");
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
                                return ChatColor.YELLOW + "Enter new chance (double). Current: " + reward.getChance();
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    double chance = Double.parseDouble(input);
                                    reward.setChance(chance);
                                    Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated chance.");
                                } catch (Exception e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new AirDropItemEditMenu(airDrop, reward).openMenu((Player) context.getForWhom());
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
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + reward.getCommand());
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
                                reward.setCommand(input);
                                Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated command.");
                                new AirDropItemEditMenu(airDrop, reward).openMenu((Player) context.getForWhom());
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
                        + (reward.isGrantItem() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
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
                reward.setGrantItem(!reward.isGrantItem());
                Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
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
                new AirDropRewardMenu(airDrop).openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
