package net.frozenorb.foxtrot.gameplay.ability.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class PartnerItemMainMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Ability Items";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final List<Ability> abilities = new ArrayList<>();
        for (Ability value : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
            if (!Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(value)) {
                continue;
            }

            if (value.getCategory() == Category.PORTABLE_BARD) {
                continue;
            }

            if (!Foxtrot.getInstance().getMapHandler().isKitMap() && value.getCategory() == Category.KIT_MAP) {
                continue;
            }

            abilities.add(value);
        }

        abilities.sort(Comparator.comparing(Ability::getName));

        int index = 0;
        for (final Ability value : abilities) {
            buttons.put(index++, new Button() {
                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return value.getItem().clone();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (player.isOp()) {
                        player.getInventory().addItem(value.getItem().clone());
                        player.sendMessage(
                                ChatColor.GREEN + "You received 1x " + value.getDisplayName() + ChatColor.GREEN + ".");
                    }
                }
            });
        }

        return buttons;
    }
}
