package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TokenItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();
        
        if (displayName.equals(ChatColor.translateAlternateColorCodes('&', "&b&lBase Token")) && item.getType() == Material.STAINED_GLASS) {
            event.setCancelled(true);
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            if (team == null) {
                player.sendMessage(ChatColor.GRAY + "You are not in a faction!");
                return;
            }

            team.setBaseTokens(team.getBaseTokens() + 1);
            player.sendMessage(ChatColor.GREEN + "Successfully redeemed 1 Base Token for your faction!");
            
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.setItemInHand(null);
            }
            player.updateInventory();
            return;
        }

        if (displayName.equals(ChatColor.translateAlternateColorCodes('&', "&4&lFaller Token")) && item.getType() == Material.HOPPER) {
            event.setCancelled(true);
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            if (team == null) {
                player.sendMessage(ChatColor.GRAY + "You are not in a faction!");
                return;
            }

            team.setFallTrapTokens(team.getFallTrapTokens() + 1);
            player.sendMessage(ChatColor.GREEN + "Successfully redeemed 1 Faller Token for your faction!");
            
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.setItemInHand(null);
            }
            player.updateInventory();
            return;
        }
    }
}
