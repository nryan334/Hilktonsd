package net.frozenorb.foxtrot.gameplay.events.koth;

import org.bukkit.scheduler.BukkitRunnable;

public class KothTickTask extends BukkitRunnable {

    private final Koth koth;

    public KothTickTask(Koth koth) {
        this.koth = koth;
        this.runTaskTimer(net.frozenorb.foxtrot.Foxtrot.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        if (!koth.isActive()) {
            this.cancel();
            koth.setBukkitTask(null);
            return;
        }

        koth.tick();
    }
}
