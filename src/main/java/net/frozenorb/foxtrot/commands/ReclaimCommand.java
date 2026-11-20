package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.universe.UniverseAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.event.ReclaimEvent;
import net.frozenorb.foxtrot.server.reclaim.Reclaim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.*;

public class ReclaimCommand {

    @Command(names = { "reclaim" }, permission = "")
    public static void execute(Player player) {

        if (Foxtrot.getInstance().getReclaimMap().hasReclaimed(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already reclaimed this map.");
            return;
        }

        if (player.getName().equalsIgnoreCase("Lavaboy29")) {
            player.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 64, (byte) 91));
            player.getInventory().addItem(new ItemStack(Material.LEASH, 32));
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        if (profile.hasSubscription()) {
            for (String command : Foxtrot.getInstance().getConfig().getStringList("reclaims.VIP.commands")) {
                Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(),
                        command.replace("{player}", player.getName()).replace("{rankName}", ChatColor.YELLOW + "VIP"));
            }
            Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), true);
        }

        for (Reclaim reclaim : Foxtrot.getInstance().getReclaimHandler().getReclaims().stream()
                .sorted(Comparator.comparingInt(Reclaim::getPriority)).collect(Collectors.toList())) {
            if (player.hasPermission(reclaim.getPermission())) {
                Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), true);

                for (String command : reclaim.getCommands()) {
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())
                                .replace("{uuid}", player.getUniqueId().toString()));
                    } catch (Exception e) {
                        Foxtrot.getInstance().getLogger().severe(
                                "[Reclaims] Failed to execute command: " + command + " for player " + player.getName());
                        e.printStackTrace();
                    }
                }

                for (ItemStack item : reclaim.getItems()) {
                    if (item != null) {
                        player.getInventory().addItem(item.clone());
                    }
                }
                player.updateInventory();

                Foxtrot.getInstance().getServer().getPluginManager()
                        .callEvent(new ReclaimEvent(player, reclaim.getName()));

                Rank activeRank = profile.getActiveRank();
                String rankDisplay = activeRank != null
                        ? activeRank.getColor() + activeRank.getName()
                        : ChatColor.GREEN + reclaim.getName();

                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Reclaim ");
                Bukkit.broadcastMessage(
                        ChatColor.WHITE + player.getName() + ChatColor.YELLOW + " has just reclaimed their "
                                + rankDisplay + ChatColor.YELLOW + " rank benefits!");
                Bukkit.broadcastMessage(
                        ChatColor.GRAY + "Purchase a rank at " + ChatColor.WHITE + "store.strivepvp.top");
                Bukkit.broadcastMessage("");

                player.sendMessage(ChatColor.GREEN + "You have successfully reclaimed your " + rankDisplay
                        + ChatColor.GREEN + " rank benefits!");
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "It appears there is no reclaim found for your rank.");
    }

    @Command(names = { "resetreclaim", "reclaimreset" }, permission = "foxtrot.command.resetreclaim")
    public static void execute(CommandSender sender, @Parameter(name = "player") UUID uuid) {

        if (!Foxtrot.getInstance().getReclaimMap().hasReclaimed(uuid)) {
            sender.sendMessage(ChatColor.RED + "That player has not reclaimed yet this map!");
            return;
        }

        Foxtrot.getInstance().getReclaimMap().setReclaimed(uuid, false);

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&',
                        LIGHT_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f"
                                + (sender instanceof Player ? ((Player) sender).getDisplayName()
                                        : DARK_RED + BOLD.toString() + "Console")
                                + " &7has reset &f" + UUIDUtils.name(uuid) + "'s &7reclaim."))));

        sender.sendMessage(
                ChatColor.GOLD + "Reset " + ChatColor.YELLOW + uuid.toString() + ChatColor.GOLD + "'s reclaim.");
    }
}
