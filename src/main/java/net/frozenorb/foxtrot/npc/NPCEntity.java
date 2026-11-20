package net.frozenorb.foxtrot.npc;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCEntity {
    @Getter
    private final NPC npc;
    private final EntityPlayer dummy;
    @Getter
    private final java.util.Set<UUID> renderedPlayers = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public NPCEntity(NPC npc) {
        this.npc = npc;
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = server.getWorldServer(0);

        StringBuilder randomName = new StringBuilder();
        for (char c : UUID.randomUUID().toString().replace("-", "").substring(0, 8).toCharArray()) {
            randomName.append(org.bukkit.ChatColor.COLOR_CHAR).append(c);
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), randomName.toString());

        if (npc.getSkinValue() != null && !npc.getSkinValue().isEmpty()) {
            profile.getProperties().put("textures",
                    new Property("textures", npc.getSkinValue(), npc.getSkinSignature()));
        }

        this.dummy = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
        this.updateLocation();
    }

    public void updateLocation() {
        Location loc = npc.getLocation();
        this.dummy.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.dummy.aP = (float) loc.getYaw();
    }

    public int getEntityId() {
        return this.dummy.getId();
    }

    public void spawn(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(dummy));

        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(dummy));

        if (npc.getHandItem() != null) {
            connection.sendPacket(
                    new PacketPlayOutEntityEquipment(getEntityId(), 0, CraftItemStack.asNMSCopy(npc.getHandItem())));
        }
        for (int i = 0; i < 4; i++) {
            if (npc.getArmor()[i] != null) {
                connection.sendPacket(new PacketPlayOutEntityEquipment(getEntityId(), i + 1,
                        CraftItemStack.asNMSCopy(npc.getArmor()[i])));
            }
        }

        float spawnYaw = npc.getLocation().getYaw();
        float spawnPitch = npc.getLocation().getPitch();
        connection.sendPacket(new PacketPlayOutEntityLook(getEntityId(),
                (byte) (spawnYaw * 256.0F / 360.0F),
                (byte) (spawnPitch * 256.0F / 360.0F),
                true));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(dummy, (byte) (spawnYaw * 256.0F / 360.0F)));

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (player.isOnline()) {
                connection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(dummy));
            }
        }, 40L);
    }

    public void destroy(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(getEntityId()));
    }

    public void updateRotation(Player player, float yaw, float pitch) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutEntityLook(getEntityId(),
                (byte) (yaw * 256.0F / 360.0F),
                (byte) (pitch * 256.0F / 360.0F),
                true));

        connection.sendPacket(new PacketPlayOutEntityHeadRotation(dummy, (byte) (yaw * 256.0F / 360.0F)));
    }
}
