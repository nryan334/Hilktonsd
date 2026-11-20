package net.frozenorb.foxtrot.team.menu.system.button;

import cc.fyre.proton.menu.Button;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamOpClaimCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SystemClaimButton extends Button {

    private final Team team;

    public SystemClaimButton(Team team) {
        this.team = team;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Manage Claims";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to receive the claim wand for this faction.");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.DIAMOND_HOE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        TeamOpClaimCommand.teamOpClaim(player, team);
    }
}
