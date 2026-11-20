package net.frozenorb.foxtrot.gameplay.events.koth.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.koth.Koth;
import net.frozenorb.foxtrot.gameplay.events.koth.KothManager;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class KothCommand {

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                Player player = (Player) event.getPlayer();
                if (player.hasMetadata("editing_koth_loot")) {
                    String kothName = player.getMetadata("editing_koth_loot").get(0).asString();
                    KothManager manager = Foxtrot.getInstance().getKothManager();
                    Koth koth = manager.getKoth(kothName);

                    if (koth != null) {
                        koth.getLoot().clear();
                        for (ItemStack stack : event.getInventory().getContents()) {
                            if (stack != null) {
                                koth.getLoot().add(stack.clone());
                            }
                        }
                        koth.save();
                        player.sendMessage(CC.translate("&aLoot saved for " + koth.getName() + " KOTH."));
                    }

                    player.removeMetadata("editing_koth_loot", Foxtrot.getInstance());
                }
            }
        }, Foxtrot.getInstance());
    }

    @Command(names = { "koth create" }, permission = "hilton.koth.create")
    public static void create(Player sender, @Parameter(name = "name") String name,
            @Parameter(name = "minutes") int minutes) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        if (manager.getKoths().containsKey(name)) {
            sender.sendMessage(CC.translate("&cKoth already exists."));
            return;
        }

        Koth koth = new Koth(manager, name, "&9", 250, minutes);
        koth.save();
        sender.sendMessage(CC.translate("&aKoth created!"));
    }

    @Command(names = { "koth delete" }, permission = "hilton.koth.delete")
    public static void delete(Player sender, @Parameter(name = "name") String name) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        koth.delete();
        sender.sendMessage(CC.translate("&cKoth deleted!"));
    }

    @Command(names = { "koth start" }, permission = "hilton.koth.start")
    public static void start(Player sender, @Parameter(name = "name") String name) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        koth.start();
        sender.sendMessage(CC.translate("&aKoth started!"));
    }

    @Command(names = { "koth stop" }, permission = "hilton.koth.stop")
    public static void stop(Player sender, @Parameter(name = "name") String name) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        koth.end();
        koth.save();
        sender.sendMessage(CC.translate("&cKoth stopped!"));
    }

    @Command(names = { "koth setloc" }, permission = "hilton.koth.setloc")
    public static void setLoc(Player sender, @Parameter(name = "name") String name) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        Location loc = sender.getLocation();
        Location loc1 = loc.clone().add(2, 5, 2);
        Location loc2 = loc.clone().subtract(2, 5, 2);

        koth.setCaptureZone(new Cuboid(loc1, loc2));
        koth.save();
        sender.sendMessage(CC.translate("&aKoth capture zone set around you."));
    }

    @Command(names = { "koth setpoints" }, permission = "hilton.koth.setpoints")
    public static void setPoints(Player sender, @Parameter(name = "name") String name, @Parameter(name = "points") int points) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        if (points < 0) {
            sender.sendMessage(CC.translate("&cPoints cannot be negative."));
            return;
        }

        koth.setPointsReward(points);
        koth.save();
        sender.sendMessage(CC.translate("&aUpdated points reward for &b" + koth.getName() + " &ato &e" + points + " &apoints."));
    }

    @Command(names = { "koth settime" }, permission = "hilton.koth.settime")
    public static void setTime(Player sender, @Parameter(name = "name") String name,
            @Parameter(name = "minutes") int minutes) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        koth.setMinutes((60 * 1000L) * minutes);
        koth.setRemaining(koth.getMinutes());
        koth.save();
        sender.sendMessage(CC.translate("&aKoth time updated!"));
    }

    @Command(names = { "koth tp" }, permission = "hilton.koth.tp")
    public static void tp(Player sender, @Parameter(name = "name") String name) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        if (koth.getCaptureZone() != null) {
            sender.teleport(koth.getCaptureZone().getCenter());
            sender.sendMessage(CC.translate("&aTeleported to &b" + koth.getName() + " &aKOTH."));
        } else {
            sender.sendMessage(CC.translate("&cThat Koth doesn't have a capture zone set."));
        }
    }

    @Command(names = { "koth claimzone" }, permission = "hilton.koth.claimzone")
    public static void claimzone(Player sender, @Parameter(name = "name") String name) {
        setLoc(sender, name);
    }

    @Command(names = { "koth list", "koth" }, permission = "")
    public static void list(Player sender) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        if (manager.getActiveKoths().isEmpty()) {
            sender.sendMessage(CC.translate("&cThere are no active KOTHs."));
        } else {
            sender.sendMessage(CC.translate("&7&m------------------------"));
            sender.sendMessage(CC.translate("&9&lActive KOTHs:"));
            for (Koth koth : manager.getActiveKoths()) {
                Location loc = koth.getCaptureZone() != null ? koth.getCaptureZone().getCenter() : null;
                String locString = loc != null ? (loc.getBlockX() + ", " + loc.getBlockZ()) : "Not set";
                sender.sendMessage(CC.translate("&7* &b" + koth.getName() + " &7- &f" +
                        cc.fyre.proton.util.TimeUtils.formatIntoMMSS((int) (koth.getRemaining() / 1000L)) +
                        " &7- &f" + locString));
            }
            sender.sendMessage(CC.translate("&7&m------------------------"));
        }
    }

    @Command(names = { "koth schedule", "koth next", "next koth", "koth next" }, permission = "")
    public static void schedule(Player sender) {
        java.util.Map<net.frozenorb.foxtrot.gameplay.events.EventScheduledTime, String> schedule = Foxtrot.getInstance()
                .getEventHandler().getEventSchedule();
        if (schedule.isEmpty()) {
            sender.sendMessage(CC.translate("&cNo scheduled KOTHs found."));
            return;
        }

        sender.sendMessage(CC.translate("&7&m------------------------"));
        sender.sendMessage(CC.translate("&9&lUpcoming KOTHs:"));

        java.util.Date now = new java.util.Date();
        int shown = 0;

        for (java.util.Map.Entry<net.frozenorb.foxtrot.gameplay.events.EventScheduledTime, String> entry : schedule
                .entrySet()) {
            java.util.Date date = entry.getKey().toDate();
            if (date.after(now)) {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, hh:mm a");
                sender.sendMessage(CC.translate("&7* &b" + entry.getValue() + " &7- &f" + format.format(date)));
                shown++;
                if (shown >= 5)
                    break;
            }
        }

        if (shown == 0) {
            sender.sendMessage(CC.translate("&cNo upcoming KOTHs scheduled for the future."));
        }
        sender.sendMessage(CC.translate("&7&m------------------------"));
    }

    @Command(names = { "koth schedule add" }, permission = "hilton.koth.setschedule")
    public static void scheduleAdd(Player sender, @Parameter(name = "koth") String kothName,
            @Parameter(name = "daysFromNow") int daysFromNow, @Parameter(name = "hour") int hour,
            @Parameter(name = "minute") int minute) {
        if (daysFromNow < 0) {
            sender.sendMessage(CC.translate("&cDays from now cannot be negative."));
            return;
        }
        if (hour < 0 || hour > 23) {
            sender.sendMessage(CC.translate("&cHour must be between 0 and 23."));
            return;
        }
        if (minute < 0 || minute > 59) {
            sender.sendMessage(CC.translate("&cMinute must be between 0 and 59."));
            return;
        }

        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(kothName);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_YEAR, daysFromNow);
        int dayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR);

        net.frozenorb.foxtrot.gameplay.events.EventScheduledTime scheduledTime = new net.frozenorb.foxtrot.gameplay.events.EventScheduledTime(
                dayOfYear, hour, minute);

        Foxtrot.getInstance().getEventHandler().getEventSchedule().put(scheduledTime, koth.getName());
        Foxtrot.getInstance().getEventHandler().saveSchedules();

        sender.sendMessage(CC.translate("&aSuccessfully scheduled &b" + koth.getName() + " &afor &e" + daysFromNow
                + " days from now at " + hour + ":" + String.format("%02d", minute) + "&a."));
    }

    @Command(names = { "koth schedule remove" }, permission = "hilton.koth.setschedule")
    public static void scheduleRemove(Player sender, @Parameter(name = "daysFromNow") int daysFromNow,
            @Parameter(name = "hour") int hour, @Parameter(name = "minute") int minute) {
        if (daysFromNow < 0) {
            sender.sendMessage(CC.translate("&cDays from now cannot be negative."));
            return;
        }
        if (hour < 0 || hour > 23) {
            sender.sendMessage(CC.translate("&cHour must be between 0 and 23."));
            return;
        }
        if (minute < 0 || minute > 59) {
            sender.sendMessage(CC.translate("&cMinute must be between 0 and 59."));
            return;
        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_YEAR, daysFromNow);
        int dayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR);

        net.frozenorb.foxtrot.gameplay.events.EventScheduledTime scheduledTime = new net.frozenorb.foxtrot.gameplay.events.EventScheduledTime(
                dayOfYear, hour, minute);

        String removed = Foxtrot.getInstance().getEventHandler().getEventSchedule().remove(scheduledTime);
        if (removed != null) {
            Foxtrot.getInstance().getEventHandler().saveSchedules();
            sender.sendMessage(CC.translate("&cRemoved &b" + removed + " &cfrom the schedule."));
        } else {
            sender.sendMessage(CC.translate("&cThere was no KOTH scheduled at that time."));
        }
    }

    @Command(names = { "koth editor", "event editor" }, permission = "hilton.koth.editor")
    public static void editor(Player sender, @Parameter(name = "name") String name) {
        KothManager manager = Foxtrot.getInstance().getKothManager();
        Koth koth = manager.getKoth(name);
        if (koth == null) {
            sender.sendMessage(CC.translate("&cKoth not found."));
            return;
        }

        new net.frozenorb.foxtrot.gameplay.events.koth.menu.KothEditorMenu(koth).openMenu(sender);
    }
}
