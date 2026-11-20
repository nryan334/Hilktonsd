package net.frozenorb.foxtrot.gameplay.events.purge;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;

public class PurgeManager {

    public static boolean isPurgeActive() {
        return CustomTimerCreateCommand.getCustomTimers().containsKey("&4&lPurge");
    }

    public static void startPurge(long time) {
        CustomTimerCreateCommand.getCustomTimers().put("&4&lPurge", System.currentTimeMillis() + time);
    }
    
    public static void stopPurge() {
        CustomTimerCreateCommand.getCustomTimers().remove("&4&lPurge");
    }
}
