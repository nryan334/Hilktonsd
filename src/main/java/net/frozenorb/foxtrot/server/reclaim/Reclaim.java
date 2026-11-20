package net.frozenorb.foxtrot.server.reclaim;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

@Getter
@Setter
public class Reclaim {

    private String name;
    private int priority;
    private List<String> commands;
    private List<ItemStack> items;

    public Reclaim(String name) {
        this.name = name;
        this.priority = 99;
        this.commands = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public Reclaim(String name, int priority, List<String> commands, List<ItemStack> items) {
        this.name = name;
        this.priority = priority;
        this.commands = commands != null ? commands : new ArrayList<>();
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getPermission() {
        return "reclaims." + name.toLowerCase();
    }

    public void save(File file, FileConfiguration dataConfig) {
        dataConfig.set("name", this.name);
        dataConfig.set("priority", this.priority);
        dataConfig.set("commands", this.commands);

        dataConfig.set("items", null);
        if (this.items != null && !this.items.isEmpty()) {
            List<String> serializedItems = new ArrayList<>();
            for (ItemStack item : this.items) {
                if (item != null) {
                    serializedItems.add(ReclaimHandler.itemToBase64(item));
                }
            }
            dataConfig.set("items", serializedItems);
        }

        try {
            dataConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
