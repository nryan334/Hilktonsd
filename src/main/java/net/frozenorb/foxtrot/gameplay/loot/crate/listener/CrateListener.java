package net.frozenorb.foxtrot.gameplay.loot.crate.listener;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.CrateBlock;
import net.frozenorb.foxtrot.gameplay.loot.crate.CrateHandler;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import net.frozenorb.foxtrot.gameplay.loot.crate.menu.CrateEditMenu;
import net.frozenorb.foxtrot.gameplay.loot.crate.menu.CrateLootEditorMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

@AllArgsConstructor
public class CrateListener implements Listener {
    private CrateHandler crateHandler;

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();
        final Block block = event.getClickedBlock();

        if (block != null && (event.getAction().name().contains("RIGHT_CLICK_BLOCK")
                || event.getAction().name().contains("LEFT_CLICK_BLOCK"))) {
            Optional<CrateBlock> crateBlock = Foxtrot.getInstance().getCrateBlockHandler().getAt(block.getLocation());
            if (crateBlock.isPresent()) {
                event.setCancelled(true);
                Crate crate = crateHandler.findById(crateBlock.get().getCrateId()).orElse(null);
                if (crate == null)
                    return;

                if (event.getAction().name().contains("LEFT_CLICK_BLOCK")) {
                    if (player.isSneaking() && player.hasPermission("foxtrot.crates.admin")) {
                        Foxtrot.getInstance().getCrateBlockHandler().removeBlock(crateBlock.get());
                        block.setType(Material.AIR);
                        player.sendMessage(ChatColor.RED + "You removed the crate block!");
                    } else {
                        new Menu() {
                            @Override
                            public String getTitle(Player player) {
                                return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', crate.getDisplayName())) + " Loot";
                            }

                            @Override
                            public Map<Integer, Button> getButtons(Player player) {
                                final Map<Integer, Button> toReturn = new HashMap<>();
                                for (CrateItem item : crate.getItems()) {
                                    if (item.getItemStack() == null
                                            || item.getItemStack().getType().equals(Material.AIR)) {
                                        continue;
                                    }
                                    if (item.getSlot() < size(player)) {
                                        toReturn.put(item.getSlot(), Button.fromItem(item.getItemStack().clone()));
                                    }
                                }

                                if (crate.getFillerItem() != null) {
                                    int maxSlots = size(player);
                                    for (int i = 0; i < maxSlots; i++) {
                                        if (!toReturn.containsKey(i)) {
                                            toReturn.put(i, Button.fromItem(crate.getFillerItem().clone()));
                                        }
                                    }
                                }
                                return toReturn;
                            }

                            @Override
                            public int size(Player player) {
                                return crate.getRows() * 9;
                            }
                        }.openMenu(player);
                    }
                    return;
                }

                if (player.isSneaking()) {
                    if (player.hasPermission("foxtrot.crates.admin")) {
                        new CrateEditMenu(crate).openMenu(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "No permission to edit this crate.");
                    }
                } else {
                    ItemStack inHand = player.getItemInHand();
                    if (inHand != null && crate.getItemStack().isSimilar(inHand)) {
                        if (inHand.getAmount() == 1) {
                            player.setItemInHand(null);
                        } else {
                            inHand.setAmount(inHand.getAmount() - 1);
                        }
                        crateHandler.openCrate(player, crate);
                    } else {
                        player.sendMessage(ChatColor.RED + "You need a " + ChatColor.translateAlternateColorCodes('&', crate.getDisplayName())
                                + ChatColor.RED + " key to open this!");
                    }
                }
                return;
            }
        }

        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Optional<Crate> optionalCrate = crateHandler.findByItemStack(itemStack);

        if (itemStack == null || !optionalCrate.isPresent()) {
            return;
        }

        event.setCancelled(true);

