package net.frozenorb.foxtrot.team.menu.system;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.system.button.SystemClaimButton;
import net.frozenorb.foxtrot.team.menu.system.button.SystemFlagsButton;
import net.frozenorb.foxtrot.team.menu.system.button.SystemRenameButton;
import net.frozenorb.foxtrot.team.menu.system.button.SystemSetHQButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SystemEditMenu extends Menu {

    private final Team team;

    public SystemEditMenu(Team team) {
        this.team = team;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new SystemRenameButton(team));
        buttons.put(12, new SystemFlagsButton(team));
        buttons.put(14, new SystemClaimButton(team));
        buttons.put(16, new SystemSetHQButton(team));

        for (int i = 0; i < 27; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15));
            }
        }

        return buttons;
    }
}
