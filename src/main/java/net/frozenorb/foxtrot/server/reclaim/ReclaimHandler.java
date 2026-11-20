package net.frozenorb.foxtrot.server.reclaim;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.reclaim.menu.ReclaimItemEditMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class ReclaimHandler implements Listener {

    private final Foxtrot instance;

    @Getter
    private final List<Reclaim> reclaims = new ArrayList<>();

    @Getter
    private File folder;

    public ReclaimHandler(Foxtrot instance) {
        this.instance = instance;
        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
        this.loadData();
    }

    public void loadData() {
        this.reclaims.clear();
        
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Reclaims");
        try (DBCursor cursor = collection.find()) {
            while (cursor.hasNext()) {
                DBObject object = cursor.next();
                if (object instanceof BasicDBObject) {
                    BasicDBObject dbObject = (BasicDBObject) object;
                    String name = dbObject.getString("name");
                    int priority = dbObject.getInt("priority", 99);
                    
                    List<String> commands = new ArrayList<>();
                    if (dbObject.containsField("commands")) {
                        BasicDBList commandList = (BasicDBList) dbObject.get("commands");
                        for (Object cmd : commandList) {
                            commands.add((String) cmd);
                        }
                    }
                    
                    List<ItemStack> items = new ArrayList<>();
                    if (dbObject.containsField("items")) {
                        BasicDBList itemList = (BasicDBList) dbObject.get("items");
                        for (Object obj : itemList) {
                            try {
                                ItemStack item = itemFromBase64((String) obj);
                                if (item != null) {
                                    items.add(item);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                    Reclaim reclaim = new Reclaim(name, priority, commands, items);
                    this.reclaims.add(reclaim);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveReclaim(Reclaim reclaim) {
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Reclaims");
        
        BasicDBObject document = new BasicDBObject("name", reclaim.getName());
        document.put("priority", reclaim.getPriority());
        
        BasicDBList commandList = new BasicDBList();
        commandList.addAll(reclaim.getCommands());
        document.put("commands", commandList);
        
        BasicDBList itemList = new BasicDBList();
        if (reclaim.getItems() != null) {
            for (ItemStack item : reclaim.getItems()) {
                if (item != null && item.getType() != Material.AIR) {
                    try {
                        itemList.add(itemToBase64(item));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        document.put("items", itemList);
        
        BasicDBObject query = new BasicDBObject("name", reclaim.getName());
        collection.update(query, document, true, false);
    }

    public void deleteReclaim(Reclaim reclaim) {
        this.reclaims.remove(reclaim);
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Reclaims");
        collection.remove(new BasicDBObject("name", reclaim.getName()));
    }

    public Optional<Reclaim> getReclaim(String name) {
        return this.reclaims.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != null && event.getInventory().getTitle().startsWith("Reclaim Items: ")) {
            Player player = (Player) event.getPlayer();
            String reclaimName = event.getInventory().getTitle().replace("Reclaim Items: ", "");

            Reclaim reclaim = this.getReclaim(reclaimName).orElse(null);
            if (reclaim == null) return;

            Inventory inv = event.getInventory();
            List<ItemStack> newItems = new ArrayList<>();

            for (ItemStack item : inv.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    newItems.add(item.clone());
                }
            }

            reclaim.setItems(newItems);
            this.saveReclaim(reclaim);

            player.sendMessage(ChatColor.GREEN + "Saved " + newItems.size() + " items to reclaim " + reclaim.getName() + ".");

            Bukkit.getScheduler().runTaskLater(instance, () -> {
                new ReclaimItemEditMenu(reclaim).openMenu(player);
            }, 1L);
        }
    }

    public static String itemToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public static ItemStack itemFromBase64(String data) throws IOException {
        try {
            if (data == null || data.trim().isEmpty() || data.trim().equalsIgnoreCase("null")) {
                return null;
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (Exception e) {
            throw new IOException("Unable to load item stack.", e);
        }
    }
}
