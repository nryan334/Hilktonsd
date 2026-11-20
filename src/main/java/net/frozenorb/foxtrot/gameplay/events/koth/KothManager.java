package net.frozenorb.foxtrot.gameplay.events.koth;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Cuboid;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class KothManager {

    private final Map<String, Koth> koths;
    private final File dataFile;

    public KothManager() {
        this.koths = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.dataFile = new File(Foxtrot.getInstance().getDataFolder(), "koths.json");

        this.load();
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new KothListener(this),
                Foxtrot.getInstance());
    }

    public void disable() {
        for (Koth koth : koths.values()) {
            koth.save();
        }
        save();
    }

    private void load() {
        if (!dataFile.exists())
            return;

        try {
            Map<String, Object> data = Foxtrot.GSON.fromJson(
                    new java.io.FileReader(dataFile),
                    new com.google.gson.reflect.TypeToken<Map<String, Object>>() {
                    }.getType());

            if (data == null)
                return;

            for (Object obj : data.values()) {
                Map<String, Object> map = (Map<String, Object>) obj;
                Koth koth = new Koth(this, map);
                koths.put(koth.getName(), koth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Map<String, Object> data = Maps.newHashMap();
            for (Koth koth : koths.values()) {
                data.put(koth.getName(), koth.serialize());
            }
            java.io.FileWriter writer = new java.io.FileWriter(dataFile);
            Foxtrot.GSON.toJson(data, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Koth getKoth(String name) {
        return koths.get(name);
    }

    public boolean isAntiCleanRadius(Location location) {
        for (Koth koth : getActiveKoths()) {
            if (koth.getCaptureZone() != null) {
                if (koth.getCaptureZone().outset(Cuboid.CuboidDirection.Both, 5).contains(location)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Koth getZone(Location location) {
        for (Koth koth : getActiveKoths()) {
            if (koth.getCaptureZone() != null && koth.getCaptureZone().contains(location)) {
                return koth;
            }
        }
        return null;
    }

    public List<Koth> getActiveKoths() {
        List<Koth> active = new ArrayList<>();
        for (Koth koth : koths.values()) {
            if (koth.isActive())
                active.add(koth);
        }
        return active;
    }
}
