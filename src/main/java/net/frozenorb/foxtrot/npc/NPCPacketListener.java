package net.frozenorb.foxtrot.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NPCPacketListener extends PacketAdapter {

    private final NPCHandler handler;
    private final Map<UUID, Long> interactionCache = new HashMap<>();
    private final long interactionCooldown = TimeUnit.MILLISECONDS.toMillis(500L);

    public NPCPacketListener(NPCHandler handler) {
        super(Foxtrot.getInstance(), PacketType.Play.Client.USE_ENTITY);
        this.handler = handler;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.USE_ENTITY) {
            return;
        }

        int entityId = event.getPacket().getIntegers().read(0);
        NPC npc = handler.getNPCByEntityId(entityId);

        if (npc == null) {
            return;
        }

        if (npc.getLocation().distanceSquared(event.getPlayer().getLocation()) > 36.0) {
            return;
        }
        UUID playerUuid = event.getPlayer().getUniqueId();
        if (interactionCache.containsKey(playerUuid) &&
                (System.currentTimeMillis() - interactionCache.get(playerUuid) <= interactionCooldown)) {
            return;
        }

        interactionCache.put(playerUuid, System.currentTimeMillis());

        EnumWrappers.EntityUseAction action;
        try {
            action = event.getPacket().getEntityUseActions().read(0);
        } catch (Exception e) {
            action = EnumWrappers.EntityUseAction.INTERACT;
        }
        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> handler.handleInteraction(event.getPlayer(), npc));
    }
}
