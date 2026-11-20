package net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.LootboxItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LootboxRollInventory {

    public static final Map<UUID, LootboxRollInventory> activeRolls = new HashMap<>();

    private final Lootbox lootbox;
    private final Player player;
    private final Inventory inventory;
    private final List<LootboxItem> wonItems = new ArrayList<>();
    private final Random random = new Random();
    private boolean rollFinished = false;

    public boolean isRollFinished() {
        return rollFinished;
    }

    public LootboxRollInventory(Lootbox lootbox, Player player) {
        this.lootbox = lootbox;
        this.player = player;
        String title = ChatColor.translateAlternateColorCodes('&', lootbox.getDisplayName());
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        this.inventory = Bukkit.createInventory(null, 27, title);
    }

    public void open() {
        player.openInventory(inventory);
        activeRolls.put(player.getUniqueId(), this);
        startRollSequence(1);
    }

    public void handleClose() {
        if (!rollFinished) {
            int currentRewards = wonItems.size();
            int totalRewards = lootbox.getRewardCount();

            for (int i = currentRewards; i < totalRewards; i++) {
                LootboxItem winner = getRandomItem();
                if (winner != null) {
                    wonItems.add(winner);
                    giveReward(winner);
                }
            }

            sendFinalBroadcast();
            rollFinished = true;
            activeRolls.remove(player.getUniqueId());
        }
    }

    private void startRollSequence(int rollNumber) {
        if (rollNumber > lootbox.getRewardCount()) {
            rollFinished = true;
            sendFinalBroadcast();
            activeRolls.remove(player.getUniqueId());
            return;
        }

        if (lootbox.getItems().isEmpty()) {
            player.sendMessage(ChatColor.RED + "This lootbox has no rewards configured!");
            player.closeInventory();
            activeRolls.remove(player.getUniqueId());
            return;
        }

        List<LootboxItem> rollingItems = new ArrayList<>();
        for (int i = 0; i < 39; i++) {
            rollingItems.add(getRandomItem());
        }

        LootboxItem winner = getRandomItem();
        rollingItems.add(winner);
        for (int i = 0; i < 11; i++)
            rollingItems.add(getRandomItem());

        new BukkitRunnable() {
            int step = 0;
            int delay = 1;
            int ticks = 0;

            @Override
            public void run() {
                if (ticks < delay) {
                    ticks++;
                    return;
                }
                ticks = 0;

                if (step >= 36) {
                    this.cancel();
                    wonItems.add(winner);
                    giveReward(winner);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                    setGlassColor((short) 2);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (rollNumber + 1 > lootbox.getRewardCount()) {
                                rollFinished = true;
                                sendFinalBroadcast();
                                activeRolls.remove(player.getUniqueId());
                                player.closeInventory();
                            } else {
                                startRollSequence(rollNumber + 1);
                            }
                        }
                    }.runTaskLater(Foxtrot.getInstance(), 30L);
                    return;
                }

                updateInventory(rollingItems, step);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);

                step++;

                if (step > 15)
                    delay = 2;
                if (step > 25)
                    delay = 3;
                if (step > 30)
                    delay = 4;
            }
        }.runTaskTimer(Foxtrot.getInstance(), 1L, 1L);
    }

    private void updateInventory(List<LootboxItem> rollingItems, int step) {
        short[] colors = { 15, 14, 13 };
        short color = colors[step % colors.length];

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        ItemStack pointer = new ItemStack(Material.REDSTONE_TORCH_ON);
        ItemMeta pointerMeta = pointer.getItemMeta();
        pointerMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Needle");
        pointer.setItemMeta(pointerMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler);
            inventory.setItem(i + 18, i == 4 ? pointer : filler);
        }

        for (int i = 0; i < 9; i++) {
            int itemIndex = step + i;
            if (itemIndex < rollingItems.size()) {
                inventory.setItem(i + 9, rollingItems.get(itemIndex).getItemStack());
            }
        }
    }

    private void setGlassColor(short data) {
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glass);
            if (i != 4)
                inventory.setItem(i + 18, glass);
        }
    }

    private LootboxItem getRandomItem() {
        if (lootbox.getItems().isEmpty())
            return null;
        double totalChance = lootbox.getItems().stream().mapToDouble(LootboxItem::getChance).sum();
        double randomValue = random.nextDouble() * totalChance;
        double currentSum = 0;

        for (LootboxItem item : lootbox.getItems()) {
            currentSum += item.getChance();
            if (randomValue <= currentSum) {
                return item;
            }
        }
        return lootbox.getItems().get(0);
    }

    private void giveReward(LootboxItem item) {
        if (item.getExecutionType() == LootboxItem.ExecutionType.ITEM_ONLY) {
            player.getInventory().addItem(item.getItemStack());
        } else {
            String type = item.getExecutionType() == LootboxItem.ExecutionType.CONSOLE ? "console" : "player";
            for (String command : item.getCommands()) {
                String cmd = command.replace("%player%", player.getName());
                if (type.equals("console")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                } else {
                    player.performCommand(cmd);
                }
            }
        }
    }

    private void sendFinalBroadcast() {
        if (wonItems.isEmpty())
            return;

        List<String> broadcastLines = new ArrayList<>();
        broadcastLines.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString()
                + "------------------------------------------------");
        broadcastLines.add(ChatColor.translateAlternateColorCodes('&', 
                "&c&l! " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', lootbox.getDisplayName())).toUpperCase() + " !"));
        broadcastLines.add(ChatColor.translateAlternateColorCodes('&', "&fOpened by &c" + player.getName()));
        broadcastLines.add("");
        broadcastLines.add(ChatColor.translateAlternateColorCodes('&', "&c&lREWARDS:"));

        for (LootboxItem item : wonItems) {
            String name = item.getItemStack().getItemMeta().hasDisplayName()
                    ? item.getItemStack().getItemMeta().getDisplayName()
                    : item.getItemStack().getType().name();
            broadcastLines.add(ChatColor.translateAlternateColorCodes('&', "&7● &f" + item.getItemStack().getAmount() + "x " + name));
        }

        broadcastLines.add("");
        broadcastLines.add(ChatColor.translateAlternateColorCodes('&', "&fUnlocked at"));
        broadcastLines.add(ChatColor.translateAlternateColorCodes('&', "&c" + lootbox.getStoreUrl()));
        broadcastLines.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString()
                + "------------------------------------------------");

        for (String line : broadcastLines) {
            Bukkit.broadcastMessage(line);
        }
    }
}
