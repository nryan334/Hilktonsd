package net.frozenorb.foxtrot.gameplay.ability;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;

@Getter
@Setter
public class Category {

    private static final Map<String, Category> categories = new LinkedHashMap<>();

    public static Category KIT_MAP;
    public static Category PORTABLE_BARD;
    public static Category PARTNER_CRATE;
    public static Category AIRDROPS;
    public static Category SEASONAL_CRATE;
    public static Category SEASONAL_LOOTBOX;
    public static Category TREASURE_CHEST;
    public static Category ALL;

    private String name;
    private String displayName;
    private String simpleName;
    private Material displayItem;
    private byte glassData;
    private int slot;

    public Category(String name, String displayName, String simpleName, Material displayItem, byte glassData,
            int slot) {
        this.name = name;
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.simpleName = simpleName;
        this.displayItem = displayItem;
        this.glassData = glassData;
        this.slot = slot;
    }

    public static Category[] values() {
        return categories.values().toArray(new Category[0]);
    }

    public static Category valueOf(String name) {
        return categories.get(name.toUpperCase());
    }

    public static Category fromName(String name) {
        if (name == null) return null;
        return categories.get(name.toUpperCase());
    }

    public static void loadCategories() {
        categories.clear();
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME)
                .getCollection("AbilityCategories");

        try (DBCursor cursor = collection.find()) {
            while (cursor.hasNext()) {
                BasicDBObject dbObject = (BasicDBObject) cursor.next();
                String name = dbObject.getString("name");
                String displayName = dbObject.getString("displayName");
                String simpleName = dbObject.getString("simpleName");
                Material displayItem = Material.valueOf(dbObject.getString("displayItem"));
                byte glassData = (byte) dbObject.getInt("glassData");
                int slot = dbObject.getInt("slot");

                Category category = new Category(name, displayName, simpleName, displayItem, glassData, slot);
                categories.put(name.toUpperCase(), category);
            }
        }

        ALL = getOrCreate("ALL", "&4&lView All", "View All", Material.NETHER_STAR, (byte) 0, 13);
        KIT_MAP = getOrCreate("KIT_MAP", "&e&lKit Map", "Kit Map", Material.AIR, (byte) 0, 0);
        PORTABLE_BARD = getOrCreate("PORTABLE_BARD", "&6&lPortable Bard", "Portable Bard", Material.AIR, (byte) 0, 0);
        PARTNER_CRATE = getOrCreate("PARTNER_CRATE", "&a&lPartner Items", "Partner Items", Material.EYE_OF_ENDER,
                (byte) 5, 11);
        AIRDROPS = getOrCreate("AIRDROPS", "&b&lAirdrop", "Airdrop", Material.DROPPER, (byte) 3, 12);
        SEASONAL_CRATE = getOrCreate("SEASONAL_CRATE", "&4&l3.0 Crate", "3.0 Crate", Material.TRIPWIRE_HOOK, (byte) 2,
                14);
        SEASONAL_LOOTBOX = getOrCreate("SEASONAL_LOOTBOX", "&c&lIndependence &b&lDay &f&lLootbox", "Seasonal Lootbox",
                Material.CHEST, (byte) 2, 15);
        TREASURE_CHEST = getOrCreate("TREASURE_CHEST", "&e&lTreasure Chest", "Treasure Chest", Material.ENDER_CHEST,
                (byte) 13, 16);
    }

    private static Category getOrCreate(String name, String displayName, String simpleName, Material displayItem,
            byte glassData, int slot) {
        Category category = categories.get(name.toUpperCase());
        if (category == null) {
            category = new Category(name, displayName, simpleName, displayItem, glassData, slot);
            saveCategory(category);
        }
        return category;
    }

    public static void saveCategory(Category category) {
        categories.put(category.getName().toUpperCase(), category);
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME)
                .getCollection("AbilityCategories");

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("name", category.getName());
        dbObject.put("displayName", category.getDisplayName());
        dbObject.put("simpleName", category.getSimpleName());
        dbObject.put("displayItem", category.getDisplayItem().name());
        dbObject.put("glassData", (int) category.getGlassData());
        dbObject.put("slot", category.getSlot());

        collection.update(new BasicDBObject("name", category.getName()), dbObject, true, false);
    }

    public static void deleteCategory(String name) {
        categories.remove(name.toUpperCase());
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME)
                .getCollection("AbilityCategories");
        collection.remove(new BasicDBObject("name", name));
    }

    public boolean isCategory(Ability ability, Category category) {
        if (ability.getCategory() != null && ability.getCategory().getName().equalsIgnoreCase(category.getName())) {
            return true;
        }
        return category.getName().equalsIgnoreCase("ALL");
    }

    public String name() {
        return name;
    }

    public int ordinal() {
        List<Category> list = new ArrayList<>(categories.values());
        return list.indexOf(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
