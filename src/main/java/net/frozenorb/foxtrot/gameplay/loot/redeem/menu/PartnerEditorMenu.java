package net.frozenorb.foxtrot.gameplay.loot.redeem.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerEditorMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.AQUA + "Partner Editor";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (String partner : Foxtrot.getInstance().getRedeemCreatorHandler().getCreators().values()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.AQUA + ChatColor.BOLD.toString() + partner;
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Click to remove this partner.");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return 3;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack item = super.getButtonItem(player);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    meta.setOwner(partner);
                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    Foxtrot.getInstance().getRedeemCreatorHandler().removeCreator(partner);
                    player.sendMessage(ChatColor.RED + "Removed partner: " + partner);
                    player.closeInventory();
                }
            });
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.AQUA + ChatColor.BOLD.toString() + "Add Partner";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "To add a new partner, use the command:");
                lore.add(ChatColor.WHITE + "/partner editor add <name>");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
            }
        });

        buttons.put(5, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Menu Rows";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Current Rows: " + ChatColor.WHITE + Foxtrot.getInstance().getRedeemCreatorHandler().getRows());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit the number of rows");
                lore.add(ChatColor.GREEN + "in the partner menu (1-6).");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TRIPWIRE_HOOK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
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
                                    Foxtrot.getInstance().getRedeemCreatorHandler().setRows(r);
                                    ctx.getForWhom().sendRawMessage(ChatColor.GREEN + "Set rows to " + r + ".");
                                }
                                new PartnerEditorMenu().openMenu((Player) ctx.getForWhom());
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }).withLocalEcho(false).buildConversation(player).begin();
            }
        });

        return buttons;
    }
}
