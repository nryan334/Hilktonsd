package net.frozenorb.foxtrot.gameplay.ability.menu.editor;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AbilityEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_RED + "Ability Editor";
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final List<Ability> sortedAbilities = new ArrayList<>(Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values());
        sortedAbilities.sort(Comparator.comparing(Ability::getName));

        int slot = 0;
        for (final Ability ability : sortedAbilities) {
            buttons.put(slot++, new Button() {
                @Override
                public String getName(Player player) {
                    return ability.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.GRAY + "Internal Name: " + ChatColor.WHITE + ability.getName());
                    lore.add(ChatColor.GRAY + "Category: " + ChatColor.YELLOW + ability.getCategory().name());
                    lore.add(ChatColor.GRAY + "Material: " + ChatColor.WHITE + ability.getMaterial().name());

                    if (ability.getCooldown() > 0) {
                        lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + TimeUtils.formatIntoDetailedString((int) (ability.getCooldown() / 1000)));
                    } else {
                        lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.GREEN + "None");
                    }

                    boolean enabled = Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability);
                    lore.add(ChatColor.GRAY + "Status: " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
                    lore.add("");
                    lore.add(ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to edit settings");
                    lore.add(ChatColor.YELLOW + "Right-Click " + ChatColor.GRAY + "to give yourself this item");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return ability.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
                        player.getInventory().addItem(ability.getItem().clone());
                        player.sendMessage(ChatColor.GREEN + "You received 1x " + ability.getDisplayName() + ChatColor.GREEN + ".");
                    } else {
                        new AbilitySettingsMenu(ability).openMenu(player);
                    }
                }
            });
        }

        return buttons;
    }
}
