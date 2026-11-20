package net.frozenorb.foxtrot.gameplay.tokenshop.menu.editor;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.tokenshop.TokenShopCategory;
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

public class TokenShopEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_RED + "Token Shop Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 36) {
                buttons.put(i, new net.frozenorb.foxtrot.util.GlassButton(7));
            }
        }

        int promo = Foxtrot.getInstance().getTokenShopHandler().getGlobalPromoDiscount();
        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + ChatColor.BOLD.toString() + "Global Promotion Sale";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Active Discount: " + ChatColor.YELLOW + promo + "%");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to set global discount percentage.");
                lore.add(ChatColor.YELLOW + "Set to 0 to disable global sale.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GOLD_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter global discount percentage (0 to 100):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                try {
                                    int discount = Integer.parseInt(input);
                                    if (discount < 0 || discount > 100) {
                                        context.getForWhom()
                                                .sendRawMessage(ChatColor.RED + "Must be between 0 and 100.");
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    Foxtrot.getInstance().getTokenShopHandler().setGlobalPromoDiscount(discount);
                                    Foxtrot.getInstance().getTokenShopHandler().save();
                                    context.getForWhom().sendRawMessage(
                                            ChatColor.GREEN + "Global promotion set to " + discount + "%!");
                                    new TokenShopEditorMenu().openMenu((Player) context.getForWhom());
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid number.");
                                }
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + ChatColor.BOLD.toString() + "Create New Category";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Create a new customizable category.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to create!");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW
                                        + "Enter unique ID for the new category (no spaces, e.g. abilities):";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                String id = input.toLowerCase().replaceAll("\\s+", "");
                                if (id.isEmpty()) {
                                    context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid ID.");
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                if (Foxtrot.getInstance().getTokenShopHandler().getCategory(id) != null) {
                                    context.getForWhom()
                                            .sendRawMessage(ChatColor.RED + "Category with that ID already exists.");
                                    return Prompt.END_OF_CONVERSATION;
                                }

                                TokenShopCategory cat = new TokenShopCategory();
                                cat.setId(id);
                                cat.setDisplayName("&d&l" + input);
                                cat.setMaterial(Material.CHEST);
                                cat.setSlot(20);

                                Foxtrot.getInstance().getTokenShopHandler().getCategories().add(cat);
                                Foxtrot.getInstance().getTokenShopHandler().save();
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Category created successfully!");
                                new TokenShopEditorMenu().openMenu((Player) context.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false);
                player.beginConversation(factory.buildConversation(player));
            }
        });

        int catSlot = 10;
        for (TokenShopCategory category : Foxtrot.getInstance().getTokenShopHandler().getCategories()) {
            while (catSlot < 36 && (catSlot % 9 == 0 || catSlot % 9 == 8 || catSlot == 22)) {
                catSlot++;
            }
            if (catSlot >= 36)
                break;

            buttons.put(catSlot++, new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translateAlternateColorCodes('&', category.getDisplayName()) + ChatColor.GRAY
                            + " (" + category.getId() + ")";
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Slot: " + ChatColor.WHITE + category.getSlot());
                    lore.add(ChatColor.GRAY + "Type: " + ChatColor.WHITE
                            + (category.isGkits() ? "GKits" : (category.isKeys() ? "Crate Keys" : "Custom")));
                    lore.add(ChatColor.GRAY + "Products: " + ChatColor.WHITE + category.getProducts().size());
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "Left-Click to edit category & items.");
                    lore.add(ChatColor.RED + "Right-Click to delete category.");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return category.getMaterial();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) category.getDamage();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType.isRightClick()) {
                        Foxtrot.getInstance().getTokenShopHandler().getCategories().remove(category);
                        Foxtrot.getInstance().getTokenShopHandler().save();
                        player.sendMessage(ChatColor.GREEN + "Category deleted.");
                        new TokenShopEditorMenu().openMenu(player);
                    } else {
                        new TokenShopCategoryEditorMenu(category).openMenu(player);
                    }
                }
            });
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 45;
    }
}
