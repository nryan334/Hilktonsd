package net.frozenorb.foxtrot.team.menu.system.button;

import cc.fyre.proton.menu.Button;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SystemSetHQButton extends Button {

    private final Team team;

    public SystemSetHQButton(Team team) {
        this.team = team;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Set HQ";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to set the HQ of this faction to your location.");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BEACON;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        team.setHQ(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Set HQ for " + team.getName() + " to your location.");
    }
}
