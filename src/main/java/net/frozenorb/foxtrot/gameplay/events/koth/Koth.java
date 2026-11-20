package net.frozenorb.foxtrot.gameplay.events.koth;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cuboid;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
@Setter
public class Koth {

    private KothManager manager;
    private String name;
    private String color;
    private Cuboid captureZone;

    private List<Player> onCap;
    private List<ItemStack> loot;
    private Player capturing;
    private BukkitRunnable bukkitTask;

    private boolean active;
    private int pointsReward;
    private long minutes;
    private long remaining;
    private long startTime;

    public Koth(KothManager manager, Map<String, Object> map) {
        this.manager = manager;

        this.name = (String) map.get("name");
        this.color = (String) map.get("color");
        this.pointsReward = Integer.parseInt((String) map.get("pointsReward"));
        this.active = Boolean.parseBoolean((String) map.get("active"));
        this.minutes = Long.parseLong((String) map.get("minutes"));
        this.loot = new ArrayList<>();

        this.onCap = new ArrayList<>();

        this.capturing = null;
        this.bukkitTask = null;

        this.remaining = minutes;
        this.startTime = Long.parseLong((String) map.get("startTime"));

        if (map.containsKey("captureZone")) {
            Map<String, Object> czMap = (Map<String, Object>) map.get("captureZone");
            this.captureZone = new Cuboid(czMap);
        }
    }

    public Koth(KothManager manager, String name, String color, int pointsReward, long minutes) {
        this.manager = manager;

        this.name = name;
        this.color = color;
        this.onCap = new ArrayList<>();
        this.loot = new ArrayList<>();

        this.captureZone = null;
        this.capturing = null;
        this.bukkitTask = null;

        this.pointsReward = pointsReward;
        this.active = false;

        this.minutes = (60 * 1000L) * minutes;
        this.remaining = this.minutes;
        this.startTime = 0L;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", name);
        map.put("color", color);
        map.put("pointsReward", String.valueOf(pointsReward));
        map.put("active", String.valueOf(active));
        map.put("minutes", String.valueOf(minutes));
        map.put("startTime", String.valueOf(startTime));

        if (captureZone != null) {
            map.put("captureZone", captureZone.serialize());
        }

        return map;
    }

    public void tick() {
        long capMessage = 10;
        long teamCapMessage = 60;
        long remSimplified = remaining / 1000L;

        if (capturing == null && !onCap.isEmpty()) {
            Collections.shuffle(onCap);
            capturing = onCap.get(0);
        }

        if (capturing != null) {
            Team pt = Foxtrot.getInstance().getTeamHandler().getTeam(capturing);

            if (pt != null && pt.getStrikes() >= 3) {
                capturing.sendMessage(CC.translate("&cPlayer cannot capture while faction is disqualified."));
                onCap.remove(capturing);
                capturing = null;
                remaining = minutes;

                if (bukkitTask != null) {
                    bukkitTask.cancel();
                    bukkitTask = null;
                }
                return;
            }

            if (!capturing.isOnline() || capturing.isDead() ||
                    Foxtrot.getInstance().getPvPTimerMap().hasTimer(capturing.getUniqueId()) ||
                    (captureZone != null && !captureZone.contains(capturing.getLocation()))) {

                checkLostCapMessage(capturing);
                onCap.remove(capturing);
                capturing = null;
                remaining = minutes;

                if (bukkitTask != null) {
                    bukkitTask.cancel();
                    bukkitTask = null;
                }
                return;
            }
        }

        boolean isSame = remaining == minutes;
        remaining = remaining - 1000L;

        if (remaining <= 0L) {
            this.handleCapture();
            return;
        }

        if (isSame)
            return;

        if (capturing != null && remSimplified % teamCapMessage == 0 && remSimplified > 5) {
            Team pt = Foxtrot.getInstance().getTeamHandler().getTeam(capturing);

            if (pt != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (pt.isMember(p.getUniqueId()) && p != capturing) {
                        p.sendMessage(
                                CC.translate("&6[KingOfTheHill] &eYour faction is controlling &9" + name + "&e."));
                    }
                }
                return;
            }
        }

