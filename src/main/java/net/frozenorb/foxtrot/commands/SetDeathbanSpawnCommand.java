package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetDeathbanSpawnCommand {

    @Command(names = {"setdeathbanspawn"}, permission = "op")
    public static void setDeathbanSpawn(Player player) {
        Foxtrot.getInstance().getDeathbanArenaHandler().setDeathbanSpawn(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Deathban Arena spawn has been set to your current location!");
    }
}
