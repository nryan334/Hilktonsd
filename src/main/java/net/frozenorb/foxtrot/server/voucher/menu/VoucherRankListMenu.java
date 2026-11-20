package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherRankListMenu extends PaginatedMenu {

    private VoucherTemplate template;

    public VoucherRankListMenu(VoucherTemplate template) {
        this.template = template;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select a Rank";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Rank rank : Neutron.getInstance().getRankHandler().getSortedValueCache()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return rank.getColor() + rank.getName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.GREEN + "Click to select this rank.");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.NAME_TAG;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    template.setValue(rank.getName());
                    template.save();
                    player.sendMessage(ChatColor.GREEN + "Set voucher value to " + rank.getName());
                    new VoucherEditMenu(template).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
