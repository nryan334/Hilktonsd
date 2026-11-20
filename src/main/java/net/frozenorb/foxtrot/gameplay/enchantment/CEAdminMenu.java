package net.frozenorb.foxtrot.gameplay.enchantment;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import org.cavepvp.suge.enchant.EnchantHandler;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CEAdminMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Custom Enchants Admin";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 0;

        try {
            List<CustomEnchant> sugeEnchants = Suge.getInstance().getEnchantHandler().getCustomEnchants().stream()
                    .filter(it -> it.getLevel() != Tier.CAVE)
                    .collect(Collectors.toList());

            for (CustomEnchant enchant : sugeEnchants) {
                buttons.put(slot++, new SugeEnchantButton(enchant));
            }
        } catch (Exception e) {
            Foxtrot.getInstance().getLogger().warning("Failed to load Suge enchants in CEAdminMenu: " + e.getMessage());
        }

        buttons.put(slot++, new ManualEnchantButton("Unrepairable", ChatColor.RED.toString() + "Unrepairable", Material.ANVIL));

        return buttons;
    }

    private static class SugeEnchantButton extends Button {
        private final CustomEnchant enchant;

        public SugeEnchantButton(CustomEnchant enchant) {
            this.enchant = enchant;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.AQUA + enchant.getName() + " " + enchant.getAmplifier();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to get a book for this enchant.");
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.ENCHANTED_BOOK;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            Foxtrot.getInstance().getServer().dispatchCommand(
                    Foxtrot.getInstance().getServer().getConsoleSender(),
                    "ce givebook " + player.getName() + " " + enchant.getName() + " " + enchant.getAmplifier());
            player.sendMessage(
                    ChatColor.GREEN + "Gave you " + enchant.getName() + " " + enchant.getAmplifier() + " book.");
        }
    }

    private static class ManualEnchantButton extends Button {
        private final String id;
        private final String displayName;
        private final Material material;

        public ManualEnchantButton(String id, String displayName, Material material) {
            this.id = id;
            this.displayName = displayName;
            this.material = material;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.AQUA + id;
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Lore applied: " + displayName);
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to get a book for this enchant.");
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return material;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            if (id.equalsIgnoreCase("Unrepairable")) {
                ItemStack book = ItemBuilder.of(Material.BOOK)
                        .name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Unrepairable Book")
                        .addToLore(CC.translate("&cDouble Click this book on the armor you want to put it on!"))
                        .build();
                player.getInventory().addItem(book);
                player.sendMessage(ChatColor.GREEN + "Gave you Unrepairable book.");
            }
        }
    }
}
