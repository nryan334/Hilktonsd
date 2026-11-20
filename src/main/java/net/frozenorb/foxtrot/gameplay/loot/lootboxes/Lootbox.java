package net.frozenorb.foxtrot.gameplay.loot.lootboxes;

import cc.fyre.proton.util.ItemBuilder;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lootbox {
    private final String id;
    private String displayName;
    private Material material;
    private short data;
    private List<String> lore = new ArrayList<>();
    private List<LootboxItem> items = new ArrayList<>();
    private int rewardCount = 3;
    private String storeUrl = "store.strivepvp.top";

    public Lootbox(String id) {
        this.id = id;
        this.displayName = "&c" + id + " Lootbox";
        this.material = Material.CHEST;
    }

    public ItemStack getItemStack() {
        return ItemBuilder.of(material)
                .data(data)
                .name(ChatColor.translateAlternateColorCodes('&', displayName))
                .setLore(lore)
                .build();
    }
}
