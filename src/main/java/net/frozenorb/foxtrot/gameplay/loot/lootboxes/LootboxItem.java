package net.frozenorb.foxtrot.gameplay.loot.lootboxes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LootboxItem {
    private ItemStack itemStack;
    private double chance;
    private List<String> commands;
    private ExecutionType executionType;
    private boolean broadcast;

    public enum ExecutionType {
        CONSOLE, PLAYER, ITEM_ONLY
    }
}
