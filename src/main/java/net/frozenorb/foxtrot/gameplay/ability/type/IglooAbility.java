package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;


/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class IglooAbility extends Ability {

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getName() {
        return "Igloo";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Igloo";
    }

    @Override
    public Material getMaterial() {
        return Material.ICE;
    }

    @Override
    public String getDescription() {
        return "Creates a protective ice igloo!";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fRight-click to create an &b&l8x8 ice igloo"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &faround you for &c&l8 seconds&f."));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &c1m15s"));
        return lore;
    }

    @Override
    public long getCooldown() {
        return 75_000L;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        Player player = event.getPlayer();
        if (!isSimilar(event.getItem())) {
            return;
        }

        AbilityUseEvent useEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled()) {
            return;
        }

        this.takeItem(player);

        Location center = player.getLocation().getBlock().getLocation();
        Set<Block> blocksToRevert = new HashSet<>();
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance > radius - 1 && distance <= radius) {
                        Block block = center.clone().add(x, y, z).getBlock();
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.ICE);
                            block.setMetadata("IGLOO_BLOCK", new org.bukkit.metadata.FixedMetadataValue(Foxtrot.getInstance(), true));
                            blocksToRevert.add(block);
                        }
                    }
                }
            }
        }

        player.sendMessage(ChatColor.GREEN + "Igloo created!");

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            for (Block block : blocksToRevert) {
                if (block.getType() == Material.ICE) {
                    block.setType(Material.AIR);
                    block.removeMetadata("IGLOO_BLOCK", Foxtrot.getInstance());
                }
            }
            if (player.isOnline()) {
                player.sendMessage(ChatColor.RED + "Your igloo has melted!");
            }
        }, 8 * 20L);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("IGLOO_BLOCK")) {
            event.setCancelled(true);
        }
    }
}
