package net.frozenorb.foxtrot.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;

public class CustomPayloadPacketAdapter extends PacketAdapter {

    public CustomPayloadPacketAdapter() {
        super(Foxtrot.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        try {
            String channel = event.getPacket().getStrings().read(0);
            byte[] data = null;

            try {
                data = event.getPacket().getByteArrays().read(0);
            } catch (Exception ignored) {
            }

            if (data == null) {
                try {
                    Object nmsPacket = event.getPacket().getHandle();
                    java.lang.reflect.Field dataField = nmsPacket.getClass().getDeclaredField("data");
                    dataField.setAccessible(true);
                    data = (byte[]) dataField.get(nmsPacket);
                } catch (Exception ignored) {
                }
            }

            if (channel != null && channel.equals("MC|Brand") && data == null) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            event.setCancelled(true);
        }
    }
}
