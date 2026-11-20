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

public class TokenShopCategoryEditorMenu extends Menu {
    private final TokenShopCategory category;

    public TokenShopCategoryEditorMenu(TokenShopCategory category) {
        this.category = category;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE + "Category: "
                + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', category.getDisplayName()));
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 54; i++) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 45) {
                buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(7));
            }
        }

        buttons.put(0, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + ChatColor.BOLD.toString() + "Back to Categories";
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
                new TokenShopEditorMenu().openMenu(player);
            }
        });

        buttons.put(1, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Display Name";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: "
                        + ChatColor.translateAlternateColorCodes('&', category.getDisplayName()));
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
                                category.setDisplayName(input);
                                Foxtrot.getInstance().getTokenShopHandler().save();
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Display name updated!");
                                new TokenShopCategoryEditorMenu(category).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Icon Material";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + category.getMaterial().name());
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
                                return ChatColor.YELLOW + "Enter Material name (e.g. DIAMOND, WORKBENCH, CHEST):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    Material mat = Material.valueOf(input.toUpperCase());
                                    category.setMaterial(mat);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Icon material updated!");
                                } catch (IllegalArgumentException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid material name.");
                                }
                                new TokenShopCategoryEditorMenu(category).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(3, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Icon Damage Value";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + category.getDamage());
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
                                return ChatColor.YELLOW + "Enter damage/data value (e.g. 14 for red glass):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int dmg = Integer.parseInt(input);
                                    category.setDamage(dmg);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Damage value updated!");
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new TokenShopCategoryEditorMenu(category).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Edit Category Slot";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current Slot: " + ChatColor.WHITE + category.getSlot());
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
                                return ChatColor.YELLOW + "Enter inventory slot (0 to 44):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int sl = Integer.parseInt(input);
                                    if (sl < 0 || sl > 44) {
                                        context.getForWhom()
                                                .sendRawMessage(ChatColor.RED + "Slot must be between 0 and 44.");
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    category.setSlot(sl);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Slot updated!");
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                new TokenShopCategoryEditorMenu(category).openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(5, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Is GKits Category";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "GKits Mode: "
                        + (category.isGkits() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                lore.add("");
                lore.add(ChatColor.GRAY + "If enabled, this category dynamically");
                lore.add(ChatColor.GRAY + "loads sellable GKits from Suge.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to toggle.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_CHESTPLATE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                category.setGkits(!category.isGkits());
                if (category.isGkits())
                    category.setKeys(false);
                Foxtrot.getInstance().getTokenShopHandler().save();
                player.sendMessage(ChatColor.GREEN + "GKits toggle updated!");
                new TokenShopCategoryEditorMenu(category).openMenu(player);
            }
        });

        buttons.put(6, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Is Crate Keys Category";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Crate Keys Mode: "
                        + (category.isKeys() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                lore.add("");
                lore.add(ChatColor.GRAY + "If enabled, this category dynamically");
                lore.add(ChatColor.GRAY + "loads sellable crate keys.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to toggle.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TRIPWIRE_HOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                category.setKeys(!category.isKeys());
                if (category.isKeys())
                    category.setGkits(false);
                Foxtrot.getInstance().getTokenShopHandler().save();
                player.sendMessage(ChatColor.GREEN + "Keys toggle updated!");
                new TokenShopCategoryEditorMenu(category).openMenu(player);
            }
        });

        if (!category.isGkits() && !category.isKeys()) {
            buttons.put(7, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GREEN + ChatColor.BOLD.toString() + "Add Product";
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Add a new customizable product.");
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "Click to add!");
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
                                    return ChatColor.YELLOW
                                            + "Enter unique ID for the new product (no spaces, e.g. base_token):";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    String id = input.toLowerCase().replaceAll("\\s+", "");
                                    if (id.isEmpty()) {
                                        context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid ID.");
                                        return Prompt.END_OF_CONVERSATION;
                                    }

                                    boolean exists = category.getProducts().stream()
                                            .anyMatch(p -> p.getId().equalsIgnoreCase(id));
                                    if (exists) {
                                        context.getForWhom().sendRawMessage(ChatColor.RED
                                                + "Product with that ID already exists in this category.");
                                        return Prompt.END_OF_CONVERSATION;
                                    }

                                    TokenShopProduct prod = new TokenShopProduct();
                                    prod.setId(id);
                                    prod.setDisplayName("&a&l" + input);
                                    prod.setMaterial(Material.STONE);
                                    prod.setSlot(20);
                                    prod.setCost(5);

                                    category.getProducts().add(prod);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom()
                                            .sendRawMessage(ChatColor.GREEN + "Product created successfully!");
                                    new TokenShopCategoryEditorMenu(category).openMenu((Player) context.getForWhom());
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false);
                    player.beginConversation(factory.buildConversation(player));
                }
            });
        }

        if (!category.isGkits() && !category.isKeys()) {
            int prodSlot = 9;
            for (TokenShopProduct product : category.getProducts()) {
                while (prodSlot < 45 && (prodSlot % 9 == 0 || prodSlot % 9 == 8)) {
                    prodSlot++;
                }
                if (prodSlot >= 45)
                    break;

                buttons.put(prodSlot++, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.translateAlternateColorCodes('&', product.getDisplayName()) + ChatColor.GRAY
                                + " (" + product.getId() + ")";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "Slot: " + ChatColor.WHITE + product.getSlot());
                        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.GOLD + product.getCost() + " Tokens");
                        if (product.getPromoPrice() >= 0) {
                            lore.add(ChatColor.GRAY + "Promo Cost: " + ChatColor.GREEN + product.getPromoPrice()
                                    + " Tokens");
                        }
                        lore.add(ChatColor.GRAY + "Commands: " + ChatColor.WHITE + product.getCommands().size());
                        lore.add("");
                        lore.add(ChatColor.YELLOW + "Left-Click to edit product.");
                        lore.add(ChatColor.RED + "Right-Click to delete product.");
                        return lore;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return product.getMaterial();
                    }

                    @Override
                    public byte getDamageValue(Player player) {
                        return (byte) product.getDamage();
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        if (clickType.isRightClick()) {
                            category.getProducts().remove(product);
                            Foxtrot.getInstance().getTokenShopHandler().save();
                            player.sendMessage(ChatColor.GREEN + "Product deleted.");
                            new TokenShopCategoryEditorMenu(category).openMenu(player);
                        } else {
                            new TokenShopProductEditorMenu(category, product).openMenu(player);
                        }
                    }
                });
            }
        } else {
            buttons.put(22, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GRAY + ChatColor.ITALIC.toString() + "Dynamic Category";
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "This category manages its items");
                    lore.add(ChatColor.GRAY + "automatically from Foxtrot/Suge.");
                    lore.add(ChatColor.GRAY + "Individual products cannot be added.");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.REDSTONE_BLOCK;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                }
            });
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 54;
    }
}
