package net.frozenorb.foxtrot.team.menu.system.button;

import cc.fyre.proton.menu.Button;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SystemRenameButton extends Button {

    private final Team team;

    public SystemRenameButton(Team team) {
        this.team = team;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Rename Faction";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to rename this faction.");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/f rename " + team.getName() + " <newName>" + ChatColor.YELLOW + " to rename this faction.");
    }
}
