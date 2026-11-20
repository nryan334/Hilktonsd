package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;


/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class AntiTrapperItem extends Ability {

    private final Map<Location, TrapperData> activeTrappers = new HashMap<>();
    private final Map<ObjectId, Long> affectedTeams = new HashMap<>();

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getName() {
        return "AntiTrapper";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Anti-Trapper";
    }

    @Override
    public Material getMaterial() {
        return Material.SPONGE;
    }

    @Override
    public String getDescription() {
        return "Disables enemy interactions in their claim!";
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fPlace in an enemy claim to disable"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &ftheir interactions for &c&l15s&f."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fRequires &c&l6 hits &f to break."));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6❙ &fCooldown: &c2m30s"));
        return lore;
    }

    @Override
    public long getCooldown() {
        return 150_000L;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();

        if (!isSimilar(item)) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        Team targetTeam = LandBoard.getInstance().getTeam(loc);
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (targetTeam == null || targetTeam.hasDTRBitmask(DTRBitmask.SAFE_ZONE) || (playerTeam != null && playerTeam.equals(targetTeam))) {
            player.sendMessage(ChatColor.RED + "You must place this in an enemy claim!");
            event.setCancelled(true);
            return;
        }

        AbilityUseEvent useEvent = new AbilityUseEvent(player, null, loc, this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        TrapperData data = new TrapperData(targetTeam.getUniqueId(), System.currentTimeMillis() + 15_000L);
        activeTrappers.put(loc, data);
        affectedTeams.put(targetTeam.getUniqueId(), data.getExpiry());

        targetTeam.sendMessage("");
        targetTeam.sendMessage(
                ChatColor.RED + ChatColor.BOLD.toString() + "AN ANTI-TRAPPER HAS BEEN PLACED IN YOUR CLAIM!");
        targetTeam.sendMessage(ChatColor.RED + "Interactions are disabled for 15 seconds unless you break it!");
        targetTeam.sendMessage("");

        player.sendMessage(ChatColor.GREEN + "Anti-Trapper placed successfully!");

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (activeTrappers.containsKey(loc)) {
                activeTrappers.remove(loc);
                checkAffectedTeams();
                if (loc.getBlock().getType() == getMaterial()) {
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }, 15 * 20L);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null && affectedTeams.containsKey(team.getUniqueId())
                && affectedTeams.get(team.getUniqueId()) > System.currentTimeMillis()) {
            Team locTeam = LandBoard.getInstance().getTeam(player.getLocation());
            if (team.equals(locTeam)) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
                    player.sendMessage(ChatColor.RED + "You cannot interact while an Anti-Trapper is active!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null && affectedTeams.containsKey(team.getUniqueId())
                && affectedTeams.get(team.getUniqueId()) > System.currentTimeMillis()) {
            Team locTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
            if (team.equals(locTeam)) {
                player.sendMessage(ChatColor.RED + "You cannot place blocks while an Anti-Trapper is active!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(BlockDamageEvent event) {
        Location loc = event.getBlock().getLocation();
        if (activeTrappers.containsKey(loc)) {
            TrapperData data = activeTrappers.get(loc);
            data.setHits(data.getHits() + 1);

            if (data.getHits() >= 6) {
                event.getBlock().setType(Material.AIR);
                activeTrappers.remove(loc);
                checkAffectedTeams();

                Team team = Foxtrot.getInstance().getTeamHandler().getTeam(data.getTeamId());
                if (team != null) {
                    team.sendMessage(ChatColor.GREEN + "The Anti-Trapper has been broken!");
                }
            } else {
                event.getPlayer()
                        .sendMessage(ChatColor.YELLOW + "Anti-Trapper hits: " + ChatColor.RED + data.getHits() + "/6");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (activeTrappers.containsKey(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    private void checkAffectedTeams() {
        affectedTeams.clear();
        for (TrapperData data : activeTrappers.values()) {
            affectedTeams.put(data.getTeamId(), data.getExpiry());
        }
    }

    private static class TrapperData {
        private final ObjectId teamId;
        private final long expiry;
        private int hits = 0;

        public TrapperData(ObjectId teamId, long expiry) {
            this.teamId = teamId;
            this.expiry = expiry;
        }

        public ObjectId getTeamId() {
            return teamId;
        }

        public long getExpiry() {
            return expiry;
        }

        public int getHits() {
            return hits;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }
    }
}
