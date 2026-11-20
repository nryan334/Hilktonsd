package net.frozenorb.foxtrot.gameplay.treasurebeach;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TreasureBeachHandler {

    private final File file;
    private final YamlConfiguration config;

    @Getter
    @Setter
    private String worldName = "";
    @Getter
    @Setter
    private int x1, y1, z1;
    @Getter
    @Setter
    private int x2, y2, z2;
    @Getter
    @Setter
    private boolean claimed = false;
    @Getter
    @Setter
    private long refillCooldownMs = 3 * 60 * 60 * 1000L;
    @Getter
    @Setter
    private long lastRefillTime = 0L;
    @Getter
    private final List<ItemStack> customLoot = new ArrayList<>();

    public TreasureBeachHandler() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/treasure-beach.yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!claimed)
                    return;
                long now = System.currentTimeMillis();
                if (now - lastRefillTime >= refillCooldownMs) {
                    refillChests();
                    lastRefillTime = now;
                    save();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20 * 60L, 20 * 60L);
    }

    @SuppressWarnings("unchecked")
    public void load() {
        if (config.contains("claimed")) {
            this.claimed = config.getBoolean("claimed");
            this.worldName = config.getString("world");
            this.x1 = config.getInt("x1");
            this.y1 = config.getInt("y1");
            this.z1 = config.getInt("z1");
            this.x2 = config.getInt("x2");
            this.y2 = config.getInt("y2");
            this.z2 = config.getInt("z2");
            this.refillCooldownMs = config.getLong("refillCooldownMs", 3 * 60 * 60 * 1000L);
            this.lastRefillTime = config.getLong("lastRefillTime", 0L);
            if (config.contains("customLoot")) {
                this.customLoot.clear();
                List<?> list = config.getList("customLoot");
                if (list != null) {
                    for (Object obj : list) {
                        if (obj instanceof ItemStack) {
                            this.customLoot.add((ItemStack) obj);
                        }
                    }
                }
            }
        }
    }

    public void save() {
        config.set("claimed", claimed);
        config.set("world", worldName);
        config.set("x1", x1);
        config.set("y1", y1);
        config.set("z1", z1);
        config.set("x2", x2);
        config.set("y2", y2);
        config.set("z2", z2);
        config.set("refillCooldownMs", refillCooldownMs);
        config.set("lastRefillTime", lastRefillTime);
        config.set("customLoot", customLoot);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refillChests() {
        if (!claimed)
            return;
        org.bukkit.World world = Bukkit.getWorld(worldName);
        if (world == null)
            return;

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = 0;
        int maxY = world.getMaxHeight();
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        int count = 0;
        Random random = new Random();

        List<ItemStack> pool = new ArrayList<>();
        if (!customLoot.isEmpty()) {
            pool.addAll(customLoot);
        } else {
            List<Ability> activeAbilities = new ArrayList<>();
            for (Ability ability : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
                if (Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability)) {
                    activeAbilities.add(ability);
                }
            }
            for (Ability ab : activeAbilities) {
                pool.add(ab.getItem().clone());
            }
            pool.add(createEnchanted(Material.DIAMOND_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
            pool.add(createEnchanted(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
            pool.add(createEnchanted(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
            pool.add(createEnchanted(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        }

        Set<Long> loadedChunks = new HashSet<>();
        Set<Inventory> refilled = new HashSet<>();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int chunkX = x >> 4;
                int chunkZ = z >> 4;
                long chunkKey = (((long) chunkX) << 32) | (chunkZ & 0xffffffffL);
                if (!loadedChunks.contains(chunkKey)) {
                    if (!world.isChunkLoaded(chunkX, chunkZ)) {
                        world.loadChunk(chunkX, chunkZ);
                    }
                    loadedChunks.add(chunkKey);
                }

                for (int y = minY; y <= maxY; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                        Chest chest = (Chest) block.getState();
                        Inventory inv = chest.getInventory();
                        if (refilled.add(inv)) {
                            inv.clear();

                            int itemsCount = 2 + random.nextInt(4);
                            for (int i = 0; i < itemsCount; i++) {
                                ItemStack loot = pool.get(random.nextInt(pool.size())).clone();
                                int slot = random.nextInt(inv.getSize());
                                inv.setItem(slot, loot);
                            }
                            count++;
                        }
                    }
                }
            }
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&eTreasure Beach&8] &7Refilled &b" + count + " &7chests inside the region."));
    }

    public boolean isInside(Location loc) {
        if (!claimed) {
            return false;
        }
        if (loc == null || loc.getWorld() == null || !loc.getWorld().getName().equalsIgnoreCase(worldName)) {
            return false;
        }
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    private ItemStack createEnchanted(Material mat, Enchantment ench, int lvl) {
        ItemStack item = new ItemStack(mat);
        item.addUnsafeEnchantment(ench, lvl);
        return item;
    }
}