        if (capturing != null && remSimplified % capMessage == 0 && remSimplified > 5) {
            capturing.sendMessage(CC.translate("&6[KingOfTheHill] &eAttempting to control &9" + name + "&e."));
        }
    }

    private void handleCapture() {
        if (capturing == null)
            return;

        Bukkit.getLogger().info("[KOTH] Capture completed for '" + name + "' by '" + capturing.getName()
                + "'. Issuing rewards and ending.");
        this.reward();

        Team pt = Foxtrot.getInstance().getTeamHandler().getTeam(capturing);

        String factionName = (pt == null ? "None" : pt.getName());
        String playerName = capturing.getName();

        saveLastCapture(name, factionName, playerName);

        String endMsg = CC.translate("&6[KingOfTheHill] " + color + name + " &ehas been controlled by &6"
                + (pt == null ? "" : "[" + factionName + "] ") + "&f" + playerName + "&e!");

        Bukkit.getConsoleSender().sendMessage(endMsg);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(endMsg);
        }

        this.end();
        this.save();
    }

    public void start() {
        Bukkit.getLogger().info("[KOTH] Starting KOTH '" + name + "' duration(ms)=" + minutes);
        this.capturing = null;
        this.active = true;
        this.remaining = minutes;
        this.startTime = System.currentTimeMillis();
        this.onCap.clear();
    }

    public void end() {
        Bukkit.getLogger().info("[KOTH] Ending KOTH '" + name + "'.");
        this.capturing = null;
        this.active = false;
        this.remaining = minutes;
        this.startTime = 0L;
        this.onCap.clear();
    }

    public void reward() {
        if (capturing == null)
            return;

        Team pt = Foxtrot.getInstance().getTeamHandler().getTeam(capturing);

        if (pt != null) {
            pt.setKothCaptures(pt.getKothCaptures() + 1);
            pt.setPoints(pt.getPoints() + pointsReward);
            pt.sendMessage(CC.translate(
                    "&6[KingOfTheHill] &eYour faction received &9" + pointsReward + " &epoints for capturing."));
        }

        ItemStack rewardKey = InventoryUtils.generateKOTHRewardKey(name + " KOTH");
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || name.equalsIgnoreCase("End")
                || name.equalsIgnoreCase("Hell")) {
            rewardKey.setAmount(2);
        }
        capturing.getInventory().addItem(rewardKey);
        if (!capturing.getInventory().contains(rewardKey)) {
            capturing.getWorld().dropItemNaturally(capturing.getLocation(), rewardKey);
        }

        ItemStack kothSign = Foxtrot.getInstance().getServerHandler().generateKOTHSign(name,
                pt == null ? capturing.getName() : pt.getName(), EventType.DTC); // Use DTC type just to get a generic
                                                                                 // sign
        capturing.getInventory().addItem(kothSign);
        if (!capturing.getInventory().contains(kothSign)) {
            capturing.getWorld().dropItemNaturally(capturing.getLocation(), kothSign);
        }
    }

    public void checkLostCapMessage(Player player) {
        long percent = (getRemaining() * 100) / minutes;

        if (percent <= 90) {
            Bukkit.broadcastMessage(CC.translate("&6[KingOfTheHill] &eControl of " + color + name + " &elost."));
        }
    }

    public void save() {
        manager.getKoths().put(name, this);
        manager.save();
    }

    public void delete() {
        manager.getKoths().remove(name);
        manager.save();
    }

    private static final Map<String, CaptureData> lastCaptures = new HashMap<>();

    private void saveLastCapture(String kothName, String factionName, String playerName) {
        CaptureData captureData = new CaptureData(factionName, playerName);
        lastCaptures.put(kothName, captureData);
    }

    public static class CaptureData {
        private final String factionName;
        private final String playerName;

        public CaptureData(String factionName, String playerName) {
            this.factionName = factionName;
            this.playerName = playerName;
        }

        public String getFactionName() {
            return factionName;
        }

        public String getPlayerName() {
            return playerName;
        }
    }

    public static CaptureData getLastCapture(String kothName) {
        return lastCaptures.get(kothName);
    }
}