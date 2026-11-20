package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import net.minecraft.util.com.google.common.io.Files;
import net.frozenorb.foxtrot.persist.maps.FriendLivesMap;
import net.frozenorb.foxtrot.server.deathban.DeathbanArenaHandler;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


/**
 * @author Hxmilton (JavaProject) - 5/11/2026 11:45 AM
 * @Contact hxmiltonn (discord)
 * @Forked CavePvP (cavepvp.org)
 */

public class LivesCommand {

    @Command(names={ "lives" }, permission="")
    public static void lives(CommandSender commandSender) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Bad console.");
            return;
        }

        Player sender = (Player) commandSender;
        
        int shared = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        sender.sendMessage(ChatColor.RED + "Lives: " + ChatColor.WHITE + shared);
    }

    @Command(names={ "Revive" }, permission="foxtrot.revive")
    public static void revive(CommandSender sender, @Parameter(name="player") UUID player, @Parameter(name="reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player)) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + UUIDUtils.name(player) + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sender instanceof Player) {
                boolean urgent = UUIDUtils.name(player).equalsIgnoreCase("z5");

                Neutron.getInstance().getSecurityHandler().addSecurityAlert(((Player) sender).getUniqueId(), player, AlertType.REVIVES_ROLLBACKS, urgent, "Reason: " + reason);
            }

            Foxtrot.getInstance().getDeathbanArenaHandler().revive(player);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(player) + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }


}
