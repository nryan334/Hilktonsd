package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Samurai extends Ability {

    private final Map<UUID, HitRecord> lastDamager = new HashMap<>();

    private static class HitRecord {
        UUID damager;
        long timestamp;

        HitRecord(UUID damager, long timestamp) {
            this.damager = damager;
            this.timestamp = timestamp;
        }
    }

    public Samurai() {
        this.hassanStack = ItemBuilder.of(this.getMaterial()).name(this.getDisplayName()).setLore(this.getLore())
                .build();
    }

    @Override
    public String getName() {
        return "Samurai";
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Samurai";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fTeleport to the last person that"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fhit you within the last &e&l30 seconds&f!"));
        lore.add(ChatColor.translateAlternateColorCodes('&',
                "&6❙ &fYou also be given &c&lStrength II &ffor &e&l10 seconds&f!"));
        lore.add(ChatColor.translateAlternateColorCodes('&',
                "&6❙ &fTarget will receive &c&lAnti-Blockup &fand &d&lAnti-Pearl &feffects!"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &e&l5 minutes"));
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
        return 300_000L;
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "You have unleashed your inner Samurai!";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();
            lastDamager.put(victim.getUniqueId(), new HitRecord(attacker.getUniqueId(), System.currentTimeMillis()));
        }
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

        HitRecord record = lastDamager.get(player.getUniqueId());
        if (record == null || System.currentTimeMillis() - record.timestamp > 30_000L) { // Limit to 30s
            player.sendMessage(ChatColor.RED + "You have not been hit by a player within the last 30 seconds.");
            event.setCancelled(true);
            return;
        }

        Player target = Foxtrot.getInstance().getServer().getPlayer(record.damager);
        if (target == null || !target.isOnline() || target.isDead()) {
            player.sendMessage(ChatColor.RED + "Your attacker is no longer online or alive.");
            event.setCancelled(true);
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, target, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        if (!this.isAllowedAtLocation(player.getLocation()) || !this.isAllowedAtLocation(target.getLocation())) {
            player.sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED
                    + " here or your target is in a safe zone.");
            event.setCancelled(true);
            return;
        }

        this.applyCooldown(player);
        this.takeItem(player);
        event.setCancelled(true);

        this.fullDescription = "Teleporting to &f" + target.getName() + " &cin 3 seconds...";

        new org.bukkit.scheduler.BukkitRunnable() {
            private int seconds = 3;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                Player currentTarget = Foxtrot.getInstance().getServer().getPlayer(record.damager);

                if (currentTarget == null || !currentTarget.isOnline()) {
                    player.sendMessage(ChatColor.RED + "Your target went offline.");
                    this.cancel();
                    return;
                }

                if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(currentTarget)) {
                    player.sendMessage(ChatColor.RED + "Cancelled the process as that player is in the Deathban Arena!");
                    this.cancel();
                    return;
                }

                if (this.seconds < 1) {
                    player.teleport(currentTarget.getLocation());

                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 1), true);

                    AntiBlockup.getCache().put(currentTarget.getUniqueId(), System.currentTimeMillis() + 15_000L);
                    currentTarget.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                    currentTarget.sendMessage(ChatColor.RED + "You may not place/break/interact with blocks for the next 15 seconds!");
                    EnderpearlCooldownHandler.resetEnderpearlTimer(currentTarget);

                    player.sendMessage(ChatColor.DARK_RED + "You used your Samurai ability on " + currentTarget.getName() + "!");
                    currentTarget.sendMessage(ChatColor.DARK_RED + player.getName() + " used a Samurai ability on you!");

                    this.cancel();
                    return;
                }

                if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
                    this.cancel();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou may not use &f" + getDisplayName() + " &cwhile deathbanned!"));
                    return;
                }

                this.seconds--;

                player.sendMessage(ChatColor.YELLOW + "Teleporting to " + ChatColor.WHITE + currentTarget.getName() + ChatColor.YELLOW + " in " + ChatColor.RED + (this.seconds+1) + ChatColor.YELLOW + " second" + (this.seconds == 1 ? "":"s") + "...");
                
                currentTarget.sendMessage(cc.fyre.neutron.Neutron.getInstance().getProfileHandler().findDisplayName(player.getUniqueId()) + ChatColor.RED + " will teleport to you in " + ChatColor.WHITE + (this.seconds+1) + ChatColor.RED + " second" + (this.seconds == 1 ? "":"s") + ".");
            }
        }.runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
    }
}
