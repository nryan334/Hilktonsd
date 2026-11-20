package net.frozenorb.foxtrot.gameplay.loot.airdrop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDrop;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class AirDropRewardMenu extends Menu {
    private final AirDrop airDrop;

    public AirDropRewardMenu(AirDrop airDrop) {
        this.airDrop = airDrop;
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Loot: " + airDrop.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "Add Item In Hand";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Adds the item you are currently");
                lore.add(ChatColor.GRAY + "holding to the airdrop rewards.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to add with 1.0% chance.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                ItemStack hand = player.getItemInHand();
                if (hand == null || hand.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You must hold an item!");
                    return;
                }

                airDrop.getRewards().add(new AirDropReward(hand.clone(), 1.0, "none", true));
                Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                player.sendMessage(ChatColor.GREEN + "Added item to " + airDrop.getId());
            }
        });

        buttons.put(1, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Bulk Add Rewards";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Opens an inventory where you can");
                lore.add(ChatColor.GRAY + "drop multiple items at once.");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to open bulk editor.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new AirDropSettingsMenu(airDrop).openBulkEditor(player);
            }
        });

        buttons.put(8, new Button() {
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
                new AirDropSettingsMenu(airDrop).openMenu(player);
            }
        });

        int slot = 9;
        for (int i = 0; i < airDrop.getRewards().size(); i++) {
            AirDropReward reward = airDrop.getRewards().get(i);
            buttons.put(slot++, new RewardButton(reward, airDrop));
        }

        return buttons;
    }

    private static class RewardButton extends Button {
        private final AirDropReward reward;
        private final AirDrop airDrop;

        public RewardButton(AirDropReward reward, AirDrop airDrop) {
            this.reward = reward;
            this.airDrop = airDrop;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Reward";
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Chance: " + ChatColor.WHITE + reward.getChance() + "%");
            lore.add(ChatColor.YELLOW + "Command: " + ChatColor.WHITE
                    + (reward.getCommand().equals("none") ? "None" : reward.getCommand()));
            lore.add(ChatColor.YELLOW + "Grant Item: " + ChatColor.WHITE + (reward.isGrantItem() ? "Yes" : "No"));
            lore.add("");
            lore.add(ChatColor.GREEN + "Left-Click to increase chance (+1.0)");
            lore.add(ChatColor.RED + "Right-Click to decrease chance (-1.0)");
            lore.add(ChatColor.AQUA + "Shift-Click to toggle Grant Item");
            lore.add(ChatColor.DARK_RED + "Press Q to delete");
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return reward.getItemStack() != null ? reward.getItemStack().getType() : Material.BEDROCK;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            if (reward.getItemStack() == null)
                return super.getButtonItem(player);
            return reward.getItemStack().clone();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType == ClickType.DROP) {
                airDrop.getRewards().remove(reward);
                Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                player.sendMessage(ChatColor.RED + "Deleted reward.");
                return;
            }

            new AirDropItemEditMenu(airDrop, reward).openMenu(player);
        }
    }
}
