package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class VoucherEditMenu extends Menu {

    @Getter private VoucherTemplate template;

    @Getter private static Map<UUID, VoucherTemplate> modifyingDisplay = new HashMap<>();
    @Getter private static Map<UUID, VoucherTemplate> modifyingMaterial = new HashMap<>();
    @Getter private static Map<UUID, VoucherTemplate> modifyingCategory = new HashMap<>();
    @Getter private static Map<UUID, VoucherTemplate> modifyingValue = new HashMap<>();
    @Getter private static Map<UUID, VoucherTemplate> modifyingDuration = new HashMap<>();

    @Override
    public String getTitle(Player player) {
        return "Edit " + template.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Display Name";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + template.getDisplayName());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit the display name.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                modifyingDisplay.put(player.getUniqueId(), template);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Provide a new display name:");
            }
        });

        buttons.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Material";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + template.getMaterial() + ":" + template.getData());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit the material.");
                lore.add(ChatColor.GRAY + "(Format: MATERIAL:DATA, e.g., INK_SACK:1)");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                Material mat = Material.matchMaterial(template.getMaterial());
                return mat != null ? mat : Material.PAPER;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) template.getData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                modifyingMaterial.put(player.getUniqueId(), template);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Provide a new material (e.g., INK_SACK:1):");
            }
        });

        buttons.put(3, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Category";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + template.getCategory());
                lore.add(ChatColor.GRAY + "Valid: RANK, MONEY, LIVES, COMMAND, STORE");
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit the category.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                modifyingCategory.put(player.getUniqueId(), template);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Provide a new category (RANK, MONEY, LIVES, COMMAND, STORE):");
            }
        });

        buttons.put(5, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Value";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + template.getValue());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit the value.");
                if (template.getCategory().equalsIgnoreCase("RANK")) {
                    lore.add(ChatColor.GRAY + "(If RANK, enter rank name)");
                } else if (template.getCategory().equalsIgnoreCase("COMMAND")) {
                    lore.add(ChatColor.GRAY + "(If COMMAND, enter the command without /)");
                } else if (template.getCategory().equalsIgnoreCase("STORE")) {
                    lore.add(ChatColor.GRAY + "(If STORE, enter the dollar amount, e.g. 5.0)");
                }
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ANVIL;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (template.getCategory().equalsIgnoreCase("RANK")) {
                    new VoucherRankListMenu(template).openMenu(player);
                } else {
                    modifyingValue.put(player.getUniqueId(), template);
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Provide a new value for the voucher:");
                }
            }
        });

        buttons.put(7, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Edit Duration";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + template.getDuration());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to edit duration (e.g., 30d, perm).");
                lore.add(ChatColor.GRAY + "Only applies to RANK categories.");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WATCH;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                modifyingDuration.put(player.getUniqueId(), template);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Provide a new duration (e.g. 30d, perm):");
            }
        });

        buttons.put(8, new BackButton(new VoucherEditorMenu()));

        return buttons;
    }
}
