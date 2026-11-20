package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class TotemOfUnderdying extends Ability {

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getName() {
        return "TotemOfUnderdying";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Totem of Underdying";
    }

    @Override
    public Material getMaterial() {
        return Material.CLAY_BALL;
    }

    @Override
    public String getDescription() {
        return "Saves you from death!";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fAutomatically activates when you reach &c&l1 heart&f."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fGrants &eRegen V&f, &eAbsorption II&f, &eStrength II&f, &eRes II"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &f for &c&l8 seconds&f."));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &c4m"));
        return lore;
    }

    @Override
    public long getCooldown() {
        return 240_000L;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (player.getHealth() - event.getFinalDamage() <= 2.0) {
            
            if (hasCooldown(player)) {
                return;
            }

            ItemStack totemItem = null;
            for (ItemStack item : player.getInventory().getContents()) {
                if (isSimilar(item)) {
                    totemItem = item;
                    break;
                }
            }

            if (totemItem == null) {
                return;
            }

            event.setDamage(0);
            player.setHealth(2.0);
            
            if (totemItem.getAmount() > 1) {
                totemItem.setAmount(totemItem.getAmount() - 1);
            } else {
                player.getInventory().removeItem(totemItem);
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 8 * 20, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8 * 20, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 8 * 20, 1));

            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "TOTEM OF UNDERDYING ACTIVATED!");
            player.sendMessage(ChatColor.YELLOW + "You have been saved from death and granted divine protection!");
            player.sendMessage("");

            this.applyCooldown(player);
        }
    }
}
