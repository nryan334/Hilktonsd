package net.frozenorb.foxtrot.server.deathban.listener;

import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

public class DeathbanArenaListener implements Listener {
    private Foxtrot instance;

    public DeathbanArenaListener(Foxtrot instance) {
        this.instance = instance;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : instance.getServer().getOnlinePlayers()) {
                    if (player.getWorld().getName().equalsIgnoreCase("Deathban")) {
                        updateSigns(player);
                    }
                }
            }
        }.runTaskTimer(instance, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null || !block.getType().name().contains("SIGN") || !(block.getState() instanceof Sign)) {
            return;
        }

        if (!block.getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        final Sign sign = (Sign) block.getState();
        String line0 = ChatColor.stripColor(sign.getLine(0));
        String line1 = ChatColor.stripColor(sign.getLine(1));

        boolean isReviveSign = (line0.equalsIgnoreCase("[DeathRevive]") || line0.equalsIgnoreCase("[DeathLives]"))
                || (line0.equalsIgnoreCase("Have a life?") && line1.equalsIgnoreCase("Click Here"))
                || (sign.getLine(1) != null && sign.getLine(1).startsWith(ChatColor.BLUE + "- Respawn -"));

        if (line0.equalsIgnoreCase("[KitDeathban]") || line1.equalsIgnoreCase("Deathban Kit")) {
            player.performCommand("deathban gkit apply");
            return;
        }

        if (!isReviveSign) {
            return;
        }

        if (!this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "You are not deathbanned, so you were revived for free (no lives consumed).");
            this.instance.getDeathbanArenaHandler().revive(player.getUniqueId());
            return;
        }

        long lifeCooldown = this.instance.getDeathbanArenaHandler().getLifeCooldown().getOrDefault(player.getUniqueId(),
                0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown - System.currentTimeMillis()) / 1000;

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&cYou may not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference)
                            + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        int lives = this.instance.getFriendLivesMap().getLives(player.getUniqueId());

        if (lives > 0) {
            this.instance.getFriendLivesMap().setLives(player.getUniqueId(), lives - 1);
            player.sendMessage(ChatColor.GREEN + "You have used 1 life to revive yourself! You now have " + (lives - 1) + " lives remaining.");
            this.instance.getDeathbanArenaHandler().revive(player.getUniqueId());
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't afford a revive!");
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }

        if (this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not do that here!");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }

        if (this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not do that here!");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            if (this.instance.getDeathbanArenaHandler().getDeathbanSpawn() != null) {
                player.teleport(this.instance.getDeathbanArenaHandler().getDeathbanSpawn());
            } else if (this.instance.getServer().getWorld("Deathban") != null) {
                player.teleport(this.instance.getServer().getWorld("Deathban").getSpawnLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onRespawn(PlayerRespawnEvent event) {
        if (this.instance.getDeathbanArenaHandler().getDeathbanSpawn() != null) {
            event.setRespawnLocation(this.instance.getDeathbanArenaHandler().getDeathbanSpawn());
        } else if (this.instance.getServer().getWorld("Deathban") != null) {
            event.setRespawnLocation(this.instance.getServer().getWorld("Deathban").getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getLocation().getWorld().getName().equalsIgnoreCase("Deathban")) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (player.getWorld().getName().equalsIgnoreCase("Deathban")) {
            event.getDrops().clear();
        }

        if (killer == null || killer.getName().equalsIgnoreCase(player.getName())) {
            return;
        }

        if (!this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            return;
        }

        if (!this.instance.getDeathbanArenaHandler().isDeathbanArena(killer)) {
            return;
        }

        long lifeCooldown = this.instance.getDeathbanArenaHandler().getLifeCooldown().getOrDefault(player.getUniqueId(),
                0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown - System.currentTimeMillis()) / 1000;

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&cYou may not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference)
                            + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        int kills = this.instance.getDeathbanArenaHandler().getCache().getOrDefault(killer.getUniqueId(), 0) + 1;

        if (kills >= 5) {
            this.instance.getDeathbanArenaHandler().revive(killer.getUniqueId());
        } else {
            this.instance.getDeathbanArenaHandler().getCache().put(killer.getUniqueId(), kills);

            killer.sendMessage(ChatColor.RED + "You need " + ChatColor.WHITE + (5 - kills) + " kills" + ChatColor.RED
                    + " until you get undeathbanned.");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (event.getMessage().startsWith("/request") || event.getMessage().startsWith("/report")
                || event.getMessage().startsWith("/helpop") || event.getMessage().startsWith("/hub")
                || event.getMessage().startsWith("/msg ") || event.getMessage().startsWith("/r ")
                || event.getMessage().startsWith("/reply ") || event.getMessage().startsWith("/message ")
                || event.getMessage().startsWith("/m ") || event.getMessage().startsWith("/w ")
                || event.getMessage().startsWith("/tell ") || event.getMessage().startsWith("/deathban")) {
            return;
        }

        if (!this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            return;
        }

        if (!this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            return;
        }

        if (player.hasPermission("foxtrot.staff")) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You may not run that command whilst deathbanned!");
        event.setCancelled(true);
    }

    @EventHandler
    private void onSignChange(org.bukkit.event.block.SignChangeEvent event) {
        if (!event.getBlock().getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        if (event.getLine(0) != null && (event.getLine(0).startsWith("[Death") || event.getLine(0).equalsIgnoreCase("[KitDeathban]"))) {
            this.instance.getDeathbanArenaHandler().getDeathSigns().add(event.getBlock().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Dynamic death sign registered!");
        }
    }

    @EventHandler
    private void onSignBreak(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        this.instance.getDeathbanArenaHandler().getDeathSigns().remove(event.getBlock().getLocation());
    }

    private void updateSigns(Player player) {
        for (Location location : this.instance.getDeathbanArenaHandler().getDeathSigns()) {
            if (location.distanceSquared(player.getLocation()) > 400) {
                continue;
            }

            Block block = location.getBlock();
            if (!(block.getState() instanceof Sign)) {
                continue;
            }

            Sign sign = (Sign) block.getState();
            String line0 = sign.getLine(0);

            if (line0.equalsIgnoreCase("[DeathInfo]")) {
                String killer = instance.getDeathbanArenaHandler().getDeathbanKiller()
                        .getOrDefault(player.getUniqueId(), "None");
                player.sendSignChange(sign.getLocation(), new String[] {
                        "",
                        "Killed by",
                        ChatColor.RED + killer,
                        ""
                });
            } else if (line0.equalsIgnoreCase("[DeathTime]")) {
                long seconds = (instance.getDeathbanMap().getDeathban(player.getUniqueId())
                        - System.currentTimeMillis()) / 1000;
                player.sendSignChange(sign.getLocation(), new String[] {
                        "",
                        "Time Left",
                        ChatColor.GOLD + formatShortTime(Math.max(0, seconds)),
                        ""
                });
            } else if (line0.equalsIgnoreCase("[DeathLoc]")) {
                String locationStr = instance.getDeathbanArenaHandler().getDeathbanLocation()
                        .getOrDefault(player.getUniqueId(), "Unknown");
                player.sendSignChange(sign.getLocation(), new String[] {
                        "",
                        "Death Location",
                        ChatColor.LIGHT_PURPLE + locationStr.replace(",", ""),
                        ""
                });
            } else if (line0.equalsIgnoreCase("[DeathRevive]") || line0.equalsIgnoreCase("[DeathLives]")) {
                player.sendSignChange(sign.getLocation(), new String[] {
                        "",
                        "Have a life?",
                        ChatColor.GREEN + "Click Here",
                        ""
                });
            } else if (line0.equalsIgnoreCase("[KitDeathban]")) {
                player.sendSignChange(sign.getLocation(), new String[] {
                        ChatColor.STRIKETHROUGH + "-------------",
                        ChatColor.DARK_RED + "Deathban Kit",
                        ChatColor.GRAY + "Right-Click",
                        ChatColor.STRIKETHROUGH + "-------------"
                });
            }
        }
    }

    public static String formatShortTime(long seconds) {
        if (seconds <= 0) {
            return "0 Seconds";
        }
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            String hourStr = hours + " Hour" + (hours > 1 ? "s" : "");
            if (hourStr.length() > 15 && minutes > 0) {
                return hours + "h " + minutes + "m";
            }
            return hourStr;
        } else if (minutes > 0) {
            return minutes + " Minute" + (minutes > 1 ? "s" : "");
        } else {
            return secs + " Second" + (secs > 1 ? "s" : "");
        }
    }
}
