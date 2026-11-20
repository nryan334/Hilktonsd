package net.frozenorb.foxtrot.team.menu.system;

import cc.fyre.proton.menu.Button;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SystemButton extends Button {

    private final Team team;

    public SystemButton(Team team) {
        this.team = team;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + team.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
        lore.add(ChatColor.YELLOW + "Flags:");

        boolean hasFlags = false;
        for (DTRBitmask bitmask : DTRBitmask.values()) {
            if (team.hasDTRBitmask(bitmask)) {
                lore.add(ChatColor.GRAY + " - " + ChatColor.GREEN + bitmask.getName());
                hasFlags = true;
            }
        }

        if (!hasFlags) {
            lore.add(ChatColor.GRAY + " - " + ChatColor.RED + "None");
        }

        lore.add("");
        lore.add(ChatColor.YELLOW + "Claims: " + ChatColor.WHITE + team.getClaims().size());
        lore.add(ChatColor.YELLOW + "HQ: " + ChatColor.WHITE
                + (team.getHQ() == null ? "None" : team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ()));
        lore.add("");
        lore.add(ChatColor.GREEN + "Left-Click to edit settings");
        lore.add(ChatColor.GREEN + "Right-Click to teleport to HQ");
        lore.add(ChatColor.RED + "Shift-Click to disband");
        lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 15;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType.isLeftClick() && !clickType.isShiftClick()) {
            new SystemEditMenu(team).openMenu(player);
        } else if (clickType.isRightClick()) {
            if (team.getHQ() != null) {
                player.teleport(team.getHQ());
                player.sendMessage(ChatColor.GREEN + "Teleported to " + team.getName() + " HQ.");
            } else if (!team.getClaims().isEmpty()) {
                player.teleport(team.getClaims().get(0).getMidpoint());
                player.sendMessage(ChatColor.GREEN + "Teleported to " + team.getName() + " claim.");
            } else {
                player.sendMessage(ChatColor.RED + "This faction has no HQ or claims.");
            }
        } else if (clickType.isShiftClick() && clickType.isLeftClick()) {
            team.disband();
            player.sendMessage(ChatColor.RED + "Disbanded " + team.getName() + ".");
            new SystemEditorMenu().openMenu(player);
        }
    }
}
