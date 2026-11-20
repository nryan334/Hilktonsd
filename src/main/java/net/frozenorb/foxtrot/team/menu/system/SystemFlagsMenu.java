package net.frozenorb.foxtrot.team.menu.system;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemFlagsMenu extends Menu {

    private final Team team;

    public SystemFlagsMenu(Team team) {
        this.team = team;
    }

    @Override
    public String getTitle(Player player) {
        return "Manage Flags - " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (DTRBitmask bitmask : DTRBitmask.values()) {
            buttons.put(i++, new Button() {
                @Override
                public String getName(Player player) {
                    boolean has = team.hasDTRBitmask(bitmask);
                    return (has ? ChatColor.GREEN : ChatColor.RED) + bitmask.getName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + bitmask.getDescription());
                    lore.add("");
                    lore.add(team.hasDTRBitmask(bitmask) ? ChatColor.RED + "Click to remove flag" : ChatColor.GREEN + "Click to add flag");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return team.hasDTRBitmask(bitmask) ? Material.WOOL : Material.STAINED_GLASS_PANE;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) (team.hasDTRBitmask(bitmask) ? 13 : 14);
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    int dtr = (int) team.getDTR();
                    if (team.hasDTRBitmask(bitmask)) {
                        dtr -= bitmask.getBitmask();
                        player.sendMessage(ChatColor.RED + "Removed " + bitmask.getName() + " flag from " + team.getName() + ".");
                    } else {
                        dtr += bitmask.getBitmask();
                        player.sendMessage(ChatColor.GREEN + "Added " + bitmask.getName() + " flag to " + team.getName() + ".");
                    }
                    team.setDTR(dtr);
                }
            });
        }

        return buttons;
    }
}
