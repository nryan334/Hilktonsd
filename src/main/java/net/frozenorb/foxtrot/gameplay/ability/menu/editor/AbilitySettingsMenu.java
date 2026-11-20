package net.frozenorb.foxtrot.gameplay.ability.menu.editor;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class AbilitySettingsMenu extends Menu {

    private final Ability ability;

    public AbilitySettingsMenu(Ability ability) {
        this.ability = ability;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_RED + "Editing: " + ChatColor.stripColor(ability.getDisplayName());
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ability.getDisplayName();
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Internal Name: " + ChatColor.WHITE + ability.getName());
                lore.add(ChatColor.GRAY + "Material: " + ChatColor.WHITE + ability.getMaterial().name());
                lore.add(ChatColor.GRAY + "Category: " + ChatColor.YELLOW + ability.getCategory().name());
                if (ability.getCooldown() > 0) {
                    lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA
                            + TimeUtils.formatIntoDetailedString((int) (ability.getCooldown() / 1000)));
                } else {
                    lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.GREEN + "None");
                }
                lore.add("");
                if (ability.getLore() != null) {
                    lore.addAll(ability.getLore());
                }
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return ability.getMaterial();
            }
        });

        buttons.put(19, new Button() {
            @Override
            public String getName(Player player) {
                boolean enabled = Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability);
                return (enabled ? ChatColor.GREEN + "✔ Enabled" : ChatColor.RED + "✘ Disabled");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Click to toggle this ability");
                lore.add(ChatColor.GRAY + "on or off.");
                lore.add("");
                boolean enabled = Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability);
                lore.add(ChatColor.GRAY + "Current: "
                        + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                boolean enabled = Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability);
                return enabled ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                Foxtrot.getInstance().getMapHandler().getAbilityHandler().toggleAbility(ability);
                boolean nowEnabled = Foxtrot.getInstance().getMapHandler().getAbilityHandler()
                        .isAbilityEnabled(ability);
                player.sendMessage(ChatColor.YELLOW + ability.getName() + " is now "
                        + (nowEnabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.YELLOW
                        + ".");
                player.playSound(player.getLocation(), Sound.CLICK, 1f, 1f);
            }
        });

        buttons.put(21, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Category";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                for (Category cat : Category.values()) {
                    if (cat.getName().equalsIgnoreCase("ALL"))
                        continue;

                    boolean current = ability.getCategory() != null
                            && ability.getCategory().getName().equalsIgnoreCase(cat.getName());
                    lore.add((current ? ChatColor.GREEN + " ▶ " : ChatColor.GRAY + "   ") + cat.name());
                }
                lore.add("");
                lore.add(ChatColor.YELLOW + "Left-Click " + ChatColor.GRAY + "to cycle forward");
                lore.add(ChatColor.YELLOW + "Right-Click " + ChatColor.GRAY + "to cycle backward");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                Category[] categories = Category.values();
                List<Category> validCategories = new ArrayList<>();
                for (Category cat : categories) {
                    if (!cat.getName().equalsIgnoreCase("ALL")) {
                        validCategories.add(cat);
                    }
                }

                int currentIdx = -1;
                if (ability.getCategory() != null) {
                    for (int i = 0; i < validCategories.size(); i++) {
                        if (validCategories.get(i).getName().equalsIgnoreCase(ability.getCategory().getName())) {
                            currentIdx = i;
                            break;
                        }
                    }
                }

                if (currentIdx == -1) {
                    currentIdx = 0;
                }

                int newIdx;
                if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
                    newIdx = (currentIdx - 1 + validCategories.size()) % validCategories.size();
                } else {
                    newIdx = (currentIdx + 1) % validCategories.size();
                }

                Category newCategory = validCategories.get(newIdx);
                Foxtrot.getInstance().getMapHandler().getAbilityHandler().setAbilityCategory(ability, newCategory);
                player.sendMessage(ChatColor.YELLOW + ability.getName() + ChatColor.GRAY + " category set to "
                        + ChatColor.GOLD + newCategory.name());
                player.playSound(player.getLocation(), Sound.CLICK, 1f, 1f);
            }
        });

        buttons.put(23, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + "Cooldown";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (ability.getCooldown() > 0) {
                    lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE
                            + TimeUtils.formatIntoDetailedString((int) (ability.getCooldown() / 1000)));
                } else {
                    lore.add(ChatColor.GRAY + "Current: " + ChatColor.GREEN + "No cooldown");
                }
                lore.add("");
                lore.add(ChatColor.DARK_GRAY + "(Cooldown is set in the ability code)");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WATCH;
            }
        });

        buttons.put(25, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Give Item";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Left-Click to give 1");
                lore.add(ChatColor.GRAY + "Right-Click to give 16");
                lore.add(ChatColor.GRAY + "Shift-Click to give 64");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                int amount = 1;

                if (clickType == ClickType.RIGHT) {
                    amount = 16;
                } else if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    amount = 64;
                }

                final org.bukkit.inventory.ItemStack item = ability.getItem().clone();
                item.setAmount(amount);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GREEN + "You received " + amount + "x " + ability.getDisplayName()
                        + ChatColor.GREEN + ".");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
            }
        });

        buttons.put(31, new Button() {
            @Override
            public String getName(Player player) {
                boolean hasTag = ability.hasMetadata("META");
                return (hasTag ? ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "★ META Ability"
                        : ChatColor.GRAY + "☆ Set META Ability");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Toggle the META tag on this ability.");
                lore.add(ChatColor.GRAY + "Meta-tagged abilities can be traded at the");
                lore.add(ChatColor.GRAY + "Ability Trader NPC in Warzone.");
                lore.add("");
                boolean hasTag = ability.hasMetadata("META");
                lore.add(ChatColor.GRAY + "Status: " + (hasTag ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return ability.hasMetadata("META") ? Material.EYE_OF_ENDER : Material.ENDER_PORTAL_FRAME;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (ability.hasMetadata("META")) {
                    ability.removeMetadata("META");
                    player.sendMessage(ChatColor.RED + "Removed META tag from " + ability.getName() + ".");
                } else {
                    ability.addMetadata("META");
                    player.sendMessage(ChatColor.GREEN + "Added META tag to " + ability.getName() + ".");
                }
                Foxtrot.getInstance().getMapHandler().getAbilityHandler().saveStatistics();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);
            }
        });

        buttons.put(33, new Button() {
            @Override
            public String getName(Player player) {
                boolean hasTag = ability.hasMetadata("OP");
                return (hasTag ? ChatColor.RED + ChatColor.BOLD.toString() + "⚡ OP Ability"
                        : ChatColor.GRAY + "⚡ Set OP Ability");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Toggle the OP tag on this ability.");
                lore.add("");
                boolean hasTag = ability.hasMetadata("OP");
                lore.add(ChatColor.GRAY + "Status: " + (hasTag ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return ability.hasMetadata("OP") ? Material.DIAMOND_SWORD : Material.WOOD_SWORD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (ability.hasMetadata("OP")) {
                    ability.removeMetadata("OP");
                    player.sendMessage(ChatColor.RED + "Removed OP tag from " + ability.getName() + ".");
                } else {
                    ability.addMetadata("OP");
                    player.sendMessage(ChatColor.GREEN + "Added OP tag to " + ability.getName() + ".");
                }
                Foxtrot.getInstance().getMapHandler().getAbilityHandler().saveStatistics();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);
            }
        });

        buttons.put(40, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "← Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Return to the ability list.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new AbilityEditorMenu().openMenu(player);
            }
        });

        return buttons;
    }
}
