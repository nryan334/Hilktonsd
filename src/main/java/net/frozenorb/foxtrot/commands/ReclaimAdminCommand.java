package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.reclaim.Reclaim;
import net.frozenorb.foxtrot.server.reclaim.menu.ReclaimEditorMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class ReclaimAdminCommand {

    @Command(names = { "reclaimadmin", "reclaims" }, permission = "foxtrot.command.reclaimadmin")
    public static void execute(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /reclaimadmin <editor|create|delete>");
    }

    @Command(names = { "reclaimadmin editor" }, permission = "foxtrot.command.reclaimadmin")
    public static void editor(Player player) {
        new ReclaimEditorMenu().openMenu(player);
    }

    @Command(names = { "reclaimadmin create" }, permission = "foxtrot.command.reclaimadmin")
    public static void create(Player player, @Parameter(name = "name") String name) {
        if (Foxtrot.getInstance().getReclaimHandler().getReclaim(name).isPresent()) {
            player.sendMessage(ChatColor.RED + "A reclaim with that name already exists.");
            return;
        }

        Reclaim reclaim = new Reclaim(name);
        Foxtrot.getInstance().getReclaimHandler().getReclaims().add(reclaim);
        Foxtrot.getInstance().getReclaimHandler().saveReclaim(reclaim);

        player.sendMessage(ChatColor.GREEN + "Created new reclaim: " + name);
    }

    @Command(names = { "reclaimadmin delete" }, permission = "foxtrot.command.reclaimadmin")
    public static void delete(Player player, @Parameter(name = "name") String name) {
        Reclaim reclaim = Foxtrot.getInstance().getReclaimHandler().getReclaim(name).orElse(null);
        if (reclaim == null) {
            player.sendMessage(ChatColor.RED + "No reclaim found with that name.");
            return;
        }

        Foxtrot.getInstance().getReclaimHandler().deleteReclaim(reclaim);
        player.sendMessage(ChatColor.GREEN + "Deleted reclaim: " + name);
    }
}
