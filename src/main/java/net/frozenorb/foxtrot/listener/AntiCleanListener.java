package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.events.koth.Koth;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

public class AntiCleanListener implements Listener {

    public static final Map<Team, Team> CACHE = new HashMap<>();
    public static final Map<Team, Long> TIME_CACHE = new HashMap<>();

    @EventHandler
    private void onEntityDamageByEntityNormal(EntityDamageByEntityEvent event) {
        if (!DisableCommand.antiClean)
            return;
        if (Foxtrot.getInstance().getMapHandler().isKitMap())
            return;
        if (!(event.getEntity() instanceof Player))
            return;

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        Player victim = (Player) event.getEntity();

        if (damager == null)
            return;
        if (damager == victim)
            return;

        final Team victimTeam = Foxtrot.getInstance().getTeamHandler().getTeam(victim);
        final Team damagerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(damager);

        World.Environment env = victim.getWorld().getEnvironment();

        if (env == World.Environment.NORMAL) {
            for (net.frozenorb.foxtrot.gameplay.events.koth.Koth koth : Foxtrot.getInstance().getKothManager().getActiveKoths()) {
                if (koth.getCaptureZone() != null && koth.getCaptureZone().getWorld().getName().equals(victim.getWorld().getName())) {
                    double distance = victim.getLocation().distance(koth.getCaptureZone().getCenter());
                    if (distance <= 30) {
                        return;
                    }
                }
            }
        } else {
            boolean ignore = false;

            for (net.frozenorb.foxtrot.gameplay.events.koth.Koth koth : Foxtrot.getInstance().getKothManager().getActiveKoths()) {
                if (koth.getCaptureZone() != null) {
                    if ((env == World.Environment.NETHER && koth.getCaptureZone().getWorld().getName().equals("world_nether"))
                            || (env == World.Environment.THE_END && koth.getCaptureZone().getWorld().getName().equals("world_the_end"))) {
                        ignore = true;
                    }
                }
            }

            if (ignore)
                return;
        }

        Team victimOpponentTeam = victimTeam == null ? null : CACHE.get(victimTeam);
        Team damagerOpponentTeam = damagerTeam == null ? null : CACHE.get(damagerTeam);

        if (victimOpponentTeam != null && victimOpponentTeam != damagerTeam) {
            damager.sendMessage(ChatColor
                    .translate("&cYou may not hit &f" + victim.getName() + " &cas they have an &3&lAnti-Clean &ctag!"));
            event.setCancelled(true);
        } else if (damagerOpponentTeam != null && damagerOpponentTeam != victimTeam) {
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou may not hit &f" + victim.getName()
                    + " &cas you have an &3&lAnti-Clean &ctag and they aren't the faction you were fighting!"));
            event.setCancelled(true);
        }

        damager.setMetadata("anti_clean", new FixedMetadataValue(Foxtrot.getInstance(), true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        if (damager == null)
            return;

        if (!damager.hasMetadata("anti_clean"))
            return;

        damager.removeMetadata("anti_clean", Foxtrot.getInstance());

        Player victim = (Player) event.getEntity();

        final Team victimTeam = Foxtrot.getInstance().getTeamHandler().getTeam(victim);
        final Team damagerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(damager);

        if (victimTeam == null)
            return;
        if (damagerTeam == null)
            return;

        if (4 > victimTeam.getOnlineMembers().size())
            return;
        if (4 > damagerTeam.getOnlineMembers().size())
            return;

        if (!CACHE.containsKey(damagerTeam)) {
            damagerTeam.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lAnti-Clean &ctag has been activated because &f"
                    + damager.getName() + " &chit &f[" + victimTeam.getName() + "] " + victim.getName() + "&c!"));
        }

        if (!CACHE.containsKey(victimTeam)) {
            victimTeam.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&3&lAnti-Clean &ctag has been activated because &f" + victim.getName()
                            + " &cwas hit by &f[" + damagerTeam.getName() + "] " + damager.getName() + "&c!"));
        }

        CACHE.put(damagerTeam, victimTeam);
        CACHE.put(victimTeam, damagerTeam);

        TIME_CACHE.put(damagerTeam, System.currentTimeMillis() + 60000);
        TIME_CACHE.put(victimTeam, System.currentTimeMillis() + 60000);
    }
}
