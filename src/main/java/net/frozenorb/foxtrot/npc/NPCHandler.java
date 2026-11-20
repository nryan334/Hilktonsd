package net.frozenorb.foxtrot.npc;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import cc.fyre.proton.hologram.type.BaseHologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NPCHandler implements Listener {
    @Getter
    private final Map<String, NPC> npcs = new ConcurrentHashMap<>();
    private final Map<String, NPCEntity> renderers = new ConcurrentHashMap<>();
    private final Map<String, Hologram> holograms = new ConcurrentHashMap<>();
    private final DBCollection collection;

    public NPCHandler() {
        this.collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("NPCs");
        loadNPCs();

        new NPCPacketListener(this);
        Proton.getInstance().getCommandHandler().registerParameterType(NPC.class, new NPCParameterType());
        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());

        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), this::tick, 2L, 2L);
    }

    private void loadNPCs() {
        try (DBCursor cursor = collection.find()) {
            while (cursor.hasNext()) {
                BasicDBObject dbObject = (BasicDBObject) cursor.next();
                NPC npc = NPC.fromDBObject(dbObject);
                npcs.put(npc.getId(), npc);
                renderers.put(npc.getId(), new NPCEntity(npc));
                updateHolograms(npc);
            }
        }
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.GREEN + "[NPCHandler] Loaded " + npcs.size() + " NPCs from MongoDB.");
    }

    public void saveNPCs() {
        for (NPC npc : npcs.values()) {
            updateNPC(npc);
        }
    }

    public void updateNPC(NPC npc) {
        collection.update(new BasicDBObject("id", npc.getId()), npc.toDBObject(), true, false);
    }

    public void respawnNPC(NPC npc) {
        NPCEntity oldRenderer = renderers.get(npc.getId());
        if (oldRenderer != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                oldRenderer.destroy(player);
            }
        }

        NPCEntity newRenderer = new NPCEntity(npc);
        renderers.put(npc.getId(), newRenderer);

        for (Player player : Bukkit.getOnlinePlayers()) {
            newRenderer.spawn(player);
        }
        updateHolograms(npc);
        updateNPC(npc);
    }

    public void createNPC(String id, Location location) {
        NPC npc = new NPC(id, location);
        npcs.put(id, npc);
        renderers.put(id, new NPCEntity(npc));

        for (Player player : Bukkit.getOnlinePlayers()) {
            renderers.get(id).spawn(player);
        }

        updateHolograms(npc);
    }

    public void removeNPC(String id) {
        if (npcs.containsKey(id)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                renderers.get(id).destroy(player);
            }

            Hologram hologram = holograms.remove(id);
            if (hologram != null) {
                hologram.delete();
            }

            npcs.remove(id);
            renderers.remove(id);
            collection.remove(new BasicDBObject("id", id));
        }
    }

    public void updateHolograms(NPC npc) {
        Hologram hologram = holograms.get(npc.getId());
        double yOffset = npc.getHologramOffset() + (npc.getHologramLines().size() * 0.25);
        Location loc = npc.getLocation().clone().add(0, yOffset, 0);

        if (hologram == null) {
            if (!npc.getHologramLines().isEmpty()) {
                hologram = Proton.getInstance().getHologramHandler().createHologram()
                        .at(loc)
                        .addLines(npc.getHologramLines())
                        .build();
                if (hologram instanceof BaseHologram) {
                    ((BaseHologram) hologram).setPersistent(false);
                }
                hologram.send();
                holograms.put(npc.getId(), hologram);
            }
        } else {
            if (npc.getHologramLines().isEmpty()) {
                hologram.delete();
                holograms.remove(npc.getId());
            } else {
                hologram.move(loc);
                hologram.setLines(npc.getHologramLines());
            }
        }
    }

    public NPC getNPCByEntityId(int entityId) {
        for (NPCEntity renderer : renderers.values()) {
            if (renderer.getEntityId() == entityId) {
                return renderer.getNpc();
            }
        }
        return null;
    }

    public void handleInteraction(Player player, NPC npc) {
        for (String command : npc.getCommands()) {
            String processedCommand = command.replace("{player}", player.getName());
            if (npc.isExecuteAsPlayer()) {
                player.performCommand(
                        processedCommand.startsWith("/") ? processedCommand.substring(1) : processedCommand);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            }
        }
    }

    private void tick() {
        for (NPCEntity renderer : renderers.values()) {
            NPC npc = renderer.getNpc();
            Location npcLoc = npc.getLocation();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Location playerLoc = player.getLocation();

                if (!npcLoc.getWorld().equals(playerLoc.getWorld())) {
                    if (renderer.getRenderedPlayers().contains(player.getUniqueId())) {
                        renderer.destroy(player);
                        renderer.getRenderedPlayers().remove(player.getUniqueId());
                    }
                    continue;
                }

                double distSq = npcLoc.distanceSquared(playerLoc);

                if (distSq < 48 * 48) {
                    if (!renderer.getRenderedPlayers().contains(player.getUniqueId())) {
                        renderer.spawn(player);
                        renderer.getRenderedPlayers().add(player.getUniqueId());
                    }
                } else if (distSq > 56 * 56) {
                    if (renderer.getRenderedPlayers().contains(player.getUniqueId())) {
                        renderer.destroy(player);
                        renderer.getRenderedPlayers().remove(player.getUniqueId());
                    }
                }

                if (npc.isLookAtPlayer() && renderer.getRenderedPlayers().contains(player.getUniqueId())) {
                    if (distSq < 64) {
                        Vector direction = playerLoc.toVector().add(new Vector(0, 1.5, 0))
                                .subtract(npcLoc.toVector().add(new Vector(0, 1.62, 0))).normalize();
                        double x = direction.getX();
                        double z = direction.getZ();

                        float yaw = (float) Math.toDegrees(Math.atan2(-x, z));
                        float pitch = (float) Math.toDegrees(Math.asin(-direction.getY()));

                        renderer.updateRotation(player, yaw, pitch);
                    } else {
                        renderer.updateRotation(player, npcLoc.getYaw(), npcLoc.getPitch());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (NPCEntity renderer : renderers.values()) {
            renderer.getRenderedPlayers().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (NPCEntity renderer : renderers.values()) {
            renderer.getRenderedPlayers().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        for (NPCEntity renderer : renderers.values()) {
            renderer.getRenderedPlayers().remove(event.getPlayer().getUniqueId());
        }
    }
}
