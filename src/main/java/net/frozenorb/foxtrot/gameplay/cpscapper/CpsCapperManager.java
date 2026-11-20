package net.frozenorb.foxtrot.gameplay.cpscapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class CpsCapperManager implements Listener {

    private final Foxtrot instance;
    private final Map<UUID, Deque<Long>> clicks = new HashMap<>();
    private final Map<UUID, Long> flagged = new HashMap<>();
    private final Map<UUID, Long> lastMessage = new HashMap<>();
    private final int maxCps;
    private final double reduction;

    public CpsCapperManager(Foxtrot instance) {
        this.instance = instance;
        this.maxCps = instance.getConfig().getInt("CPS_CAPPER.MAX_CPS", 17);
        this.reduction = instance.getConfig().getDouble("CPS_CAPPER.DAMAGE_REDUCTION", 50.0D) / 100.0D;

        Bukkit.getPluginManager().registerEvents(this, instance);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(instance,
                PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleAttack(event.getPlayer());
            }
        });
    }

    public void handleAttack(Player player) {
        if (maxCps <= 0)
            return;
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        Deque<Long> deque = clicks.computeIfAbsent(uuid, k -> new ArrayDeque<>());
        deque.addLast(now);
        while (!deque.isEmpty() && now - deque.peekFirst() > 1000L) {
            deque.removeFirst();
        }
        if (deque.size() > maxCps) {
            flagged.put(uuid, now + 1000L);
        }
    }

    public boolean isFlagged(Player player) {
        Long time = flagged.get(player.getUniqueId());
        return time != null && time > System.currentTimeMillis();
    }

    public double getReduction() {
        return reduction;
    }

    public void clear(Player player) {
        UUID uuid = player.getUniqueId();
        clicks.remove(uuid);
        flagged.remove(uuid);
        lastMessage.remove(uuid);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clear(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getDamager();
        if (isFlagged(player)) {
            event.setDamage(event.getDamage() * getReduction());
            
            long now = System.currentTimeMillis();
            if (now - lastMessage.getOrDefault(player.getUniqueId(), 0L) > 2000L) {
                player.sendMessage(org.bukkit.ChatColor.RED + "Hey, you are clicking over " + maxCps + " CPS! Your hits have been reduced.");
                lastMessage.put(player.getUniqueId(), now);
            }
        }
    }
}
