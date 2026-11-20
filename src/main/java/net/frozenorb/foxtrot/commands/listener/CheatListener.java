package net.frozenorb.foxtrot.commands.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class CheatListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.hasMetadata("CHEAT_REACH")) {
                for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Vector direction = player.getLocation().getDirection().normalize();
                        Vector toEntity = entity.getLocation().toVector().subtract(player.getLocation().toVector())
                                .normalize();
                        if (direction.angle(toEntity) < 0.3) {
                            if (player.getLocation().distance(entity.getLocation()) <= 8) {
                                ((LivingEntity) entity).damage(getDamage(player), player);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.hasMetadata("CHEAT_PHASE") && player.isSneaking()) {
                org.bukkit.Location current = player.getLocation().clone();
                current.setY(current.getY() + 1.62);
                Vector dir = player.getLocation().getDirection().normalize();

                boolean passedSolid = false;
                for (int i = 0; i < 20; i++) {
                    current.add(dir.clone().multiply(1.0));
                    org.bukkit.block.Block block = current.getBlock();

                    if (block.getType().isSolid()) {
                        passedSolid = true;
                    } else if (passedSolid) {
                        org.bukkit.block.Block above = block.getRelative(org.bukkit.block.BlockFace.UP);
                        if (!above.getType().isSolid()) {
                            org.bukkit.Location safeSpot = current.clone();
                            safeSpot.setX(safeSpot.getBlockX() + 0.5);
                            safeSpot.setY(safeSpot.getBlockY());
                            safeSpot.setZ(safeSpot.getBlockZ() + 0.5);
                            safeSpot.setPitch(player.getLocation().getPitch());
                            safeSpot.setYaw(player.getLocation().getYaw());

                            player.teleport(safeSpot);
                            player.sendMessage(org.bukkit.ChatColor.LIGHT_PURPLE + "Phased through wall!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                player.sendMessage(org.bukkit.ChatColor.RED + "Could not find an empty space to phase into!");
            }

            if (player.hasMetadata("CHEAT_FASTBOW") && player.getItemInHand() != null
                    && player.getItemInHand().getType() == Material.BOW) {
                if (player.getInventory().contains(Material.ARROW)) {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(player.getLocation().getDirection().multiply(3.0));
                    event.setCancelled(true);
                }
            }
        }
    }

    private double getDamage(Player player) {
        Material type = player.getItemInHand() == null ? Material.AIR : player.getItemInHand().getType();
        if (type == Material.DIAMOND_SWORD)
            return 7.0;
        if (type == Material.IRON_SWORD)
            return 6.0;
        if (type == Material.STONE_SWORD)
            return 5.0;
        if (type == Material.WOOD_SWORD || type == Material.GOLD_SWORD)
            return 4.0;
        return 1.0;
    }
}
