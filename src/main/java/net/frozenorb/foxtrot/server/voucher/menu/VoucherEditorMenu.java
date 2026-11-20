package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherEditorMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Voucher Editor";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (VoucherTemplate template : Foxtrot.getInstance().getVoucherHandler().getTemplates()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GOLD + template.getId();
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
                    lore.add(ChatColor.YELLOW + "Display: " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', template.getDisplayName()));
                    lore.add(ChatColor.YELLOW + "Category: " + ChatColor.WHITE + template.getCategory());
                    lore.add(ChatColor.YELLOW + "Value: " + ChatColor.WHITE + template.getValue());
                    lore.add(ChatColor.YELLOW + "Duration: " + ChatColor.WHITE + template.getDuration());
                    lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
                    lore.add(ChatColor.GREEN + "Click to edit voucher.");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new VoucherEditMenu(template).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
