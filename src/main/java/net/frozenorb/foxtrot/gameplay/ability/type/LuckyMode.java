package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LuckyMode extends Ability {

    private final Random random = new Random();

    public LuckyMode() {
        this.hassanStack = ItemBuilder.of(this.getMaterial()).data((short) 11).name(this.getDisplayName())
                .setLore(this.getLore()).build();
    }

    @Override
    public String getName() {
        return "LuckyMode";
    }

    @Override
    public Material getMaterial() {
        return Material.INK_SACK;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Lucky Mode";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fRight-click to gamble with your luck!"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &f50% chance for incredible buffs,"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &f50% chance for nasty debuffs!"));
        lore.add("");
        return lore;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (location.getWorld().getName().equalsIgnoreCase("sg")) {
            return true;
        }
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location)
                    && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location)
                    && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }
        return !Foxtrot.getInstance().getServerHandler().isWarzone(location)
                && location.getWorld().getEnvironment() == World.Environment.NORMAL
                && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location)
                && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location)
                && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 135_000L;
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "You have gambled with the Lucky Mode!";
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !this.isSimilar(event.getPlayer().getItemInHand())) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You may not use " + this.getDisplayName() + ChatColor.RED
                    + " whilst your " + ChatColor.GREEN + ChatColor.BOLD + "PvP Timer " + ChatColor.RED + "is active!");
            event.setCancelled(true);
            return;
        }

        if (this.hasCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        if (!this.isAllowedAtLocation(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " here.");
            event.setCancelled(true);
            return;
        }

        this.applyCooldown(player);
        this.takeItem(player);
        event.setCancelled(true);

        if (random.nextBoolean()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 8, 1), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 3), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 2), true);

            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            player.sendMessage(ChatColor.GREEN + "You felt lucky! Positive effects applied for 8 seconds.");
            player.sendMessage("");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 8, 0), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 8, 0), true);

            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            player.sendMessage(ChatColor.RED + "Bad luck! Negative effects applied for 8 seconds.");
            player.sendMessage("");
        }
    }
}
