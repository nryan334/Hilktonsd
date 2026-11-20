package net.frozenorb.foxtrot.gameplay.loot.airdrop;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.listener.AirDropListener;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AirDropHandler {
    private Foxtrot instance;

    @Getter
    private List<UUID> variable = new ArrayList<>();

    @Getter
    private Map<String, AirDrop> airDrops = new HashMap<>();

    @Getter
    private File folder;

    public AirDropHandler(Foxtrot instance) {
        this.instance = instance;
        this.instance.getServer().getPluginManager().registerEvents(new AirDropListener(this.instance), this.instance);

        this.loadLootTable();
    }

    public void loadLootTable() {
        this.airDrops.clear();
        this.folder = new File(this.instance.getDataFolder(), "data/airdrops");

        if (!this.folder.exists()) {
            this.folder.mkdirs();
        }

        File oldFile = new File(this.instance.getDataFolder(), "data/airdrops.yml");
        if (oldFile.exists()) {
            this.migrateOldData(oldFile);
            oldFile.renameTo(new File(this.instance.getDataFolder(), "data/airdrops.yml.bak"));
        }

        File[] files = this.folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".yml")) {
                    this.loadFile(file);
                }
            }
        }
    }

    private void migrateOldData(File file) {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        if (data.get("rewards") == null)
            return;

        List<AirDropReward> rewards = new ArrayList<>();
        data.getConfigurationSection("rewards").getKeys(false)
                .forEach(it -> rewards.add(new AirDropReward(data.getItemStack("rewards." + it + ".itemStack"),
                        data.getDouble("rewards." + it + ".chance"),
                        data.getString("rewards." + it + ".command"),
                        data.getBoolean("rewards." + it + ".giveItem"))));

        AirDrop defaultAirDrop = new AirDrop("Default");
        defaultAirDrop.setRewards(rewards);
        this.saveAirDrop(defaultAirDrop);
    }

    private void loadFile(File file) {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        String id = data.getString("id", file.getName().replace(".yml", ""));
        String displayName = CC.translate(data.getString("displayName", "&b&lAir Drop"));
        Material material = Material.valueOf(data.getString("material", "DROPPER"));
        short dataValue = (short) data.getInt("data", 0);
        List<String> lore = data.getStringList("lore").stream().map(CC::translate).collect(Collectors.toList());

        List<AirDropReward> rewards = new ArrayList<>();
        if (data.getConfigurationSection("rewards") != null) {
            data.getConfigurationSection("rewards").getKeys(false)
                    .forEach(it -> rewards.add(new AirDropReward(data.getItemStack("rewards." + it + ".itemStack"),
                            data.getDouble("rewards." + it + ".chance"),
                            data.getString("rewards." + it + ".command"),
                            data.getBoolean("rewards." + it + ".giveItem"))));
        }

        AirDrop airDrop = new AirDrop(id, displayName, material, dataValue, lore, rewards);
        this.airDrops.put(id.toLowerCase(), airDrop);
    }

    public void saveAirDrop(AirDrop airDrop) {
        File file = new File(this.folder, airDrop.getId() + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        data.set("id", airDrop.getId());
        data.set("displayName", airDrop.getDisplayName());
        data.set("material", airDrop.getMaterial().name());
        data.set("data", airDrop.getData());
        data.set("lore", airDrop.getLore());

        data.set("rewards", null);
        int i = 0;
        for (AirDropReward reward : airDrop.getRewards()) {
            i++;
            data.set("rewards.reward_" + i + ".itemStack", reward.getItemStack());
            data.set("rewards.reward_" + i + ".chance", reward.getChance());
            data.set("rewards.reward_" + i + ".command", reward.getCommand());
            data.set("rewards.reward_" + i + ".giveItem", reward.isGrantItem());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLootTable() {
        this.airDrops.values().forEach(this::saveAirDrop);
    }

    public void deleteAirDrop(AirDrop airDrop) {
        this.airDrops.remove(airDrop.getId().toLowerCase());
        File file = new File(this.folder, airDrop.getId() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    public void setContents(Block block, Player player, boolean airdropAll, AirDrop airDrop) {
        if (!(block.getState() instanceof Dropper)) {
            return;
        }

        final Dropper dropper = (Dropper) block.getState();

        for (int i = 0; i < 9; i++) {
            dropper.getInventory().setItem(i, this.findReward(player, airdropAll, airDrop));
        }

        if (this.variable != null && this.variable.contains(player.getUniqueId())) {
            dropper.getInventory().setItem(5, null);
            final String prize = ChatColor.translateAlternateColorCodes('&', "&2&l$&a&l25 &9&lPAY&3&lPAL");
            this.instance.getServer().broadcastMessage("");
            this.instance.getServer()
                    .broadcastMessage(CC.translate("&f{player} &chas just won {item} &cfrom an &b&lAirdrop&c.")
                            .replace("{player}", player.getName()).replace("{item}", prize));
            this.instance.getServer().broadcastMessage(ChatColor.GRAY + "Purchase airdrops on our store at "
                    + ChatColor.WHITE + "store.strivepvp.top" + ChatColor.GRAY + ".");
            this.instance.getServer().broadcastMessage("");

            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(),
                    "vouchers add " + player.getName() + " $25 Store Voucher");

            this.variable.clear();
        }
    }

    private ItemStack findReward(Player player, boolean airdropAll, AirDrop airDrop) {
        final List<AirDropReward> rewards = new ArrayList<>(airDrop.getRewards());
        if (rewards.isEmpty())
            return new ItemStack(Material.AIR);

        double sumNumber = rewards.stream().mapToDouble(AirDropReward::getChance).sum();
        double random = Math.random() * sumNumber;

        AirDropReward choice = rewards.get(0);

        for (AirDropReward airDropReward : rewards) {
            double chance = airDropReward.getChance();

            if (chance > 0.0 && airdropAll && !airDropReward.getCommand().equalsIgnoreCase("none")
                    && !airDropReward.getCommand().equalsIgnoreCase("")) {
                chance /= 2.5;
            }

            choice = airDropReward;
            random -= chance;
            if (random < 0) {
                break;
            }
        }

        final String command = choice.getCommand();

        if (command != null && !command.equalsIgnoreCase("none") && !command.equalsIgnoreCase("")) {
            final String itemName = choice.getItemStack().getItemMeta().hasDisplayName()
                    ? choice.getItemStack().getItemMeta().getDisplayName()
                    : choice.getItemStack().getType().name();
            for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(CC.translate("&f" + player.getName() + " &6has just won a " + itemName
                        + " &6from a " + airDrop.getDisplayName() + "&6."));
                onlinePlayer.sendMessage(
                        ChatColor.GRAY + "Purchase an Airdrop at " + ChatColor.WHITE + "store.strivepvp.top");
                onlinePlayer.sendMessage("");
            }

            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(),
                    command.replace("{player}", player.getName()).replace("{displayName}", itemName));
        }

        if (choice.getItemStack() == null || !choice.isGrantItem()) {
            return new ItemStack(Material.AIR);
        }

        return choice.getItemStack().clone();
    }

    public Optional<AirDrop> getAirDrop(String id) {
        return Optional.ofNullable(this.airDrops.get(id.toLowerCase()));
    }

    public Optional<AirDrop> getAirDropByItemStack(ItemStack itemStack) {
        for (AirDrop airDrop : this.airDrops.values()) {
            if (isSimilar(itemStack, airDrop)) {
                return Optional.of(airDrop);
            }
        }
        return Optional.empty();
    }

    public boolean isSimilar(ItemStack itemStack, AirDrop airDrop) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        ItemStack compareTo = airDrop.getItemStack();

        if (itemStack.getItemMeta().getDisplayName() == null || compareTo.getItemMeta().getDisplayName() == null) {
            return false;
        }

        return itemStack.getType() == compareTo.getType() &&
                itemStack.getItemMeta().getDisplayName().equals(compareTo.getItemMeta().getDisplayName());
    }
}
