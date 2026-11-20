package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
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
public class Crate {
    private String id;
    private String displayName;
    private Material material;
    private short data;
    private Material blockMaterial;
    private short blockData;
    private Color fireworkColor;
    private List<String> lore;
    private List<String> hologramLines;
    private int rows = 3;
    private ItemStack fillerItem;
    private List<CrateItem> items;
    private boolean sellableOnTokenShop = false;
    private boolean fillerGlow = false;

    public Crate(String id, String displayName, Material material, short data, Material blockMaterial, short blockData, Color fireworkColor, List<String> lore,
            List<String> hologramLines, int rows, ItemStack fillerItem, List<CrateItem> items) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.data = data;
        this.blockMaterial = blockMaterial != null ? blockMaterial : Material.CHEST;
        this.blockData = blockData;
        this.fireworkColor = fireworkColor;
        this.lore = lore;
        this.hologramLines = hologramLines != null ? hologramLines : new ArrayList<>();
        this.rows = rows > 0 ? rows : 3;
        this.fillerItem = fillerItem;
        this.items = items;
    }

    public ItemStack getItemStack() {
        final List<String> newLore = new ArrayList<>();
        lore.forEach(it -> newLore.add(ChatColor.translateAlternateColorCodes('&', it)));

        return ItemBuilder.of(this.material).data(this.data).name(ChatColor.translateAlternateColorCodes('&', this.displayName))
                .setLore(newLore).build();
    }

    public List<String> getHologramLines() {
        if (hologramLines == null) {
            hologramLines = new ArrayList<>();
        }
        return hologramLines;
    }

    public void saveCrate(File file, FileConfiguration dataConfig, Crate crate) {
        Map<String, Object> configValues = dataConfig.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            dataConfig.set(entry.getKey(), null);

        dataConfig.set("id", crate.getId());
        dataConfig.set("displayName", crate.getDisplayName());
        dataConfig.set("material", crate.getMaterial().name());
        dataConfig.set("data", crate.getData());
        dataConfig.set("blockMaterial", crate.getBlockMaterial() != null ? crate.getBlockMaterial().name() : Material.CHEST.name());
        dataConfig.set("blockData", crate.getBlockData());
        dataConfig.set("fireworkColor", crate.getFireworkColor().asRGB());
        dataConfig.set("lore", crate.getLore());
        dataConfig.set("hologramLines", crate.hologramLines);
        dataConfig.set("rows", crate.getRows());
        if (crate.getFillerItem() != null) {
            dataConfig.set("fillerItem", crate.getFillerItem());
        }
        dataConfig.set("fillerGlow", crate.isFillerGlow());
        dataConfig.set("sellableOnTokenShop", crate.isSellableOnTokenShop());

        int i = 0;
        for (CrateItem crateItem : crate.getItems()) {
            i++;
            dataConfig.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".itemStack",
                    crateItem.getItemStack());
            dataConfig.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".chance",
                    crateItem.getChance());
            dataConfig.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".command",
                    crateItem.getCommand());
            dataConfig.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".giveItem",
                    crateItem.isGiveItem());
            dataConfig.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".broadcast",
                    crateItem.isBroadcast());
            dataConfig.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".slot",
                    crateItem.getSlot());
        }

        if (file != null) {
            try {
                dataConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
