package net.frozenorb.foxtrot.commands.troll;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TrollListeners implements Listener {

    public static final Set<UUID> noDamage = new HashSet<>();
    public static final Set<UUID> spinning = new HashSet<>();

    public TrollListeners() {
        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            for (UUID uuid : spinning) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    Location loc = player.getLocation();
                    loc.setYaw(loc.getYaw() + 25.0f);
                    loc.setPitch(loc.getPitch() + 10.0f);
                    player.teleport(loc);
                }
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (noDamage.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            if (event.getEntity().hasMetadata("fake_tnt")) {
                event.blockList().clear();
                event.setYield(0f);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            if (event.getDamager().hasMetadata("fake_tnt")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerAnimation(org.bukkit.event.player.PlayerAnimationEvent event) {
        if (event.getAnimationType() == org.bukkit.event.player.PlayerAnimationType.ARM_SWING) {
            Player player = event.getPlayer();
            if (player.isSneaking() && spinning.contains(player.getUniqueId())) {
                spinning.remove(player.getUniqueId());
                player.sendMessage(net.frozenorb.foxtrot.util.CC.translate("&aYou stopped spinning"));
            }
        }
    }
}
