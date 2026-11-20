package net.frozenorb.foxtrot.gameplay.tokenshop;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TokenShopHandler {
    private final File configFile;
    private FileConfiguration config;

    private List<TokenShopCategory> categories = new ArrayList<>();
    private int globalPromoDiscount = 0;

    public TokenShopHandler() {
        this.configFile = new File(Foxtrot.getInstance().getDataFolder(), "tokenshop.yml");
        this.load();
    }

    public void load() {
        this.categories.clear();
        if (!configFile.exists()) {
            setupDefaults();
            return;
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.globalPromoDiscount = config.getInt("globalPromoDiscount", 0);

        if (config.getConfigurationSection("categories") == null) {
            setupDefaults();
            return;
        }

        for (String catKey : config.getConfigurationSection("categories").getKeys(false)) {
            TokenShopCategory category = new TokenShopCategory();
            category.setId(catKey);
            category.setDisplayName(config.getString("categories." + catKey + ".displayName", catKey));

            String matStr = config.getString("categories." + catKey + ".material", "CHEST");
            try {
                category.setMaterial(Material.valueOf(matStr));
            } catch (Exception e) {
                category.setMaterial(Material.CHEST);
            }
            category.setDamage(config.getInt("categories." + catKey + ".damage", 0));
            category.setSlot(config.getInt("categories." + catKey + ".slot", 0));
            category.setGkits(config.getBoolean("categories." + catKey + ".gkits", false));
            category.setKeys(config.getBoolean("categories." + catKey + ".keys", false));

            if (config.getConfigurationSection("categories." + catKey + ".products") != null) {
                for (String prodKey : config.getConfigurationSection("categories." + catKey + ".products")
                        .getKeys(false)) {
                    TokenShopProduct product = new TokenShopProduct();
                    product.setId(prodKey);
                    product.setDisplayName(config
                            .getString("categories." + catKey + ".products." + prodKey + ".displayName", prodKey));
                    product.setLore(config.getStringList("categories." + catKey + ".products." + prodKey + ".lore"));

                    String prodMatStr = config.getString("categories." + catKey + ".products." + prodKey + ".material",
                            "STONE");
                    try {
                        product.setMaterial(Material.valueOf(prodMatStr));
                    } catch (Exception e) {
                        product.setMaterial(Material.STONE);
                    }
                    product.setDamage(config.getInt("categories." + catKey + ".products." + prodKey + ".damage", 0));
                    product.setSlot(config.getInt("categories." + catKey + ".products." + prodKey + ".slot", 0));
                    product.setCost(config.getInt("categories." + catKey + ".products." + prodKey + ".cost", 5));
                    product.setPromoPrice(
                            config.getInt("categories." + catKey + ".products." + prodKey + ".promoPrice", -1));
                    product.setCommands(
                            config.getStringList("categories." + catKey + ".products." + prodKey + ".commands"));

                    category.getProducts().add(product);
                }
            }
            this.categories.add(category);
        }
    }

    public void save() {
        if (config == null) {
            config = new YamlConfiguration();
        }

        config.set("globalPromoDiscount", globalPromoDiscount);
        config.set("categories", null);

        for (TokenShopCategory category : categories) {
            String catKey = category.getId();
            config.set("categories." + catKey + ".displayName", category.getDisplayName());
            config.set("categories." + catKey + ".material", category.getMaterial().name());
            config.set("categories." + catKey + ".damage", category.getDamage());
            config.set("categories." + catKey + ".slot", category.getSlot());
            config.set("categories." + catKey + ".gkits", category.isGkits());
            config.set("categories." + catKey + ".keys", category.isKeys());

            for (TokenShopProduct product : category.getProducts()) {
                String prodKey = product.getId();
                config.set("categories." + catKey + ".products." + prodKey + ".displayName", product.getDisplayName());
                config.set("categories." + catKey + ".products." + prodKey + ".lore", product.getLore());
                config.set("categories." + catKey + ".products." + prodKey + ".material", product.getMaterial().name());
                config.set("categories." + catKey + ".products." + prodKey + ".damage", product.getDamage());
                config.set("categories." + catKey + ".products." + prodKey + ".slot", product.getSlot());
                config.set("categories." + catKey + ".products." + prodKey + ".cost", product.getCost());
                config.set("categories." + catKey + ".products." + prodKey + ".promoPrice", product.getPromoPrice());
                config.set("categories." + catKey + ".products." + prodKey + ".commands", product.getCommands());
            }
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupDefaults() {
        this.categories.clear();
        this.globalPromoDiscount = 0;

        TokenShopCategory baseFall = new TokenShopCategory();
        baseFall.setId("base_fall");
        baseFall.setDisplayName("&d&lBase & Traps");
        baseFall.setMaterial(Material.WORKBENCH);
        baseFall.setSlot(19);

        TokenShopProduct baseToken = new TokenShopProduct();
        baseToken.setId("base_token");
        baseToken.setDisplayName("&b&lBase Token");
        baseToken.setMaterial(Material.STAINED_GLASS);
        baseToken.setDamage(14);
        baseToken.setCost(3);
        baseToken.setSlot(20);
        baseToken.getLore().add("&7Purchase a Base Token for your team.");
        baseToken.getLore().add("&7This token allows you to generate a base");
        baseToken.getLore().add("&7using &f/blueprints&7.");
        baseToken.getCommands().add("givebasetokens %player_team% 1");
        baseToken.getCommands().add("broadcast &a%player% &ehas purchased a &dBase Token &efor their team!");
        baseFall.getProducts().add(baseToken);

        TokenShopProduct fallerToken = new TokenShopProduct();
        fallerToken.setId("faller_token");
        fallerToken.setDisplayName("&4&lFaller Token");
        fallerToken.setMaterial(Material.HOPPER);
        fallerToken.setCost(5);
        fallerToken.setSlot(24);
        fallerToken.getLore().add("&7Purchase a Faller Token for your team.");
        fallerToken.getLore().add("&7This token allows you to generate a faller trap");
        fallerToken.getLore().add("&7using &f/blueprints&7.");
        fallerToken.getCommands().add("givefallertokens %player_team% 1");
        fallerToken.getCommands().add("broadcast &a%player% &ehas purchased a &4Faller Token &efor their team!");
        baseFall.getProducts().add(fallerToken);

        this.categories.add(baseFall);

        TokenShopCategory gkits = new TokenShopCategory();
        gkits.setId("gkits");
        gkits.setDisplayName("&b&lGKits");
        gkits.setMaterial(Material.DIAMOND_CHESTPLATE);
        gkits.setSlot(21);
        gkits.setGkits(true);
        this.categories.add(gkits);

        TokenShopCategory keys = new TokenShopCategory();
        keys.setId("keys");
        keys.setDisplayName("&e&lCrate Keys");
        keys.setMaterial(Material.TRIPWIRE_HOOK);
        keys.setSlot(23);
        keys.setKeys(true);
        this.categories.add(keys);

        TokenShopCategory vouchers = new TokenShopCategory();
        vouchers.setId("vouchers");
        vouchers.setDisplayName("&a&lLife Vouchers");
        vouchers.setMaterial(Material.PAPER);
        vouchers.setSlot(25);

        TokenShopProduct life1 = new TokenShopProduct();
        life1.setId("1_life");
        life1.setDisplayName("&a&l1 Life Voucher");
        life1.setMaterial(Material.PAPER);
        life1.setCost(2);
        life1.setSlot(20);
        life1.getLore().add("&7Purchase a Voucher that grants");
        life1.getLore().add("&71 Life to whoever redeems it.");
        life1.getCommands().add("voucher give %player% lives 1 perm &4&l1 Life &7┃ &bVoucher");
        life1.getCommands().add("broadcast &a%player% &ehas purchased a &a1 Life Voucher&e!");
        vouchers.getProducts().add(life1);

        TokenShopProduct lives5 = new TokenShopProduct();
        lives5.setId("5_lives");
        lives5.setDisplayName("&b&l5 Lives Voucher");
        lives5.setMaterial(Material.PAPER);
        lives5.setCost(5);
        lives5.setSlot(22);
        lives5.getLore().add("&7Purchase a Voucher that grants");
        lives5.getLore().add("&75 Lives to whoever redeems it.");
        lives5.getCommands().add("voucher give %player% lives 5 perm &4&l5 Lives &7┃ &bVoucher");
        lives5.getCommands().add("broadcast &a%player% &ehas purchased a &b5 Lives Voucher&e!");
        vouchers.getProducts().add(lives5);

        TokenShopProduct lives10 = new TokenShopProduct();
        lives10.setId("10_lives");
        lives10.setDisplayName("&6&l10 Lives Voucher");
        lives10.setMaterial(Material.PAPER);
        lives10.setCost(8);
        lives10.setSlot(24);
        lives10.getLore().add("&7Purchase a Voucher that grants");
        lives10.getLore().add("&710 Lives to whoever redeems it.");
        lives10.getCommands().add("voucher give %player% lives 10 perm &4&l10 Lives &7┃ &bVoucher");
        lives10.getCommands().add("broadcast &a%player% &ehas purchased a &610 Lives Voucher&e!");
        vouchers.getProducts().add(lives10);

        this.categories.add(vouchers);

        this.save();
    }

    public TokenShopCategory getCategory(String id) {
        return categories.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
