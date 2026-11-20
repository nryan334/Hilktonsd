package net.frozenorb.foxtrot.gameplay.events.koth.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.koth.Koth;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KothEditorMenu extends Menu {

    private final Koth koth;

    public KothEditorMenu(Koth koth) {
        this.koth = koth;
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Editing: " + koth.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&aEdit Time");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Current: &f" + (koth.getMinutes() / 60000) + " minutes"));
                desc.add("");
                desc.add(CC.translate("&eLeft click to add 5 minutes"));
                desc.add(CC.translate("&cRight click to remove 5 minutes"));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WATCH;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                long current = koth.getMinutes() / 60000;
                if (clickType.isLeftClick()) {
                    current += 5;
                } else if (clickType.isRightClick()) {
                    current = Math.max(1, current - 5);
                }
                koth.setMinutes(current * 60000);
                koth.setRemaining(koth.getMinutes());
                koth.save();
                player.sendMessage(CC.translate("&aTime updated to " + current + " minutes."));
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&eEdit Rewards (Loot)");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Click to open the loot editor"));
                desc.add(CC.translate("&7for this KOTH."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, "Editing Loot: " + koth.getName());
                for (org.bukkit.inventory.ItemStack stack : koth.getLoot()) {
                    if (stack != null) {
                        inv.addItem(stack);
                    }
                }
                
                player.openInventory(inv);
                player.setMetadata("editing_koth_loot", new org.bukkit.metadata.FixedMetadataValue(net.frozenorb.foxtrot.Foxtrot.getInstance(), koth.getName()));
                player.sendMessage(net.frozenorb.foxtrot.util.CC.translate("&aOpened loot editor."));
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&bEdit Color");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Current: " + koth.getColor() + "Color Example"));
                desc.add("");
                desc.add(CC.translate("&eClick to cycle colors."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 4;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new KothColorMenu(koth).openMenu(player);
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&dSet Schedule");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Click to open the schedule editor."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage(CC.translate("&cSchedule system is currently pending migration for KothManager."));
                player.closeInventory();
            }
        });

        return buttons;
    }
}
