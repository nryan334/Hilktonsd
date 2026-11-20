package net.frozenorb.foxtrot.team.menu.system.button;

import cc.fyre.proton.menu.Button;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.system.SystemFlagsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SystemFlagsButton extends Button {

    private final Team team;

    public SystemFlagsButton(Team team) {
        this.team = team;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Manage Flags";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to manage the flags of this faction.");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SIGN;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new SystemFlagsMenu(team).openMenu(player);
    }
}
