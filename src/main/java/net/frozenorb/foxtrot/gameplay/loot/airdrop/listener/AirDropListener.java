package net.frozenorb.foxtrot.gameplay.loot.airdrop.listener;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDrop;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import net.frozenorb.foxtrot.server.voucher.command.VoucherCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class AirDropListener implements Listener {
    private Foxtrot instance;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Optional<AirDrop> airDropOptional = Foxtrot.getInstance().getAirDropHandler()
                .getAirDropByItemStack(event.getItemInHand());
        if (!airDropOptional.isPresent()) {
            return;
        }

        event.setCancelled(true);
        startDrop(event.getPlayer(), event.getItemInHand(), event.getBlockPlaced().getLocation(), airDropOptional.get());
    }

    private void startDrop(Player player, ItemStack itemStack, Location location, AirDrop airDrop) {
        final Block block = location.getBlock();
        final AtomicInteger seconds = new AtomicInteger(4);

        VoucherCommand.spawnFireworks(block.getLocation(), 1, 2, Color.RED, FireworkEffect.Type.BALL_LARGE);

        if (itemStack.getAmount() == 1) {
            if (player.getItemInHand().isSimilar(itemStack)) {
                player.setItemInHand(null);
            } else {
                player.getInventory().removeItem(itemStack);
            }
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        List<String> lore = itemStack.getItemMeta() != null ? itemStack.getItemMeta().getLore() : null;
        boolean airdropAll = lore != null && lore.size() > 3;

        player.updateInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                seconds.decrementAndGet();

                if (seconds.get() > 0) {
                    block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
                    block.getWorld().playEffect(block.getLocation(), Effect.LARGE_SMOKE, 2);

                    player.sendMessage(ChatColor.RED + "Airdrop will drop in " + ChatColor.WHITE + seconds.get()
                            + ChatColor.RED + ".");
                } else {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                    block.setType(Material.DROPPER);
                    block.getWorld().playEffect(block.getLocation(), Effect.EXPLOSION_HUGE, 7);

                    Foxtrot.getInstance().getAirDropHandler().setContents(block, player, airdropAll, airDrop);

                    player.sendMessage(ChatColor.GREEN + "The Airdrop has dropped!");
                    this.cancel();

                    if (player.getWorld().getEnvironment() == World.Environment.NETHER
                            || player.getWorld().getEnvironment() == World.Environment.THE_END
                            || Foxtrot.getInstance().getServerHandler().isWarzone(block.getLocation())) {
                        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {

                            if (block.getType() == Material.DROPPER) {
                                block.setType(Material.AIR);
                            }
                        }, 20 * 60 * 2);
                    }
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 0, 20);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (!inventory.getType().equals(InventoryType.DROPPER)) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof Dropper)) {
            return;
        }

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return;
            }
        }

        final Dropper holder = (Dropper) event.getInventory().getHolder();
        holder.getBlock().breakNaturally();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        Optional<AirDrop> airDropOptional = Foxtrot.getInstance().getAirDropHandler().getAirDropByItemStack(itemStack);
        if (!airDropOptional.isPresent()) {
            return;
        }

        AirDrop airDrop = airDropOptional.get();

        if (player.isSneaking()) {
            event.setCancelled(true);
            new Menu() {

                @Override
                public String getTitle(Player player) {
                    return airDrop.getId() + " Loot";
                }

                @Override
                public Map<Integer, Button> getButtons(Player player) {
                    final Map<Integer, Button> toReturn = new HashMap<>();

                    for (AirDropReward airDropReward : airDrop.getRewards()) {
                        if (airDropReward.getItemStack() == null
                                || airDropReward.getItemStack().getType().equals(Material.AIR)) {
                            continue;
                        }
                        toReturn.put(toReturn.size(), new Button() {
                            @Override
                            public String getName(Player player) {
                                return null;
                            }

                            @Override
                            public List<String> getDescription(Player player) {
                                return null;
                            }

                            @Override
                            public Material getMaterial(Player player) {
                                return null;
                            }

                            @Override
                            public ItemStack getButtonItem(Player player) {
                                return airDropReward.getItemStack().clone();
                            }
                        });
                    }

                    return toReturn;
                }
            }.openMenu(player);
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            Location target;

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                target = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
            } else {
                target = player.getLocation().add(player.getLocation().getDirection().multiply(2));
                if (target.getBlock().getType() != Material.AIR) {
                    target = player.getLocation();
                }
            }

            startDrop(player, itemStack, target, airDrop);
        }
    }
}
