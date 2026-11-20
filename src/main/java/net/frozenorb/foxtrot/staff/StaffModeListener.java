package net.frozenorb.foxtrot.staff;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StaffModeListener implements Listener {

    private final StaffModeHandler handler;
    private final Random random = new Random();

    public StaffModeListener(StaffModeHandler handler) {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!handler.isInModMode(player.getUniqueId())) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (item.getType() == Material.COMPASS) {
                event.setCancelled(true);
                try {
                    if (event.getAction().name().contains("LEFT")) {
                        BlockIterator iter = new BlockIterator(player, 10);
                        Block lastBlock = iter.next();
                        while (iter.hasNext()) {
                            lastBlock = iter.next();
                            if (lastBlock.getType() == Material.AIR) {
                                continue;
                            }
                            break;
                        }
                        if (lastBlock != null) {
                            Location loc = lastBlock.getLocation();
                            loc.setPitch(player.getLocation().getPitch());
                            loc.setYaw(player.getLocation().getYaw());
                            player.teleport(loc.add(0, 1, 0));
                        }
                    } else {
                        Block targetBlock = player.getTargetBlock((java.util.HashSet<Byte>) null, 100);
                        if (targetBlock != null) {
                            Location loc = targetBlock.getLocation();
                            loc.setPitch(player.getLocation().getPitch());
                            loc.setYaw(player.getLocation().getYaw());
                            player.teleport(loc.add(0, 1, 0));
                        }
                    }
                } catch (Exception ignored) {
                }
            } else if (item.getType() == Material.DIAMOND) {
                event.setCancelled(true);
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                players.remove(player);
                if (!players.isEmpty()) {
                    Player target = players.get(random.nextInt(players.size()));
                    player.teleport(target);
                    player.sendMessage(ChatColor.AQUA + "Randomly teleported to " + ChatColor.WHITE + target.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough players");
                }
            } else if (item.getType() == Material.LEASH) {
                event.setCancelled(true);
                List<Player> validPlayers = new ArrayList<>();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online == player)
                        continue;
                    if (!DTRBitmask.SAFE_ZONE.appliesAt(online.getLocation())) {
                        validPlayers.add(online);
                    }
                }

                if (!validPlayers.isEmpty()) {
                    Player target = validPlayers.get(random.nextInt(validPlayers.size()));
                    player.teleport(target);
                    player.sendMessage(ChatColor.AQUA + "Randomly teleported to " + ChatColor.WHITE + target.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "No players are currently in combat tag");
                }
            } else if (item.getType() == Material.INK_SACK) {
                event.setCancelled(true);
                player.performCommand("v");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player))
            return;

        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();

        if (!handler.isInModMode(player.getUniqueId()))
            return;

        ItemStack item = player.getItemInHand();
        if (item == null)
            return;

        if (item.getType() == Material.BOOK) {
            event.setCancelled(true);
            player.openInventory(target.getInventory());
            player.sendMessage(ChatColor.GOLD + "Viewing inventory of " + ChatColor.WHITE + target.getName());
        } else if (item.getType() == Material.ICE) {
            event.setCancelled(true);
            player.performCommand("freeze " + target.getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (player.hasPermission("foxtrot.staff")) {
                handler.enableModMode(player);
            }
        }, 2L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (handler.isInModMode(player.getUniqueId())) {
            handler.disableModMode(player);
        }
        handler.setVanish(player, StaffModeHandler.VanishState.VISIBLE);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        cc.fyre.proton.Proton.getInstance().getVisibilityHandler().update(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (handler.isInModMode(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (handler.isInModMode(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (handler.isInModMode(event.getPlayer().getUniqueId())
                || handler.isInVanish(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (handler.isInModMode(player.getUniqueId()) || handler.isInVanish(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (handler.isInModMode(player.getUniqueId()) || handler.isInVanish(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (handler.isInModMode(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    ChatColor.RED + ChatColor.BOLD.toString() + "You cannot break blocks whilst in Mod Mode.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (handler.isInModMode(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    ChatColor.RED + ChatColor.BOLD.toString() + "You cannot place blocks whilst in Mod Mode.");
        }
    }
}
