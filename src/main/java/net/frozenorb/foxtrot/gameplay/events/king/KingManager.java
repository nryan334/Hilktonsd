package net.frozenorb.foxtrot.gameplay.events.king;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KingManager {

    private Player king;
    private long startedTime;
    private int flightLimit;

    public KingManager() {
        this.king = null;
        this.startedTime = 0L;
        this.flightLimit = 5;

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new KingListener(this),
                Foxtrot.getInstance());
    }

    public boolean isActive() {
        return king != null;
    }

    public void startKing(Player player) {
        this.king = player;
        this.startedTime = System.currentTimeMillis();

        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().addItem(createLaser());
        player.getInventory().addItem(createDash());

        Bukkit.broadcastMessage(CC.translate("&6[KingOfTheHill] &eThe King Event has started! &c" + player.getName()
                + " &eis the King. Kill him for rewards!"));
    }

    public void stopKing(boolean forced) {
        if (this.king == null)
            return;

        if (this.king.isOnline()) {
            this.king.getInventory().remove(createLaser());
            this.king.getInventory().remove(createDash());
            this.king.setAllowFlight(false);
            this.king.setFlying(false);
        }

        String killer = (king.getKiller() == null ? "Unknown" : king.getKiller().getName());

        if (forced) {
            Bukkit.broadcastMessage(CC.translate("&6[KingOfTheHill] &eThe King Event was forcibly ended."));
        } else {
            Bukkit.broadcastMessage(
                    CC.translate("&6[KingOfTheHill] &c" + king.getName() + " &ewas killed by &c" + killer + "&e!"));
        }

        this.king = null;
        this.startedTime = 0L;
    }

    private ItemStack createLaser() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "King Laser");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Shot the laser to another players.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDash() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "King Dash");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Dash to another players.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isLaser(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_ROD)
            return false;
        if (!item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        return (ChatColor.GOLD + "King Laser").equals(meta.getDisplayName());
    }

    public boolean isDash(ItemStack item) {
        if (item == null || item.getType() != Material.FEATHER)
            return false;
        if (!item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        return (ChatColor.GOLD + "King Dash").equals(meta.getDisplayName());
    }
}
