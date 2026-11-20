package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.menu.HiltonFeatureMenu;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HiltonCommand {

    @Command(names = { "hilton syncperms" }, permission = "hilton.syncperms", async = true)
    public static void syncperms(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Starting comprehensive permission discovery in Neutron...");

        try {
            Neutron.getInstance().getPermissionHandler().discoverPermissions();
            sender.sendMessage(ChatColor.GREEN + "Successfully synced all permissions to Neutron.");
            sender.sendMessage(ChatColor.GRAY + "They should now appear in the rank editor menu.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while syncing permissions.");
            e.printStackTrace();
        }
    }

    @Command(names = { "hilton features" }, permission = "hilton.features")
    public static void features(Player player) {
        new HiltonFeatureMenu().openMenu(player);
    }

    @Command(names = { "hilton forcewipe teams" }, permission = "op")
    public static void forcewipeTeams(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Starting full forcewipe of ALL player factions...");
        int count = 0;
        List<Team> teams = new ArrayList<>(Foxtrot.getInstance().getTeamHandler().getTeams());
        for (Team team : teams) {
            if (team.getOwner() != null) {
                team.disband();
                count++;
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Successfully wiped and disbanded " + ChatColor.WHITE + count
                + ChatColor.GREEN + " player factions!");
    }

    @Command(names = { "hilton forcewipe users" }, permission = "op")
    public static void forcewipeUsers(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Starting comprehensive forcewipe of ALL user data...");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != sender) {
                onlinePlayer.kickPlayer(ChatColor.RED + "Server is undergoing a full database wipe!");
            }
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getEnderChest().clear();
            p.setExp(0);
            p.setLevel(0);
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
        }

        try {
            Foxtrot.getInstance().getPlaytimeMap().wipe();
            Foxtrot.getInstance().getOppleMap().wipe();
            Foxtrot.getInstance().getDeathbanMap().wipe();
            Foxtrot.getInstance().getPvPTimerMap().wipe();
            Foxtrot.getInstance().getSaleTimersScoreboardMap().wipe();
            Foxtrot.getInstance().getStartingPvPTimerMap().wipe();
            Foxtrot.getInstance().getDeathsMap().wipe();
            Foxtrot.getInstance().getFactionFilterMap().wipe();
            Foxtrot.getInstance().getKitmapTokensMap().wipe();
            Foxtrot.getInstance().getKillsMap().wipe();
            Foxtrot.getInstance().getKillstreakMap().wipe();
            Foxtrot.getInstance().getKillTagMap().wipe();
            Foxtrot.getInstance().getChatModeMap().wipe();
            Foxtrot.getInstance().getTeamColorMap().wipe();
            Foxtrot.getInstance().getEnemyColorMap().wipe();
            Foxtrot.getInstance().getArcherTagColorMap().wipe();
            Foxtrot.getInstance().getTeamFocusColorMap().wipe();
            Foxtrot.getInstance().getFocusColorMap().wipe();
            Foxtrot.getInstance().getToggleGlobalChatMap().wipe();
            Foxtrot.getInstance().getFishingKitMap().wipe();
            Foxtrot.getInstance().getFriendLivesMap().wipe();
            Foxtrot.getInstance().getChatSpyMap().wipe();
            Foxtrot.getInstance().getDiamondMinedMap().wipe();
            Foxtrot.getInstance().getGoldMinedMap().wipe();
            Foxtrot.getInstance().getIronMinedMap().wipe();
            Foxtrot.getInstance().getCoalMinedMap().wipe();
            Foxtrot.getInstance().getRedstoneMinedMap().wipe();
            Foxtrot.getInstance().getAnnoyingBroadcastMap().wipe();
            Foxtrot.getInstance().getLapisMinedMap().wipe();
            Foxtrot.getInstance().getEmeraldMinedMap().wipe();
            Foxtrot.getInstance().getDTRDisplayMap().wipe();
            Foxtrot.getInstance().getTeamfightModeMap().wipe();
            Foxtrot.getInstance().getLcTeamViewMap().wipe();
            Foxtrot.getInstance().getTipsMap().wipe();
            Foxtrot.getInstance().getFirstJoinMap().wipe();
            Foxtrot.getInstance().getLastJoinMap().wipe();
            Foxtrot.getInstance().getWrappedBalanceMap().wipe();
            Foxtrot.getInstance().getToggleFoundDiamondsMap().wipe();
            Foxtrot.getInstance().getToggleDeathMessageMap().wipe();
            Foxtrot.getInstance().getToggleClaimMessageMap().wipe();
            Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().wipe();
            Foxtrot.getInstance().getTabListModeMap().wipe();
            Foxtrot.getInstance().getCobblePickupMap().wipe();
            Foxtrot.getInstance().getMobDropsPickupMap().wipe();
            Foxtrot.getInstance().getReclaimMap().wipe();
            Foxtrot.getInstance().getKdrMap().wipe();
            Foxtrot.getInstance().getBountyCooldownMap().wipe();
            Foxtrot.getInstance().getGemMap().wipe();
            Foxtrot.getInstance().getGemBoosterMap().wipe();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error wiping persist maps: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            if (Foxtrot.getInstance().getMapHandler().getStatsHandler() != null) {
                Foxtrot.getInstance().getMapHandler().getStatsHandler().clearAll();
                Foxtrot.getInstance().getMapHandler().getStatsHandler().clearLeaderboards();
                cc.fyre.proton.Proton.getInstance().runRedisCommand(redis -> {
                    for (String key : redis.keys(Bukkit.getServerName() + ":" + "stats2:*")) {
                        redis.del(key);
                    }
                    return null;
                });
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error wiping StatsHandler: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            com.mongodb.DBCollection playersCollection = Foxtrot.getInstance().getMongoPool()
                    .getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
            playersCollection.drop();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error dropping players collection in Mongo: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            for (org.bukkit.World world : Bukkit.getWorlds()) {
                File folder = new File(world.getWorldFolder(), "playerdata");
                if (folder.exists() && folder.isDirectory()) {
                    for (File file : folder.listFiles()) {
                        file.delete();
                    }
                }
                File stats = new File(world.getWorldFolder(), "stats");
                if (stats.exists() && stats.isDirectory()) {
                    for (File file : stats.listFiles()) {
                        file.delete();
                    }
                }
                File advancements = new File(world.getWorldFolder(), "advancements");
                if (advancements.exists() && advancements.isDirectory()) {
                    for (File file : advancements.listFiles()) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error deleting playerdata files: " + e.getMessage());
            e.printStackTrace();
        }

        sender.sendMessage(ChatColor.GREEN
                + "Successfully wiped all offline and online user data across all databases and filesystems!");
    }
}
