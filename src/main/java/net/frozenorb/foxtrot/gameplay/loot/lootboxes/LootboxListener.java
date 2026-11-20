package net.frozenorb.foxtrot.gameplay.loot.lootboxes;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu.LootboxRollInventory;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu.LootboxRewardsMenu;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.LootboxItem;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootboxListener implements Listener {
    private final Foxtrot instance;

    public LootboxListener(Foxtrot instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        for (Lootbox lootbox : instance.getLootboxHandler().getLootboxes().values()) {
            if (lootbox.getItemStack().isSimilar(item)) {
                event.setCancelled(true);

                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.setItemInHand(null);
                }

                new LootboxRollInventory(lootbox, player).open();
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (LootboxRollInventory.activeRolls.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (LootboxRollInventory.activeRolls.containsKey(player.getUniqueId())) {
            LootboxRollInventory roll = LootboxRollInventory.activeRolls.get(player.getUniqueId());
            if (!roll.isRollFinished()) {
                instance.getServer().getScheduler().runTaskLater(instance, () -> {
                    player.openInventory(event.getInventory());
                }, 1L);
                return;
            }
            roll.handleClose();
        }

        if (event.getInventory().getTitle().startsWith("Lootbox Drop: ")) {
            String id = event.getInventory().getTitle().replace("Lootbox Drop: ", "");
            Lootbox lootbox = instance.getLootboxHandler().getLootbox(id);
            if (lootbox == null)
                return;

            List<LootboxItem> newItems = new ArrayList<>();
            for (ItemStack item : event.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR)
                    continue;

                LootboxItem existing = lootbox.getItems().stream()
                        .filter(it -> it.getItemStack().isSimilar(item))
                        .findFirst().orElse(null);

                if (existing != null) {
                    newItems.add(existing);
                } else {
                    newItems.add(new LootboxItem(item.clone(), 1.0, new ArrayList<>(),
                            LootboxItem.ExecutionType.ITEM_ONLY, false));
                }
            }

            lootbox.setItems(newItems);
            instance.getLootboxHandler().save(lootbox);
            ((Player) event.getPlayer()).sendMessage(ChatColor.GREEN + "Saved lootbox rewards!");

            instance.getServer().getScheduler().runTaskLater(instance, () -> {
                new LootboxRewardsMenu(lootbox).openMenu((Player) event.getPlayer());
            }, 1L);
        }
    }
}
