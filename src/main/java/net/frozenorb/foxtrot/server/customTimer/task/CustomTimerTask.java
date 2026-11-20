package net.frozenorb.foxtrot.server.customTimer.task;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CustomTimerTask extends BukkitRunnable {
    private List<String> queue = new ArrayList<>();

    @Override
    public void run() {
        if (CustomTimerCreateCommand.isSOTWTimer()) {
            return;
        }

        for (String timer : CustomTimerCreateCommand.getCustomTimers().keySet()) {
            if (timer.equalsIgnoreCase("&a&lSOTW") || timer.equalsIgnoreCase("&d&l2x Points")) {
                continue;
            }

            if (queue.contains(timer)) {
                continue;
            }

            if (CustomTimerCreateCommand.getCustomTimers().get(timer) < System.currentTimeMillis()) {
                continue;
            }

            queue.add(timer);
        }

        queue.removeIf(timer -> CustomTimerCreateCommand.getCustomTimers().containsKey(timer) && CustomTimerCreateCommand.getCustomTimers().get(timer) <= System.currentTimeMillis());

        CustomTimerCreateCommand.getCustomTimers().keySet().stream().filter(it -> !queue.contains(it) && !it.equalsIgnoreCase("&a&lSOTW") && !it.equalsIgnoreCase("&d&l2x Points")).forEach(it -> queue.add(it));
        queue.removeIf(it -> !CustomTimerCreateCommand.getCustomTimers().containsKey(it));

        if (queue.isEmpty()) {
            return;
        }

        String chosenTimer = queue.remove(0);

        Foxtrot.getInstance().getCustomTimerHandler().setActiveTimer(chosenTimer);
    }
}
