package net.frozenorb.foxtrot.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;

public class PlaytimeCommand {

    @Command(names = { "Playtime", "PTime", "checktime" }, permission = "")
    public static void playtime(Player sender, @Parameter(name = "player", defaultValue = "self") UUID player) {
        PlaytimeMap playtime = Foxtrot.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(player);
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + ChatColor.GRAY + " has a total of " + ChatColor.RED
                + TimeUtils.formatIntoDetailedString(playtimeTime) + ChatColor.GRAY + " on the server.");
    }

}