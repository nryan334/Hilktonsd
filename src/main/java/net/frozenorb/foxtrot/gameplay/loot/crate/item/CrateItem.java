package net.frozenorb.foxtrot.gameplay.loot.crate.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

@AllArgsConstructor
@Getter
@Setter
public class CrateItem {
    private ItemStack itemStack;
    private double chance;
    private String command;
    private boolean giveItem;
    private boolean broadcast;
    private int slot;
}
