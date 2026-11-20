package net.frozenorb.foxtrot.gameplay.loot.airdrop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDrop;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirDropSettingsMenu extends Menu {
    private final AirDrop airDrop;

    public AirDropSettingsMenu(AirDrop airDrop) {
        this.airDrop = airDrop;
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "AirDrop: " + airDrop.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Change Display Name";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + airDrop.getDisplayName());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to change display name.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter new display name (colors supported). Current: "
                                        + airDrop.getDisplayName();
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                airDrop.setDisplayName(CC.translate(input));
                                Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated display name.");
                                new AirDropSettingsMenu(airDrop).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Rewards";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current Rewards: " + ChatColor.WHITE + airDrop.getRewards().size());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to manage rewards.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new AirDropRewardMenu(airDrop).openMenu(player);
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Bulk Add Rewards";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Open an inventory to drop items.");
                lore.add(ChatColor.GRAY + "Every item added will have 1.0% chance.");
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
                openBulkEditor(player);
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Change Icon";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + airDrop.getMaterial().name());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click with an item in hand to change icon.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return airDrop.getMaterial();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                ItemStack hand = player.getItemInHand();
                if (hand == null || hand.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You must hold an item!");
                    return;
                }

                airDrop.setMaterial(hand.getType());
                airDrop.setData(hand.getDurability());
                Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                player.sendMessage(ChatColor.GREEN + "Updated icon.");
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
                new AirDropEditorMenu().openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    void openBulkEditor(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Bulk Add: " + airDrop.getId());

        player.openInventory(inventory);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getInventory().equals(inventory)) {
                    int added = 0;
                    for (ItemStack item : event.getInventory().getContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            airDrop.getRewards().add(new AirDropReward(item.clone(), 1.0, "none", true));
                            added++;
                        }
                    }
                    if (added > 0) {
                        Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
                        player.sendMessage(
                                ChatColor.GREEN + "Bulk added " + added + " items to " + airDrop.getId() + ".");
                    }
                    InventoryCloseEvent.getHandlerList().unregister(this);
                }
            }
        }, Foxtrot.getInstance());
    }
}
