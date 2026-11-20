package net.frozenorb.foxtrot.team.menu.system;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemEditorMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "System Factions";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams().stream().filter(it -> it.getOwner() == null).collect(Collectors.toList())) {
            buttons.put(i++, new SystemButton(team));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new SystemCreateButton());
        return buttons;
    }
}
