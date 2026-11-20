package net.frozenorb.foxtrot.gameplay.loot.lootboxes;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootboxHandler {
    private final Foxtrot instance;
    private final File folder;
    private final Map<String, Lootbox> lootboxes = new HashMap<>();

    public LootboxHandler(Foxtrot instance) {
        this.instance = instance;
        this.folder = new File(instance.getDataFolder(), "lootboxes");
        if (!folder.exists()) folder.mkdirs();

        this.load();
        
        Bukkit.getPluginManager().registerEvents(new LootboxListener(instance), instance);
    }

    public void load() {
        lootboxes.clear();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String id = config.getString("id");
            if (id == null) continue;

            Lootbox lootbox = new Lootbox(id);
            lootbox.setDisplayName(config.getString("displayName", id));
            lootbox.setMaterial(Material.valueOf(config.getString("material", "CHEST")));
            lootbox.setData((short) config.getInt("data", 0));
            lootbox.setLore(config.getStringList("lore"));
            lootbox.setRewardCount(config.getInt("rewardCount", 3));
            lootbox.setStoreUrl(config.getString("storeUrl", "store.strivepvp.top"));

            List<LootboxItem> items = new ArrayList<>();
            ConfigurationSection itemsSection = config.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ItemStack itemStack = itemsSection.getItemStack(key + ".itemStack");
                    if (itemStack == null) continue;

                    double chance = itemsSection.getDouble(key + ".chance");
                    List<String> commands = itemsSection.getStringList(key + ".commands");
                    LootboxItem.ExecutionType type = LootboxItem.ExecutionType.valueOf(itemsSection.getString(key + ".executionType", "ITEM_ONLY"));
                    boolean broadcast = itemsSection.getBoolean(key + ".broadcast", false);

                    items.add(new LootboxItem(itemStack, chance, commands, type, broadcast));
                }
            }
            lootbox.setItems(items);
            lootboxes.put(id.toLowerCase(), lootbox);
        }
    }

    public void save(Lootbox lootbox) {
        File file = new File(folder, lootbox.getId().toLowerCase() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("id", lootbox.getId());
        config.set("displayName", lootbox.getDisplayName());
        config.set("material", lootbox.getMaterial().name());
        config.set("data", lootbox.getData());
        config.set("lore", lootbox.getLore());
        config.set("rewardCount", lootbox.getRewardCount());
        config.set("storeUrl", lootbox.getStoreUrl());

        int i = 0;
        for (LootboxItem item : lootbox.getItems()) {
            String key = "items.item_" + i;
            config.set(key + ".itemStack", item.getItemStack());
            config.set(key + ".chance", item.getChance());
            config.set(key + ".commands", item.getCommands());
            config.set(key + ".executionType", item.getExecutionType().name());
            config.set(key + ".broadcast", item.isBroadcast());
            i++;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(Lootbox lootbox) {
        lootboxes.remove(lootbox.getId().toLowerCase());
        File file = new File(folder, lootbox.getId().toLowerCase() + ".yml");
        if (file.exists()) file.delete();
    }

    public Lootbox getLootbox(String id) {
        return lootboxes.get(id.toLowerCase());
    }

    public Map<String, Lootbox> getLootboxes() {
        return lootboxes;
    }
}
