package net.frozenorb.foxtrot.npc;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.util.InventorySerialization;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NPC {
    private String id;
    private String displayName;
    private Location location;

    private String skinName;
    private String skinValue;
    private String skinSignature;

    private List<String> hologramLines = new ArrayList<>();
    private List<String> commands = new ArrayList<>();
    private boolean executeAsPlayer = true;

    private ItemStack handItem;
    private ItemStack[] armor = new ItemStack[4];

    private boolean lookAtPlayer = true;
    private double hologramOffset = 3.3;

    public NPC(String id, Location location) {
        this.id = id;
        this.location = location;
        this.displayName = id;
    }

    public BasicDBObject toDBObject() {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("id", id);
        dbObject.put("displayName", displayName);
        dbObject.put("world", location.getWorld().getName());
        dbObject.put("x", location.getX());
        dbObject.put("y", location.getY());
        dbObject.put("z", location.getZ());
        dbObject.put("yaw", (double) location.getYaw());
        dbObject.put("pitch", (double) location.getPitch());
        dbObject.put("lookAtPlayer", lookAtPlayer);
        dbObject.put("executeAsPlayer", executeAsPlayer);
        dbObject.put("hologramOffset", hologramOffset);
        dbObject.put("skinName", skinName);
        dbObject.put("skinValue", skinValue);
        dbObject.put("skinSignature", skinSignature);
        dbObject.put("commands", commands);
        dbObject.put("hologramLines", hologramLines);

        if (handItem != null) {
            dbObject.put("handItem", InventorySerialization.serialize(handItem));
        }

        BasicDBList armorList = new BasicDBList();
        for (ItemStack item : armor) {
            armorList.add(InventorySerialization.serialize(item));
        }
        dbObject.put("armor", armorList);

        return dbObject;
    }

    public static NPC fromDBObject(BasicDBObject dbObject) {
        Location loc = new Location(
                Bukkit.getWorld(dbObject.getString("world")),
                dbObject.getDouble("x"),
                dbObject.getDouble("y"),
                dbObject.getDouble("z"),
                (float) dbObject.getDouble("yaw"),
                (float) dbObject.getDouble("pitch"));

        NPC npc = new NPC(dbObject.getString("id"), loc);
        npc.setDisplayName(dbObject.getString("displayName"));
        npc.setLookAtPlayer(dbObject.getBoolean("lookAtPlayer"));
        npc.setExecuteAsPlayer(dbObject.getBoolean("executeAsPlayer"));

        if (dbObject.containsField("hologramOffset")) {
            double offset = dbObject.getDouble("hologramOffset");
            if (offset < 2.5 || offset == 3.2 || offset == 2.8) {
                offset = 3.3;
            }
            npc.setHologramOffset(offset);
        } else {
            npc.setHologramOffset(3.3);
        }

        npc.setSkinName(dbObject.getString("skinName"));
        npc.setSkinValue(dbObject.getString("skinValue"));
        npc.setSkinSignature(dbObject.getString("skinSignature"));

        if (dbObject.containsField("commands")) {
            npc.setCommands((List<String>) dbObject.get("commands"));
        }

        if (dbObject.containsField("hologramLines")) {
            npc.setHologramLines((List<String>) dbObject.get("hologramLines"));
        }

        if (dbObject.containsField("handItem")) {
            npc.setHandItem(InventorySerialization.deserialize((BasicDBObject) dbObject.get("handItem")));
        }

        if (dbObject.containsField("armor")) {
            BasicDBList armorList = (BasicDBList) dbObject.get("armor");
            ItemStack[] armor = new ItemStack[4];
            for (int i = 0; i < 4; i++) {
                armor[i] = InventorySerialization.deserialize((BasicDBObject) armorList.get(i));
            }
            npc.setArmor(armor);
        }

        return npc;
    }
}
