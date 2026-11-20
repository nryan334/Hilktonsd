package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.Proton;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CrateBlockHandler {
    private final Foxtrot instance;
    @Getter
    private final List<CrateBlock> crateBlocks = new ArrayList<>();
    private final File file;
    private FileConfiguration config;

    public CrateBlockHandler(Foxtrot instance) {
        this.instance = instance;
        this.file = new File(instance.getDataFolder(), "data/crate-blocks.yml");
        this.load();
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("blocks")) {
            for (String key : config.getConfigurationSection("blocks").getKeys(false)) {
                String crateId = config.getString("blocks." + key + ".crateId");
                String locStr = config.getString("blocks." + key + ".location");
                Location loc = deserializeLocation(locStr);
                if (loc == null)
                    continue;

                CrateBlock block = new CrateBlock(crateId, loc, null);
                crateBlocks.add(block);

                instance.getServer().getScheduler().runTaskLater(instance, block::spawnHologram, 20L);
            }
        }
    }

    public void save() {
        config.set("blocks", null);
        int i = 0;
        for (CrateBlock block : crateBlocks) {
            String path = "blocks." + (i++);
            config.set(path + ".crateId", block.getCrateId());
            config.set(path + ".location", serializeLocation(block.getLocation()));
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serializeLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return "null";
        }
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location deserializeLocation(String str) {
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null"))
            return null;

        try {
            String[] parts = str.split(",");

            if (parts.length < 4) {
                return null;
            }

            return new Location(Bukkit.getWorld(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<CrateBlock> getAt(Location loc) {
        return crateBlocks.stream().filter(it -> it.getLocation().getBlockX() == loc.getBlockX() &&
                it.getLocation().getBlockY() == loc.getBlockY() &&
                it.getLocation().getBlockZ() == loc.getBlockZ() &&
                it.getLocation().getWorld().equals(loc.getWorld()))
                .findFirst();
    }

    public void addBlock(CrateBlock block) {
        crateBlocks.add(block);
        block.spawnHologram();
        save();
    }

    public void removeBlock(CrateBlock block) {
        block.removeHologram();
        crateBlocks.remove(block);
        save();
    }

    public ItemStack getCrateBlockItem(Crate crate) {
        ItemStack item = new ItemStack(crate.getBlockMaterial() != null ? crate.getBlockMaterial() : Material.CHEST, 1,
                crate.getBlockData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crate Block: "
                + ChatColor.translateAlternateColorCodes('&', crate.getDisplayName()));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "ID: " + crate.getId());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Place this to create a physical crate!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void refreshHolograms(String crateId) {
        crateBlocks.stream()
                .filter(it -> it.getCrateId().equalsIgnoreCase(crateId))
                .filter(it -> it.getLocation() != null && it.getLocation().getWorld() != null)
                .forEach(it -> {
                    Proton.getInstance().getHologramHandler().getCache().values().stream()
                            .filter(h -> h.getLocation().getWorld().equals(it.getLocation().getWorld()) &&
                                    h.getLocation().distanceSquared(it.getLocation().clone().add(0.5, 0.5, 0.5)) < 4.0)
                            .collect(java.util.stream.Collectors.toList())
                            .forEach(h -> h.delete());

                    it.spawnHologram();
                });
    }
}
