package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;


/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

@Getter
@Setter
@AllArgsConstructor
public class CrateBlock {
    private final String crateId;
    private final Location location;
    private Hologram hologram;

    public void spawnHologram() {
        if (location == null || location.getWorld() == null) {
            return;
        }

        if (hologram != null) {
            hologram.delete();
            hologram = null;
        }

        Crate crate = Foxtrot.getInstance().getCrateHandler().findById(crateId).orElse(null);
        if (crate == null)
            return;

        List<String> lines = crate.getHologramLines();

        double yOffset = 2.0 + (lines.size() * 0.25);
        Location spawnLoc = location.clone().add(0.5, yOffset, 0.5);

        cc.fyre.proton.hologram.builder.HologramBuilder builder = Proton.getInstance().getHologramHandler()
                .createHologram();
        String[] linesArray = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            linesArray[i] = ChatColor.translateAlternateColorCodes('&', lines.get(i));
        }
        builder.addLines(linesArray);
        builder.at(spawnLoc);

        hologram = builder.build();
        hologram.send();
    }

    public void removeHologram() {
        if (hologram != null) {
            hologram.delete();
            hologram = null;
        }
    }
}
