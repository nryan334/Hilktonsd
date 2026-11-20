package net.frozenorb.foxtrot.gameplay.events.koth.menu;

import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.gameplay.events.koth.Koth;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KothLootEditorMenu extends Menu {

    private final Koth koth;

    public KothLootEditorMenu(Koth koth) {
        this.koth = koth;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Loot: " + koth.getName();
    }

    @Override
    public void onOpen(Player player) {
        player.getInventory().clear();
        for (ItemStack stack : koth.getLoot()) {
            if (stack != null) {
                player.getInventory().addItem(stack);
            }
        }
        player.sendMessage(net.frozenorb.foxtrot.util.CC
                .translate("&aAdd or remove items from your inventory, then close it to save."));
    }

    @Override
    public void onClose(Player player) {
        koth.getLoot().clear();
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null) {
                koth.getLoot().add(stack.clone());
            }
        }
        koth.save();
        player.getInventory().clear();
        player.sendMessage(net.frozenorb.foxtrot.util.CC.translate("&aLoot saved for " + koth.getName() + " KOTH."));
    }

    @Override
    public Map<Integer, cc.fyre.proton.menu.Button> getButtons(Player player) {
        return new HashMap<>();
    }
}
