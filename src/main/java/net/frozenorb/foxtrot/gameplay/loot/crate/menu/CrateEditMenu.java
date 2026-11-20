package net.frozenorb.foxtrot.gameplay.loot.crate.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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

public class CrateEditMenu extends Menu {
    private final Crate crate;

    public CrateEditMenu(Crate crate) {
        this.crate = crate;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing " + crate.getId();
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
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.translateAlternateColorCodes('&', crate.getDisplayName()));
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to change the key item name.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter new display name (use & for colors). Current: "
                                        + crate.getDisplayName();
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext ctx, String input) {
                                if (crate.getHologramLines().size() == 1 && crate.getHologramLines().get(0).equals(crate.getDisplayName())) {
                                    crate.getHologramLines().set(0, input);
                                    Foxtrot.getInstance().getCrateBlockHandler().refreshHolograms(crate.getId());
                                }
                                crate.setDisplayName(input);
                                ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated display name.");
                                new CrateEditMenu(crate).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Key Lore";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                for (String line : crate.getLore())
                    desc.add(ChatColor.translateAlternateColorCodes('&', line));
                desc.add("");
                desc.add(ChatColor.GREEN + "Left-Click to add a line.");
                desc.add(ChatColor.RED + "Right-Click to remove last line.");
                desc.add(ChatColor.GRAY + "Shift + Click to clear all.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isShiftClick()) {
                    crate.getLore().clear();
                    return;
                }
                if (clickType.isLeftClick()) {
                    player.closeInventory();
                    new ConversationFactory(Foxtrot.getInstance())
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext ctx) {
                                    return ChatColor.YELLOW + "Enter new lore line (use & for colors).";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext ctx, String input) {
                                    crate.getLore().add(input);
                                    new CrateEditMenu(crate).openMenu((Player) ctx.getForWhom());
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false).buildConversation(player).begin();
                } else {
                    if (!crate.getLore().isEmpty())
                        crate.getLore().remove(crate.getLore().size() - 1);
                }
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Key Material";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + crate.getMaterial().name() + ":"
                        + crate.getData());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click with item in hand to set it.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return crate.getMaterial();
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) crate.getData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                ItemStack inHand = player.getItemInHand();
                if (inHand != null && inHand.getType() != Material.AIR) {
                    crate.setMaterial(inHand.getType());
                    crate.setData(inHand.getDurability());
                    player.sendMessage(ChatColor.GREEN + "Set key to " + inHand.getType().name() + ":"
                            + inHand.getDurability() + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "Hold an item in your hand!");
                }
            }
        });

        buttons.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Firework Color";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current RGB: " + ChatColor.WHITE + crate.getFireworkColor().asRGB());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to set by RGB integer.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.FIREWORK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter Firework Color as RGB integer (e.g. 16753920).";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext ctx, String input) {
                                try {
                                    crate.setFireworkColor(Color.fromRGB(Integer.parseInt(input)));
                                    ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Updated color.");
                                } catch (Exception e) {
                                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new CrateEditMenu(crate).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + ChatColor.BOLD.toString() + "Edit Hologram Lines";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                List<String> lines = crate.getHologramLines();
                desc.add(ChatColor.GRAY + "Lines (" + lines.size() + "):");
                for (int i = 0; i < lines.size(); i++) {
                    desc.add(ChatColor.WHITE + "  " + (i + 1) + ". " + ChatColor.translateAlternateColorCodes('&', lines.get(i)));
                }
                desc.add("");
                desc.add(ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to add a line.");
                desc.add(ChatColor.RED + "Right-Click " + ChatColor.GRAY + "to remove last line.");
                desc.add(ChatColor.GRAY + "Shift+Click to clear all.");
                desc.add("");
                desc.add(ChatColor.YELLOW + "Tip: 1 line = at chest level.");
                desc.add(ChatColor.YELLOW + "     2+ lines = stacked above.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SIGN;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isShiftClick()) {
                    crate.getHologramLines().clear();
                    player.sendMessage(ChatColor.RED + "Cleared all hologram lines.");
                    Foxtrot.getInstance().getCrateHandler().saveData();
                    Foxtrot.getInstance().getCrateBlockHandler().refreshHolograms(crate.getId());
                    return;
                }
                if (clickType.isRightClick()) {
                    List<String> lines = crate.getHologramLines();
                    if (!lines.isEmpty()) {
                        lines.remove(lines.size() - 1);
                        player.sendMessage(ChatColor.RED + "Removed last hologram line.");
                        Foxtrot.getInstance().getCrateHandler().saveData();
                        Foxtrot.getInstance().getCrateBlockHandler().refreshHolograms(crate.getId());
                    } else {
                        player.sendMessage(ChatColor.RED + "No hologram lines to remove!");
                    }
                    return;
                }
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter the hologram line text (use & for colors):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext ctx, String input) {
                                crate.getHologramLines().add(input);
                                ctx.getForWhom()
                                        .sendRawMessage(ChatColor.GREEN + "Added: " + ChatColor.translateAlternateColorCodes('&', input));
                                Foxtrot.getInstance().getCrateHandler().saveData();
                                Foxtrot.getInstance().getCrateBlockHandler().refreshHolograms(crate.getId());
                                new CrateEditMenu(crate).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Edit Loot";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new CrateLootEditorMenu(crate).openMenu(player);
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Menu Rows";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current Rows: " + ChatColor.WHITE + crate.getRows());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click to edit the number of rows");
                desc.add(ChatColor.GREEN + "in the crate preview menu (1-6).");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TRIPWIRE_HOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new NumericPrompt() {
                            @Override
                            public String getPromptText(ConversationContext ctx) {
                                return ChatColor.YELLOW + "Enter number of rows (1-6).";
                            }

                            @Override
                            protected Prompt acceptValidatedInput(ConversationContext ctx, Number input) {
                                int r = input.intValue();
                                if (r < 1 || r > 6) {
                                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Rows must be between 1 and 6.");
                                } else {
                                    crate.setRows(r);
                                    ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Set rows to " + r + ".");
                                }
                                new CrateEditMenu(crate).openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        buttons.put(17, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Filler Item";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: "
                        + (crate.getFillerItem() != null ? ChatColor.WHITE + crate.getFillerItem().getType().name()
                                : ChatColor.RED + "None"));
                desc.add("");
                desc.add(ChatColor.GREEN + "Click with item in hand to set it.");
                desc.add(ChatColor.RED + "Shift-Click to remove the filler item.");
                desc.add(ChatColor.GRAY + "This item fills empty slots in the crate preview.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return crate.getFillerItem() != null ? crate.getFillerItem().getType() : Material.STAINED_GLASS_PANE;
            }

            @Override
            public byte getDamageValue(Player player) {
                return crate.getFillerItem() != null ? (byte) crate.getFillerItem().getDurability() : 7;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isShiftClick()) {
                    crate.setFillerItem(null);
                    player.sendMessage(ChatColor.GREEN + "Removed filler item.");
                    return;
                }
                ItemStack inHand = player.getItemInHand();
                if (inHand != null && inHand.getType() != Material.AIR) {
                    crate.setFillerItem(inHand.clone());
                    player.sendMessage(ChatColor.GREEN + "Set filler item to " + inHand.getType().name() + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "Hold an item in your hand to set it!");
                }
            }
        });

        buttons.put(18, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Block Material";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + crate.getBlockMaterial().name() + ":"
                        + crate.getBlockData());
                desc.add("");
                desc.add(ChatColor.GREEN + "Click with item in hand to set it.");
                desc.add(ChatColor.GRAY + "This updates the physical block in the world.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return crate.getBlockMaterial() != null ? crate.getBlockMaterial() : Material.CHEST;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) crate.getBlockData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                ItemStack inHand = player.getItemInHand();
                if (inHand != null && inHand.getType() != Material.AIR && inHand.getType().isBlock()) {
                    crate.setBlockMaterial(inHand.getType());
                    crate.setBlockData((short) inHand.getDurability());
                    player.sendMessage(ChatColor.GREEN + "Set crate block to " + inHand.getType().name() + ":"
                            + inHand.getDurability() + ".");

                    Foxtrot.getInstance().getCrateBlockHandler().getCrateBlocks().stream()
                            .filter(b -> b.getCrateId().equalsIgnoreCase(crate.getId()))
                            .forEach(b -> {
                                b.getLocation().getBlock().setType(crate.getBlockMaterial());
                                b.getLocation().getBlock().setData((byte) crate.getBlockData());
                            });
                } else {
                    player.sendMessage(ChatColor.RED + "Hold a BLOCK in your hand!");
                }
            }
        });

        buttons.put(35, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "Save Crate";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WOOL;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 13;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                Foxtrot.getInstance().getCrateHandler().saveData();
                Foxtrot.getInstance().getCrateBlockHandler().refreshHolograms(crate.getId());
                player.sendMessage(ChatColor.GREEN + "Crate saved and holograms refreshed.");
                new CrateEditorMenu().openMenu(player);
            }
        });

        buttons.put(19, new Button() {
            @Override
            public String getName(Player player) {
                return (crate.isSellableOnTokenShop() ? ChatColor.GREEN : ChatColor.RED) + "Sellable on TokenShop";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + (crate.isSellableOnTokenShop() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                desc.add("");
                desc.add(ChatColor.YELLOW + "Click to toggle whether this crate");
                desc.add(ChatColor.YELLOW + "appears in the TokenShop.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                crate.setSellableOnTokenShop(!crate.isSellableOnTokenShop());
                player.sendMessage(ChatColor.YELLOW + "Crate " + crate.getId() + " is now " + (crate.isSellableOnTokenShop() ? ChatColor.GREEN + "sellable" : ChatColor.RED + "hidden") + ChatColor.YELLOW + " on the TokenShop.");
                new CrateEditMenu(crate).openMenu(player);
            }
        });

        buttons.put(20, new Button() {
            @Override
            public String getName(Player player) {
                return (crate.isFillerGlow() ? ChatColor.GREEN : ChatColor.RED) + "Filler Glow";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(ChatColor.GRAY + "Current: " + (crate.isFillerGlow() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                desc.add("");
                desc.add(ChatColor.YELLOW + "Click to toggle whether the filler item glows.");
                desc.add(ChatColor.GRAY + "If enabled, the filler item will glow and");
                desc.add(ChatColor.GRAY + "its name and enchantments will be hidden.");
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GLOWSTONE_DUST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                crate.setFillerGlow(!crate.isFillerGlow());
                player.sendMessage(ChatColor.YELLOW + "Filler glow for crate " + crate.getId() + " is now " + (crate.isFillerGlow() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.YELLOW + ".");
                new CrateEditMenu(crate).openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 36;
    }
}
