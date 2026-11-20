package net.frozenorb.foxtrot.gameplay.loot.redeem;

import cc.fyre.proton.Proton;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.FileConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;

import static net.frozenorb.foxtrot.commands.SaveRedisCommand.saveData;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public final class RedeemCreatorHandler {

    @Getter private final Map<String, String> creators = new HashMap<>();
    @Getter private final List<CrateItem> rewards = new ArrayList<>();
    @Getter private int rows = 3;
    private FileConfig fileConfig;

    public RedeemCreatorHandler() {
        fileConfig = new FileConfig(Foxtrot.getInstance(), "redeem_creator.yml");
        FileConfiguration config = fileConfig.getConfig();
        config.options().copyDefaults(true);
        fileConfig.save();

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new net.frozenorb.foxtrot.gameplay.loot.redeem.listener.RedeemLootListener(), Foxtrot.getInstance());

        if (config.contains("rows")) {
            rows = config.getInt("rows");
        } else {
            rows = 3;
        }

        if (config.getConfigurationSection("partners") != null) {
            for (String partner : config.getConfigurationSection("partners").getKeys(false)) {
                creators.put(partner.toLowerCase(Locale.ROOT), partner);

                for (String alias : config.getStringList("partners." + partner + ".aliases")) {
                    creators.put(alias.toLowerCase(Locale.ROOT), partner);
                }
            }
        }

        if (config.getConfigurationSection("rewards") != null) {
            int fallbackSlot = 0;
            for (String path : config.getConfigurationSection("rewards").getKeys(false)) {
                String finalPath = "rewards." + path + ".";
                rewards.add(new CrateItem(
                        config.getItemStack(finalPath + "itemStack"),
                        config.getDouble(finalPath + "chance"),
                        config.getString(finalPath + "command"),
                        config.getBoolean(finalPath + "giveItem"),
                        config.getBoolean(finalPath + "broadcast"),
                        config.getInt(finalPath + "slot", fallbackSlot++)
                ));
            }
        }
    }

    public void addCreator(String name) {
        creators.put(name.toLowerCase(Locale.ROOT), name);
        save();
    }

    public void removeCreator(String name) {
        creators.entrySet().removeIf(entry -> entry.getValue().equalsIgnoreCase(name));
        save();
    }

    public void save() {
        FileConfiguration config = fileConfig.getConfig();
        
        Set<String> uniquePartners = new HashSet<>(creators.values());
        fileConfig.getConfig().set("partners", null);
        for (String partner : creators.values()) {
            fileConfig.getConfig().set("partners." + partner + ".aliases", new ArrayList<String>());
        }
        
        fileConfig.getConfig().set("rows", rows);

        config.set("rewards", null);
        int i = 0;
        for (CrateItem crateItem : rewards) {
            i++;
            String path = "rewards.item_" + i + ".";
            config.set(path + "itemStack", crateItem.getItemStack());
            config.set(path + "chance", crateItem.getChance());
            config.set(path + "command", crateItem.getCommand());
            config.set(path + "giveItem", crateItem.isGiveItem());
            config.set(path + "broadcast", crateItem.isBroadcast());
            config.set(path + "slot", crateItem.getSlot());
        }

        fileConfig.save();
    }

    public String getCreator(String input) {
        return creators.get(input.toLowerCase(Locale.ROOT));
    }

    public boolean hasRedeemed(Player player) {
        return Proton.getInstance().runRedisCommand(jedis -> jedis.exists("redeemed_creator:" + player.getUniqueId().toString()));
    }

    public void redeem(Player player, String creator) {
        Proton.getInstance().runRedisCommand(jedis -> jedis.set("redeemed_creator:" + player.getUniqueId().toString(), creator));
    }
    
    public void setRows(int rows) {
        this.rows = rows;
        save();
    }
}
