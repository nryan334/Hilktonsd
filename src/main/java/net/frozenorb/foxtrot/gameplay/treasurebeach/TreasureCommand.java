package net.frozenorb.foxtrot.gameplay.treasurebeach;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TreasureCommand {

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                ItemStack item = player.getItemInHand();
                if (item != null && item.getType() == Material.GOLD_HOE && item.hasItemMeta()
                        && item.getItemMeta().hasDisplayName()
                        && item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&4&lTreasure Beach Claiming Tool"))) {

                    event.setCancelled(true);

                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        Location loc = event.getClickedBlock().getLocation();
                        player.setMetadata("treasure_c1", new FixedMetadataValue(Foxtrot.getInstance(), loc));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&4&lTreasure Beach &7» &7Set corner &b1 &7to &b" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "&7."));
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        Location loc = event.getClickedBlock().getLocation();
                        player.setMetadata("treasure_c2", new FixedMetadataValue(Foxtrot.getInstance(), loc));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&4&lTreasure Beach &7» &7Set corner &b2 &7to &b" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "&7."));
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
                if (event.getInventory().getTitle().equals("Treasure Beach Loot Editor")) {
                    java.util.List<ItemStack> items = new java.util.ArrayList<>();
                    for (ItemStack item : event.getInventory().getContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            items.add(item);
                        }
                    }
                    TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();
                    handler.getCustomLoot().clear();
                    handler.getCustomLoot().addAll(items);
                    handler.save();
                    Player player = (Player) event.getView().getPlayer();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&4&lTreasure Beach &7» &aSuccessfully saved &b" + items.size() + " &aitem(s) to the loot pool!"));
                }
            }
        }, Foxtrot.getInstance());
    }

    @Command(names = { "treasure claim", "treasure claimtool", "treasure tool" }, permission = "foxtrot.command.treasure")
    public static void treasureClaim(Player player) {
        ItemStack hoe = new ItemStack(Material.GOLD_HOE);
        ItemMeta meta = hoe.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lTreasure Beach Claiming Tool"));
        meta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&7Left-click block to set Corner 1"),
                ChatColor.translateAlternateColorCodes('&', "&7Right-click block to set Corner 2"),
                ChatColor.translateAlternateColorCodes('&', "&7Use &b/treasure claim save &7when finished.")
        ));
        hoe.setItemMeta(meta);
        player.getInventory().addItem(hoe);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lTreasure Beach &7» &7Received claiming hoe. Select both corners."));
    }

    @Command(names = { "treasure claim save" }, permission = "foxtrot.command.treasure")
    public static void treasureClaimSave(Player player) {
        if (!player.hasMetadata("treasure_c1") || !player.hasMetadata("treasure_c2")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lTreasure Beach &7» &7You must select both corners first using the hoe!"));
            return;
        }

        Location c1 = (Location) player.getMetadata("treasure_c1").get(0).value();
        Location c2 = (Location) player.getMetadata("treasure_c2").get(0).value();

        if (!c1.getWorld().getName().equalsIgnoreCase(c2.getWorld().getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lTreasure Beach &7» &7Both corners must be in the same world!"));
            return;
        }

        TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();
        handler.setClaimed(true);
        handler.setWorldName(c1.getWorld().getName());
        handler.setX1(c1.getBlockX());
        handler.setY1(c1.getBlockY());
        handler.setZ1(c1.getBlockZ());
        handler.setX2(c2.getBlockX());
        handler.setY2(c2.getBlockY());
        handler.setZ2(c2.getBlockZ());
        handler.setLastRefillTime(System.currentTimeMillis());
        handler.save();

        player.removeMetadata("treasure_c1", Foxtrot.getInstance());
        player.removeMetadata("treasure_c2", Foxtrot.getInstance());

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lTreasure Beach &7» &7Region successfully claimed and saved!"));
    }

    @Command(names = { "treasure editor force-refill", "treasure refill",
            "treasure force-refill" }, permission = "foxtrot.command.treasure")
    public static void treasureRefill(Player player) {
        TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();
        if (!handler.isClaimed()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lTreasure Beach &7» &7Region has not been claimed yet!"));
            return;
        }

        handler.refillChests();
        handler.setLastRefillTime(System.currentTimeMillis());
        handler.save();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lTreasure Beach &7» &7Forced a chest refill in the region."));
    }

    @Command(names = { "treasure editor setcooldown", "treasure setcooldown" }, permission = "foxtrot.command.treasure")
    public static void treasureSetCooldown(Player player, @Parameter(name = "hours") int hours) {
        if (hours <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lTreasure Beach &7» &7Cooldown must be at least 1 hour!"));
            return;
        }

        TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();
        handler.setRefillCooldownMs(hours * 60 * 60 * 1000L);
        handler.save();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lTreasure Beach &7» &7Refill cooldown set to &b" + hours + " &7hour(s)."));
    }

    @Command(names = { "treasure editor info", "treasure info" }, permission = "foxtrot.command.treasure")
    public static void treasureInfo(Player player) {
        TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lTreasure Beach Status"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Claimed: &b" + (handler.isClaimed() ? "Yes" : "No")));
        if (handler.isClaimed()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7World: &b" + handler.getWorldName()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Corner 1: &b" + handler.getX1() + ", "
                    + handler.getY1() + ", " + handler.getZ1()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Corner 2: &b" + handler.getX2() + ", "
                    + handler.getY2() + ", " + handler.getZ2()));

            long timeRemaining = handler.getRefillCooldownMs()
                    - (System.currentTimeMillis() - handler.getLastRefillTime());
            if (timeRemaining < 0)
                timeRemaining = 0;

            long min = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60;
            long hr = TimeUnit.MILLISECONDS.toHours(timeRemaining);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Cooldown: &b"
                    + TimeUnit.MILLISECONDS.toHours(handler.getRefillCooldownMs()) + " hour(s)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Next Refill: &b" + hr + "h " + min + "m"));
        }
    }

    @Command(names = { "treasure editor loot", "treasure loot editor" }, permission = "foxtrot.command.treasure")
    public static void treasureLootEditor(Player player) {
        TreasureBeachHandler handler = Foxtrot.getInstance().getMapHandler().getTreasureBeachHandler();
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, "Treasure Beach Loot Editor");
        for (ItemStack item : handler.getCustomLoot()) {
            inv.addItem(item.clone());
        }
        player.openInventory(inv);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lTreasure Beach &7» &bOpened the loot editor. Drag and drop items, then close to save."));
    }

    @Command(names = { "treasurebeach info", "treasure beach info", "tb info", "treasurebeach" }, permission = "")
    public static void treasureBeachInfo(Player player) {
        new TreasureBeachInfoMenu().openMenu(player);
    }
}
