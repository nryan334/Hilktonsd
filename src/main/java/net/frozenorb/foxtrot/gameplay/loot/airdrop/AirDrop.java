package net.frozenorb.foxtrot.gameplay.loot.airdrop;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import cc.fyre.proton.util.ItemBuilder;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

@Getter
@Setter
public class AirDrop {

    private String id;
    private String displayName;
    private Material material;
    private short data;
    private List<String> lore;
    private List<AirDropReward> rewards;

    public AirDrop(String id) {
        this.id = id;
        this.displayName = ChatColor.AQUA + ChatColor.BOLD.toString() + id + " Air Drop";
        this.material = Material.DROPPER;
        this.data = 0;
        this.lore = new ArrayList<>();
        this.rewards = new ArrayList<>();
    }

    public AirDrop(String id, String displayName, Material material, short data, List<String> lore,
            List<AirDropReward> rewards) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.data = data;
        this.lore = lore;
        this.rewards = rewards;
    }

    public ItemStack getItemStack() {
        return ItemBuilder.of(material).data(data).name(displayName).setLore(lore).build();
    }
}