        if (player.isSneaking()) {
            new Menu() {
                @Override
                public String getTitle(Player player) {
                    return optionalCrate.get().getId() + " Loot";
                }

                @Override
                public Map<Integer, Button> getButtons(Player player) {
                    final Map<Integer, Button> toReturn = new HashMap<>();
                    Crate crate = optionalCrate.get();
                    for (CrateItem item : crate.getItems()) {
                        if (item.getItemStack() == null || item.getItemStack().getType().equals(Material.AIR)) {
                            continue;
                        }
                        if (item.getSlot() < size(player)) {
                            toReturn.put(item.getSlot(), Button.fromItem(item.getItemStack().clone()));
                        }
                    }

                    if (crate.getFillerItem() != null) {
                        int maxSlots = size(player);
                        ItemStack filler = crate.getFillerItem().clone();
                        if (crate.isFillerGlow()) {
                            org.bukkit.inventory.meta.ItemMeta meta = filler.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName(" ");
                                meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
                                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
                                filler.setItemMeta(meta);
                            }
                        }
                        for (int i = 0; i < maxSlots; i++) {
                            if (!toReturn.containsKey(i)) {
                                toReturn.put(i, Button.fromItem(filler));
                            }
                        }
                    }
                    return toReturn;
                }

                @Override
                public int size(Player player) {
                    return optionalCrate.get().getRows() * 9;
                }
            }.openMenu(player);
            return;
        }

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        crateHandler.openCrate(player, optionalCrate.get());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemInHand();

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return;
        }

        if (itemStack.getItemMeta().getDisplayName().contains("Crate Block:")) {
            String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).replace("Crate Block: ",
                    "");
            Crate crate = crateHandler.getCrates().stream()
                    .filter(it -> ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', it.getDisplayName()))
                            .equalsIgnoreCase(displayName))
                    .findFirst().orElse(null);

            if (crate != null) {
                CrateBlock cb = new CrateBlock(crate.getId(), event.getBlock().getLocation(), null);
                Foxtrot.getInstance().getCrateBlockHandler().addBlock(cb);
                player.sendMessage(ChatColor.GREEN + "Placed " + crate.getId() + " crate block!");
                return;
            }
        }

        final Optional<Crate> optionalCrate = crateHandler.findByItemStack(itemStack);
        if (optionalCrate.isPresent()) {
            event.setCancelled(true);
            if (itemStack.getAmount() == 1) {
                player.setItemInHand(null);
            } else {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
            crateHandler.openCrate(player, optionalCrate.get());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        Optional<CrateBlock> crateBlock = Foxtrot.getInstance().getCrateBlockHandler()
                .getAt(event.getBlock().getLocation());
        if (crateBlock.isPresent()) {
            if (!event.getPlayer().hasPermission("foxtrot.crates.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break crate blocks!");
            } else {
                Foxtrot.getInstance().getCrateBlockHandler().removeBlock(crateBlock.get());
                event.getPlayer().sendMessage(ChatColor.RED + "You removed the crate block!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != null && event.getInventory().getTitle().startsWith("Drag & Drop: ")) {
            Player player = (Player) event.getPlayer();
            String crateId = event.getInventory().getTitle().replace("Drag & Drop: ", "");

            Crate crate = crateHandler.findById(crateId).orElse(null);
            if (crate == null)
                return;

            Inventory inv = event.getInventory();
            List<CrateItem> newItems = new ArrayList<>();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    int finalI = i;
                    CrateItem existing = crate.getItems().stream()
                            .filter(ci -> ci.getSlot() == finalI && ci.getItemStack().isSimilar(item))
                            .findFirst().orElse(null);

                    if (existing != null) {
                        existing.setItemStack(item.clone());
                        newItems.add(existing);
                    } else {
                        CrateItem newItem = new CrateItem(item.clone(), 10.0, "none", true, false, i);
                        newItems.add(newItem);
                    }
                }
            }

            crate.setItems(newItems);
            crateHandler.saveData();

            player.sendMessage(ChatColor.GREEN + "Saved " + newItems.size() + " items to the crate.");

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                new CrateLootEditorMenu(crate).openMenu(player);
            }, 2L);
        }
    }
}
