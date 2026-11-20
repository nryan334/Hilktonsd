package net.frozenorb.foxtrot.gameplay.ability;

import cc.fyre.piston.util.Cooldown;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ClassUtils;
import com.comphenix.protocol.ProtocolLibrary;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Table;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.icon.ItemStackIcon;
import com.lunarclient.apollo.module.cooldown.CooldownModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.listener.AbilityListener;
import net.frozenorb.foxtrot.gameplay.ability.menu.TradeMenu;
import net.frozenorb.foxtrot.gameplay.ability.menu.editor.AbilityEditorMenu;
import net.frozenorb.foxtrot.gameplay.ability.packet.InvisibilityPacketAdapter;
import net.frozenorb.foxtrot.gameplay.ability.parameter.AbilityParameter;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavepvp.suge.Suge;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class AbilityHandler {

    @Getter
    private final Map<Ability, Integer> usedItems = new HashMap<>();
    @Getter
    private final Map<String, Ability> abilities = new HashMap<>();
    @Getter
    private final Table<UUID, String, Long> lastUsedItem = HashBasedTable.create();

    @Getter
    private final Map<UUID, Long> globalCooldowns = new HashMap<>();

    @Getter
    private final List<Ability> tradeAbleItems = new ArrayList<>();

    @Getter
    private final Set<String> disabledAbilities = new HashSet<>();
    @Getter
    private final Map<String, Category> categoryOverrides = new HashMap<>();

    @Getter
    public static final Table<UUID, Ability, Cooldown> cooldown = HashBasedTable.create();

    private File file;
    private FileConfiguration data;

    public AbilityHandler(Foxtrot instance) {
        Category.loadCategories();
        for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(),
                "net.frozenorb.foxtrot.gameplay.ability.type")) {

            if (!Ability.class.isAssignableFrom(clazz) || clazz.isInterface()
                    || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            try {
                final Ability ability = (Ability) clazz.newInstance();

                this.abilities.put(ability.getName(), ability);
                System.out.println("[AbilityHandler] Loaded ability: " + ability.getName() + " ("
                        + ability.getCategory().name() + ")");
            } catch (Throwable ex) {
                System.out.println("[AbilityHandler] Failed to load ability class: " + clazz.getName());
                ex.printStackTrace();
            }
        }

        instance.getServer().getPluginManager().registerEvents(new AbilityListener(Foxtrot.getInstance(), this),
                instance);
        Proton.getInstance().getCommandHandler().registerParameterType(Ability.class, new AbilityParameter());
        Proton.getInstance().getCommandHandler().registerClass(this.getClass());
        ProtocolLibrary.getProtocolManager().addPacketListener(new InvisibilityPacketAdapter());
        instance.getServer().getScheduler().runTaskLater(instance, this::loadStatistics, 20);
    }

    public void loadStatistics() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/ability-stats.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("abilities") != null) {
            for (String key : data.getConfigurationSection("abilities").getKeys(false)) {
                final Ability ability = this.fromName(key);

                if (ability == null) {
                    continue;
                }

                this.usedItems.put(ability, data.getInt("abilities." + key));
            }
        }

        if (this.data.get("disabled-abilities") != null) {
            List<String> disabled = this.data.getStringList("disabled-abilities");
            this.disabledAbilities.clear();
            this.disabledAbilities.addAll(disabled);
            System.out.println("[AbilityHandler] Loaded " + disabled.size() + " disabled abilities from file.");
        }

        if (this.data.get("category-overrides") != null) {
            for (String key : this.data.getConfigurationSection("category-overrides").getKeys(false)) {
                String categoryName = this.data.getString("category-overrides." + key);
                Category category = Category.fromName(categoryName);
                if (category != null) {
                    this.categoryOverrides.put(key, category);
                }
            }
            System.out.println(
                    "[AbilityHandler] Loaded " + this.categoryOverrides.size() + " category overrides from file.");
        }

        if (this.data.get("ability-metadata") != null) {
            for (String key : this.data.getConfigurationSection("ability-metadata").getKeys(false)) {
                Ability ability = this.fromName(key);
                if (ability != null) {
                    List<String> meta = this.data.getStringList("ability-metadata." + key);
                    ability.getMetadata().clear();
                    ability.getMetadata().addAll(meta);
                }
            }
            System.out.println("[AbilityHandler] Loaded ability metadata tags from file.");
        }
    }

    public void saveStatistics() {
        Map<String, Object> configValues = this.data.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            this.data.set(entry.getKey(), null);

        for (Map.Entry<Ability, Integer> abilityIntegerEntry : this.usedItems.entrySet()) {
            this.data.set("abilities." + abilityIntegerEntry.getKey().getName(), +abilityIntegerEntry.getValue());
        }

        this.data.set("disabled-abilities", new ArrayList<>(this.disabledAbilities));

        for (Map.Entry<String, Category> entry : this.categoryOverrides.entrySet()) {
            this.data.set("category-overrides." + entry.getKey(), entry.getValue().getName());
        }

        for (Ability ability : this.abilities.values()) {
            if (!ability.getMetadata().isEmpty()) {
                this.data.set("ability-metadata." + ability.getName(), new ArrayList<>(ability.getMetadata()));
            }
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ability fromName(String name) {
        return this.abilities.values().stream().filter(ability -> ability.getName().equalsIgnoreCase(name)).findAny()
                .orElse(null);
    }

    @Command(names = { "ability" }, permission = "foxtrot.command.ability")
    public static void execute(CommandSender sender,
            @Parameter(name = "ability") Ability ability,
            @Parameter(name = "amount", defaultValue = "1") int amount,
            @Parameter(name = "player", defaultValue = "self") Player target) {
        final ItemStack itemStack = ability.getItem().clone();

        if (ability.getName().equalsIgnoreCase("Backpack")) {

            for (int i = 0; i < amount; i++) {
                final ItemStack theStack = ability.getItem().clone();
                final ItemMeta itemMeta = theStack.getItemMeta();
                final List<String> lore = itemMeta.getLore();
                lore.add(ChatColor.GRAY + "Identifier: " + ChatColor.WHITE + UUID.randomUUID());
                itemMeta.setLore(lore);
                theStack.setItemMeta(itemMeta);
                target.getInventory().addItem(theStack.clone());
            }
            return;
        }

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);

    }

    @Command(names = { "ability reset" }, permission = "foxtrot.command.ability.reset")
    public static void reset(Player player, @Parameter(name = "ability") Ability ability) {
        if (!getCooldown().contains(player.getUniqueId(), ability)) {
            player.sendMessage(
                    ChatColor.RED + "You don't have a cooldown for " + ability.getDisplayName() + ChatColor.RED + ".");
            return;
        }

        getCooldown().remove(player.getUniqueId(), ability);

        player.sendMessage(
                ChatColor.RED + "Removed cooldown for the " + ability.getDisplayName() + ChatColor.RED + ".");
    }

    public void applyCooldown(Ability ability, Player player) {
        applyCooldown(ability, player, ability.getCooldown());
    }

    public void applyCooldown(Ability ability, Player player, long cooldownTime) {
        this.usedItems.putIfAbsent(ability, 0);
        this.usedItems.replace(ability, this.usedItems.get(ability) + 1);

        if (ability.getCooldown() <= 0) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        final ArmorClass armorClass = Foxtrot.getInstance().getArmorClassHandler().findWearing(player);

        if (Foxtrot.getInstance().getMiniEventsHandler() != null
                && Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent() != null) {
            final MiniEvent miniEvent = Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent();

            cooldownTime = miniEvent.getEventID().equalsIgnoreCase("Rage") ? cooldownTime / 2 : cooldownTime;
        } else if (armorClass != null && armorClass.getId().equalsIgnoreCase("Raider")) {
            cooldownTime *= 0.85;
        } else if (Foxtrot.getInstance().getNetworkBoosterHandler() != null
                && Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()) {
            cooldownTime *= 0.75;
        }



        if (player.hasMetadata("NO_COOLDOWN")) {
            cooldownTime = 0;
        }

        java.util.Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
        if (apolloPlayer.isPresent()) {
            CooldownModule cooldownModule = Apollo.getModuleManager().getModule(CooldownModule.class);
            com.lunarclient.apollo.module.cooldown.Cooldown apolloCooldown = com.lunarclient.apollo.module.cooldown.Cooldown
                    .builder()
                    .name(ability.getName())
                    .duration(java.time.Duration.ofMillis(cooldownTime))
                    .icon(ItemStackIcon.builder()
                            .itemId(ability.getMaterial().getId())
                            .build())
                    .build();
            cooldownModule.displayCooldown(apolloPlayer.get(), apolloCooldown);
        }

        cooldown.put(player.getUniqueId(), ability, new Cooldown(cooldownTime));
    }

    @Command(names = { "tradeitem" }, permission = "")
    public static void tradeItem(Player player) {
        final ItemStack itemStack = player.getItemInHand().clone();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You don't have an item in your hand!");
            return;
        }

        final Ability ability = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()
                .stream().filter(it -> it.isSimilar(itemStack)).findFirst().orElse(null);

        if (ability == null) {
            player.sendMessage(ChatColor.RED + "You must have an ability item in your hand!");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().getAbilityHandler().getTradeAbleItems().contains(ability)) {
            player.sendMessage(ChatColor.RED + "This ability item may not be traded!");
            return;
        }

        new TradeMenu(itemStack, ability).openMenu(player);
    }

    public boolean hasCooldown(Ability ability, Player player) {
        if (!cooldown.contains(player.getUniqueId(), ability)) {
            return false;
        }

        return !cooldown.get(player.getUniqueId(), ability).hasExpired();
    }

    public long getRemaining(Ability ability, Player player) {
        if (!cooldown.contains(player.getUniqueId(), ability)) {
            return 0L;
        }

        return cooldown.get(player.getUniqueId(), ability).getRemaining();
    }

    @Command(names = { "listabilities" }, permission = "op")
    public static void listAbilities(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Loaded Abilities ("
                + Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().size() + "):");
        for (String name : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().keySet()) {
            Ability ability = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().get(name);
            boolean enabled = Foxtrot.getInstance().getMapHandler().getAbilityHandler().isAbilityEnabled(ability);
            sender.sendMessage(ChatColor.YELLOW + " - " + name + " (" + ability.getCategory().name() + ")"
                    + (enabled ? "" : ChatColor.RED + " [DISABLED]"));
        }
    }

    @Command(names = { "ability editor" }, permission = "foxtrot.command.ability.editor")
    public static void openEditor(Player player) {
        new AbilityEditorMenu().openMenu(player);
    }

    public boolean isAbilityEnabled(Ability ability) {
        return !disabledAbilities.contains(ability.getName());
    }

    public void toggleAbility(Ability ability) {
        if (disabledAbilities.contains(ability.getName())) {
            disabledAbilities.remove(ability.getName());
        } else {
            disabledAbilities.add(ability.getName());
        }
    }

    public void setAbilityCategory(Ability ability, Category category) {
        categoryOverrides.put(ability.getName(), category);
    }

    public Category getEffectiveCategory(Ability ability) {
        return categoryOverrides.getOrDefault(ability.getName(), ability.getCategory());
    }

    @Command(names = { "ability category create" }, permission = "foxtrot.command.ability.category")
    public static void createCategory(CommandSender sender,
            @Parameter(name = "name") String name,
            @Parameter(name = "displayName") String displayName,
            @Parameter(name = "displayItem") String displayItem,
            @Parameter(name = "glassData") int glassData,
            @Parameter(name = "slot") int slot) {

        Material material;
        try {
            material = Material.valueOf(displayItem.toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid material name! Example: TRIPWIRE_HOOK");
            return;
        }

        Category category = new Category(name.toUpperCase(), displayName, name, material, (byte) glassData, slot);
        Category.saveCategory(category);
        sender.sendMessage(ChatColor.GREEN + "Category " + name + " created successfully and saved to MongoDB.");
    }

    @Command(names = { "ability category delete" }, permission = "foxtrot.command.ability.category")
    public static void deleteCategory(CommandSender sender,
            @Parameter(name = "name") String name) {

        if (name.equalsIgnoreCase("ALL")) {
            sender.sendMessage(ChatColor.RED + "You cannot delete the ALL category!");
            return;
        }

        Category.deleteCategory(name);
        sender.sendMessage(ChatColor.RED + "Category " + name + " deleted successfully from memory and MongoDB.");
    }

    @Command(names = { "ability category list" }, permission = "foxtrot.command.ability.category")
    public static void listCategories(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Ability Categories (" + Category.values().length + ")");
        for (Category category : Category.values()) {
            sender.sendMessage(ChatColor.YELLOW + "- " + category.getName() + " (" + category.getDisplayName()
                    + ChatColor.YELLOW + ") Slot: " + category.getSlot());
        }
    }
}
