package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.command.parameterType.CrateParameterProvider;
import net.frozenorb.foxtrot.gameplay.loot.crate.data.ArmorType;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import net.frozenorb.foxtrot.gameplay.loot.crate.listener.CrateListener;
import net.frozenorb.foxtrot.server.voucher.command.VoucherCommand;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CrateHandler {
    private final Foxtrot instance;

    @Getter
    private List<Crate> crates = new ArrayList<>();
    @Getter
    private File folder;
    private com.mongodb.DBCollection collection;

    public CrateHandler(Foxtrot instance) {
        this.instance = instance;

        Proton.getInstance().getCommandHandler().registerParameterType(Crate.class, new CrateParameterProvider());

        this.instance.getServer().getPluginManager().registerEvents(new CrateListener(this), this.instance);
        this.instance.getServer().getScheduler().runTaskLater(this.instance, this::loadData, 40);
    }

    public void loadData() {
        this.collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Crates");

        boolean loadedFromMongo = false;
        try (com.mongodb.DBCursor cursor = this.collection.find()) {
            while (cursor.hasNext()) {
                com.mongodb.BasicDBObject dbObject = (com.mongodb.BasicDBObject) cursor.next();
                String yamlData = dbObject.getString("yamlData");
                if (yamlData != null) {
                    YamlConfiguration config = new YamlConfiguration();
                    try {
                        config.loadFromString(yamlData);
                        this.loadFile(config);
                        loadedFromMongo = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        this.folder = new File(this.instance.getDataFolder(), "data/crates");
        if (!loadedFromMongo && this.folder.exists() && this.folder.listFiles() != null) {
            File[] files = this.folder.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.getName().endsWith(".yml")) {
                        try {
                            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                            this.loadFile(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (Crate crate : this.crates) {
                    this.saveCrate(crate);
                }

                for (File file : files) {
                    file.delete();
                }
                this.folder.delete();
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Crates] Migrated " + this.crates.size()
                        + " local crates to MongoDB and deleted local files.");
            }
        }

        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.GREEN + "[Crates] Loaded " + crates.size() + " crates from MongoDB.");
    }

    public void loadFile(FileConfiguration data) {
        final String id = data.getString("id");
        final String displayName = data.getString("displayName");
        final Material material = Material.getMaterial(data.getString("material"));
        final short dataValue = (short) data.getInt("data", 0);
        final Color fireworkColor = Color.fromRGB(data.getInt("fireworkColor"));
        final Material blockMaterial = data.getString("blockMaterial") != null
                ? Material.getMaterial(data.getString("blockMaterial"))
                : Material.CHEST;
        final short blockData = (short) data.getInt("blockData", 0);
        final List<String> lore = data.getStringList("lore");
        final List<String> hologramLines = data.getStringList("hologramLines");
        final int rows = data.getInt("rows", 3);
        final ItemStack fillerItem = data.getItemStack("fillerItem");

        final List<CrateItem> crateItems = new ArrayList<>();

        if (data.getConfigurationSection("items") != null) {
            int fallbackSlot = 0;
            for (String path : data.getConfigurationSection("items").getKeys(false)) {
                final String finalPath = "items." + path + ".";
                crateItems.add(
                        new CrateItem(data.getItemStack(finalPath + "itemStack"), data.getDouble(finalPath + "chance"),
                                data.getString(finalPath + "command"), data.getBoolean(finalPath + "giveItem"),
                                data.getBoolean(finalPath + "broadcast"),
                                data.getInt(finalPath + "slot", fallbackSlot++)));
            }
        }

        Crate crate = new Crate(id, displayName, material, dataValue, blockMaterial, blockData, fireworkColor, lore,
                hologramLines, rows,
                fillerItem, crateItems);
        crate.setSellableOnTokenShop(data.getBoolean("sellableOnTokenShop", false));
        crate.setFillerGlow(data.getBoolean("fillerGlow", false));
        this.crates.add(crate);
    }

    public void saveData() {
        for (Crate crate : this.crates) {
            this.saveCrate(crate);
        }
    }

    public void saveCrate(Crate crate) {
        if (this.collection == null) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        crate.saveCrate(null, config, crate);
        String yamlString = config.saveToString();

        com.mongodb.BasicDBObject dbObject = new com.mongodb.BasicDBObject();
        dbObject.put("id", crate.getId());
        dbObject.put("yamlData", yamlString);

        this.collection.update(new com.mongodb.BasicDBObject("id", crate.getId()), dbObject, true, false);
    }

    public void deleteCrate(Crate crate) {
        this.crates.remove(crate);
        if (this.collection != null) {
            this.collection.remove(new com.mongodb.BasicDBObject("id", crate.getId()));
        }
    }

    public Optional<Crate> findByItemStack(ItemStack itemStack) {
        return crates.stream().filter(it -> it.getItemStack().isSimilar(itemStack)).findFirst();
    }

    public Optional<Crate> findById(String id) {
        return crates.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst();
    }

    public void openCrate(Player player, Crate crate) {
        player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&aYou have opened a " + crate.getDisplayName() + "&a."));
        player.playSound(player.getLocation(), Sound.BAT_LOOP, 1, 1);
        spawnFireworks(player.getLocation(), 1, 2, crate.getFireworkColor(), FireworkEffect.Type.BURST);

        if (crate.getId().equalsIgnoreCase("Reinforce")) {
            final List<ArmorType> types = Arrays.stream(ArmorType.values()).collect(Collectors.toList());
            final ArmorType armorType = types.get(ThreadLocalRandom.current().nextInt(0, types.size()));

            giveArmor(player, armorType);
            player.getInventory().addItem(this.getRandomItem(player, crate, crate.getItems()));
        } else {
            player.getInventory().addItem(this.getRandomItem(player, crate, crate.getItems()));
        }

    }

    public void giveArmor(Player player, ArmorType armorType) {

        List<String> lore = Arrays.asList("§cHellForged IV", "§cImplants V", "§cMermaid III", "§cRecover I",
                "§cFireResistance I");

        player.getInventory()
                .addItem(ItemBuilder.of(armorType.getHelmet()).name("§6§lReinforce §7┃ §fHelmet").setLore(lore)
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5)
                        .enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());

        if (armorType == ArmorType.GOLD) {
            player.getInventory()
                    .addItem(ItemBuilder.of(armorType.getChestplate()).name("§6§lReinforce §7┃ §fChestplate")
                            .setLore(lore).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                            .enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());
        } else {

            player.getInventory()
                    .addItem(ItemBuilder.of(armorType.getChestplate()).name("§6§lReinforce §7┃ §fChestplate")
                            .setLore(lore).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                            .enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());
        }

        player.getInventory()
                .addItem(ItemBuilder.of(armorType.getLeggings()).name("§6§lReinforce §7┃ §fLeggings").setLore(lore)
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5)
                        .enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());
        player.getInventory()
                .addItem(ItemBuilder.of(armorType.getBoots()).name("§6§lReinforce §7┃ §fBoots")
                        .setLore(Arrays.asList("§cHellForged IV", "§cImplants V", "§cMermaid III", "§cRecover I",
                                "§cFireResistance I", "§cSpeed II"))
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5)
                        .enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).enchant(Enchantment.PROTECTION_FALL, 4).build());
    }

    public ItemStack getRandomItem(Player player, Crate crate, List<CrateItem> crateItems) {
        double sum = crateItems.stream().mapToDouble(CrateItem::getChance).sum();
        double randomNumber = Math.random() * sum;

        CrateItem choice = null;

        for (CrateItem crateItem : crateItems) {
            choice = crateItem;
            randomNumber -= crateItem.getChance();
            if (randomNumber < 0) {
                break;
            }
        }

        if (choice == null) {
            return new ItemStack(Material.AIR);
        }

        if (choice.getCommand() != null && !choice.getCommand().equalsIgnoreCase("none")) {
            final String command = choice.getCommand();
            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(),
                    command.replace("{player}", player.getName()).replace("{displayName}",
                            choice.getItemStack().getItemMeta().hasDisplayName()
                                    ? choice.getItemStack().getItemMeta().getDisplayName()
                                    : choice.getItemStack().getType().name()));
        }

        if (choice.isBroadcast()) {
            for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', crate.getDisplayName()));
                onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&f" + player.getName() + " &chas won a "
                                + (choice.getItemStack().getItemMeta().hasDisplayName()
                                        ? choice.getItemStack().getItemMeta().getDisplayName()
                                        : choice.getItemStack().getType().name())
                                + "&c!"));
                onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7Purchase this crate and more in the token shop by typing &f/tokenshop"));
                new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a[Click to open the Token Shop]"))
                        .command("/tokenshop").send(onlinePlayer);
                onlinePlayer.sendMessage("");
            }
        }

        if (!choice.isGiveItem()) {
            return new ItemStack(Material.AIR);
        }

        final ItemStack itemStack = choice.getItemStack().clone();

        if (itemStack.getAmount() == 0 || itemStack.getAmount() <= 0) {
            itemStack.setAmount(1);
        }

        return itemStack;
    }

    public static void spawnFireworks(Location location, int amount, int power, Color color, FireworkEffect.Type fireworkEffect) {
        org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.FIREWORK);
        org.bukkit.inventory.meta.FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(power);
        fireworkMeta.addEffect(FireworkEffect.builder().with(fireworkEffect).with(FireworkEffect.Type.BURST).withColor(color).flicker(true).build());
        firework.setFireworkMeta(fireworkMeta);
        for (int i = 0; i < amount; i++) {
            org.bukkit.entity.Firework entityFirework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.FIREWORK);
            entityFirework.setFireworkMeta(fireworkMeta);
        }
    }
}
