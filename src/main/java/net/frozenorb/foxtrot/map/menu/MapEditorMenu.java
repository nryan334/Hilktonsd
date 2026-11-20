package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.map.MapHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_RED + "Map Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        MapHandler mapHandler = Foxtrot.getInstance().getMapHandler();

        buttons.put(10, new MapSettingButton(
                Material.IRON_FENCE,
                "&c&lBorder Size",
                "&7Current: &f" + BorderListener.BORDER_SIZE,
                "&eClick to change border size.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                startConversation(player, "Enter new border size:", (input) -> {
                    try {
                        int size = Integer.parseInt(input);
                        BorderListener.BORDER_SIZE = size;
                        mapHandler.saveBorder();
                        player.sendMessage(ChatColor.GREEN + "Border size updated to " + size + "!");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                    openMenu(player);
                });
            }
        });

        buttons.put(11, new MapSettingButton(
                Material.DIAMOND_SWORD,
                "&c&lFaction Member Limit",
                "&7Current: &f" + mapHandler.getTeamSize(),
                "&eClick to change member limit.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                startConversation(player, "Enter new faction member limit:", (input) -> {
                    try {
                        int size = Integer.parseInt(input);
                        mapHandler.setTeamSize(size);
                        mapHandler.saveMapInfo();
                        player.sendMessage(ChatColor.GREEN + "Faction member limit updated to " + size + "!");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                    openMenu(player);
                });
            }
        });

        buttons.put(12, new MapSettingButton(
                Material.GOLD_SWORD,
                "&c&lAlly Limit",
                "&7Current: &f" + mapHandler.getAllyLimit(),
                "&eClick to change ally limit.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                startConversation(player, "Enter new ally limit:", (input) -> {
                    try {
                        int limit = Integer.parseInt(input);
                        mapHandler.setAllyLimit(limit);
                        mapHandler.saveMapInfo();
                        player.sendMessage(ChatColor.GREEN + "Ally limit updated to " + limit + "!");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                    openMenu(player);
                });
            }
        });

        buttons.put(13, new MapSettingButton(
                Material.LEASH,
                "&c&lAllies Enabled",
                "&7Current: &f" + (mapHandler.getAllyLimit() > 0 ? "&aEnabled" : "&cDisabled"),
                "&eClick to toggle allies.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (mapHandler.getAllyLimit() > 0) {
                    mapHandler.setAllyLimit(0);
                } else {
                    mapHandler.setAllyLimit(1);
                }
                mapHandler.saveMapInfo();
                player.sendMessage(ChatColor.GREEN + "Allies " + (mapHandler.getAllyLimit() > 0 ? "enabled (Limit: 1)" : "disabled") + "!");
                openMenu(player);
            }
        });

        buttons.put(14, new MapSettingButton(
                Material.ENDER_PORTAL_FRAME,
                "&c&lEnd Portal Location",
                "&7Current: &f" + mapHandler.getEndPortalLocation(),
                "&eClick to change portal location text.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                startConversation(player, "Enter new portal location text (e.g. 2500, 2500):", (input) -> {
                    mapHandler.setEndPortalLocation(input);
                    mapHandler.saveMapInfo();
                    player.sendMessage(ChatColor.GREEN + "End portal location text updated!");
                    openMenu(player);
                });
            }
        });

        buttons.put(15, new MapSettingButton(
                Material.SIGN,
                "&c&lScoreboard Title",
                "&7Current: &f" + mapHandler.getScoreboardTitle(),
                "&eClick to change scoreboard title.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                startConversation(player, "Enter new scoreboard title (use & for colors):", (input) -> {
                    mapHandler.setScoreboardTitle(ChatColor.translateAlternateColorCodes('&', input));
                    mapHandler.saveMapInfo();
                    player.sendMessage(ChatColor.GREEN + "Scoreboard title updated!");
                    openMenu(player);
                });
            }
        });

        buttons.put(16, new MapSettingButton(
                Material.DIAMOND_CHESTPLATE,
                "&c&lMap Kit",
                "&7Current: &f" + mapHandler.getMapKit(),
                "&eClick to change map kit text.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                startConversation(player, "Enter new map kit text (e.g. Sharp 1, Prot 1):", (input) -> {
                    mapHandler.setMapKit(input);
                    mapHandler.saveMapInfo();
                    player.sendMessage(ChatColor.GREEN + "Map kit text updated!");
                    openMenu(player);
                });
            }
        });

        buttons.put(17, new MapSettingButton(
                Material.EYE_OF_ENDER,
                "&c&lKitMap Mode",
                "&7Current: &f" + (mapHandler.isKitMap() ? "&aEnabled" : "&cDisabled"),
                "&eClick to toggle KitMap mode.") {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage(ChatColor.RED + "Toggling KitMap requires a server restart to take full effect.");
            }
        });

        return buttons;
    }

    private void startConversation(Player player, String prompt, java.util.function.Consumer<String> callback) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                .withModality(true)
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return ChatColor.YELLOW + prompt;
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        callback.accept(input);
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false);
        factory.buildConversation(player).begin();
    }

    private abstract class MapSettingButton extends Button {
        private final Material material;
        private final String name;
        private final String current;
        private final String action;

        public MapSettingButton(Material material, String name, String current, String action) {
            this.material = material;
            this.name = name;
            this.current = current;
            this.action = action;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.translateAlternateColorCodes('&', name);
        }

        @Override
        public List<String> getDescription(Player player) {
            return java.util.Arrays.asList(
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------",
                    ChatColor.translateAlternateColorCodes('&', current),
                    "",
                    ChatColor.translateAlternateColorCodes('&', action),
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
        }

        @Override
        public Material getMaterial(Player player) {
            return material;
        }
    }
}
