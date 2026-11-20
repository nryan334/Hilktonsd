package net.frozenorb.foxtrot.gameplay.tokenshop.menu.editor;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.tokenshop.TokenShopCategory;
import net.frozenorb.foxtrot.gameplay.tokenshop.TokenShopProduct;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenShopProductEditorMenu extends Menu {
    private final TokenShopCategory category;
    private final TokenShopProduct product;

    public TokenShopProductEditorMenu(TokenShopCategory category, TokenShopProduct product) {
        this.category = category;
        this.product = product;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Product: "
                + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', product.getDisplayName()));
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 36) {
                buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(7));
            }
        }

        buttons.put(0, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + ChatColor.BOLD.toString() + "Back to Category";
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
                new TokenShopCategoryEditorMenu(category).openMenu(player);
            }
        });

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Display Name";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: "
                        + ChatColor.translateAlternateColorCodes('&', product.getDisplayName()));
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to edit.");
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
                                return ChatColor.YELLOW + "Enter new display name (colors allowed):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                product.setDisplayName(input);
                                Foxtrot.getInstance().getTokenShopHandler().save();
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Display name updated!");
                                new TokenShopProductEditorMenu(category, product)
                                        .openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Icon Material";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + product.getMaterial().name());
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to edit.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ITEM_FRAME;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW
                                        + "Enter Material name (e.g. GOLD_INGOT, PAPER, TRIPWIRE_HOOK):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    Material mat = Material.valueOf(input.toUpperCase());
                                    product.setMaterial(mat);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Icon material updated!");
                                } catch (IllegalArgumentException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid material name.");
                                }
                                new TokenShopProductEditorMenu(category, product)
                                        .openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Icon Damage Value";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + product.getDamage());
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to edit.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter damage/data value:";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int dmg = Integer.parseInt(input);
                                    product.setDamage(dmg);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Damage value updated!");
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new TokenShopProductEditorMenu(category, product)
                                        .openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Product Slot";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current Slot: " + ChatColor.WHITE + product.getSlot());
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to edit slot.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter inventory slot (e.g. 20):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int sl = Integer.parseInt(input);
                                    product.setSlot(sl);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Slot updated!");
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new TokenShopProductEditorMenu(category, product)
                                        .openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Regular Cost";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current Cost: " + ChatColor.GOLD + product.getCost() + " Tokens");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to edit cost.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GOLD_INGOT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter regular token cost:";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int cost = Integer.parseInt(input);
                                    product.setCost(cost);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Regular cost updated!");
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new TokenShopProductEditorMenu(category, product)
                                        .openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Sale / Promotion Cost";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current Sale Cost: "
                        + (product.getPromoPrice() >= 0
                                ? ChatColor.GREEN + String.valueOf(product.getPromoPrice()) + " Tokens"
                                : ChatColor.RED + "None"));
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to edit sale price.");
                lore.add(ChatColor.YELLOW + "Set to -1 to disable sale price.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter sale cost (-1 to disable):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int promo = Integer.parseInt(input);
                                    product.setPromoPrice(promo);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Sale cost updated!");
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new TokenShopProductEditorMenu(category, product)
                                        .openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(21, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Manage Console Commands";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Commands executed on purchase:");
                for (String cmd : product.getCommands()) {
                    lore.add(ChatColor.GRAY + " - " + ChatColor.WHITE + cmd);
                }
                lore.add("");
                lore.add(ChatColor.GREEN + "Placeholders:");
                lore.add(ChatColor.GRAY + " %player% -> Player's name");
                lore.add(ChatColor.GRAY + " %player_uuid% -> Player's UUID");
                lore.add(ChatColor.GRAY + " %player_team% -> Player's Team Name");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to add a command.");
                lore.add(ChatColor.RED + "Right-Click to CLEAR all commands.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isRightClick()) {
                    product.getCommands().clear();
                    Foxtrot.getInstance().getTokenShopHandler().save();
                    player.sendMessage(ChatColor.GREEN + "Commands cleared!");
                    new TokenShopProductEditorMenu(category, product).openMenu(player);
                } else {
                    player.closeInventory();
                    ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return ChatColor.YELLOW
                                            + "Type a console command to add (do not include '/' at start):";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    product.getCommands().add(input);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom()
                                            .sendRawMessage(ChatColor.GREEN + "Command added successfully!");
                                    new TokenShopProductEditorMenu(category, product)
                                            .openMenu((Player) context.getForWhom());
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false);
                    player.beginConversation(factory.buildConversation(player));
                }
            }
        });

        buttons.put(23, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Manage Display Lore";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Lore lines:");
                for (String line : product.getLore()) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to add a lore line.");
                lore.add(ChatColor.RED + "Right-Click to CLEAR all lore lines.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (clickType.isRightClick()) {
                    product.getLore().clear();
                    Foxtrot.getInstance().getTokenShopHandler().save();
                    player.sendMessage(ChatColor.GREEN + "Lore cleared!");
                    new TokenShopProductEditorMenu(category, product).openMenu(player);
                } else {
                    player.closeInventory();
                    ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return ChatColor.YELLOW + "Type a lore line to add (colors allowed):";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    product.getLore().add(input);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Lore line added!");
                                    new TokenShopProductEditorMenu(category, product)
                                            .openMenu((Player) context.getForWhom());
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false);
                    player.beginConversation(factory.buildConversation(player));
                }
            }
        });

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 45;
    }
}
