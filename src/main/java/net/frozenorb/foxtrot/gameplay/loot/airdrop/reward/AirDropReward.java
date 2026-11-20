package net.frozenorb.foxtrot.gameplay.loot.airdrop.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
@Setter
public class AirDropReward {
    private ItemStack itemStack;
    private double chance;
    private String command;
    private boolean grantItem;
}
