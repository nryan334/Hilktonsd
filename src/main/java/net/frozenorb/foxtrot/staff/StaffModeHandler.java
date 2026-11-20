package net.frozenorb.foxtrot.staff;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class StaffModeHandler {

    public enum VanishState {
        VISIBLE,
        STAFF,
        INVISIBLE
    }

    @Getter
    private final Set<UUID> modMode = new HashSet<>();
    private final Map<UUID, VanishState> vanishState = new HashMap<>();

    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();

    public StaffModeHandler() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new StaffModeListener(this),
                Foxtrot.getInstance());
        Proton.getInstance().getCommandHandler().registerClass(this.getClass());
        Proton.getInstance().getVisibilityHandler().registerHandler("FOXTROT_STAFF_VISIBILITY", new net.frozenorb.foxtrot.staff.StaffVisibilityAdapter(this));
    }

    public boolean isInModMode(UUID uuid) {
        return modMode.contains(uuid);
    }

    public boolean isInVanish(UUID uuid) {
        return vanishState.getOrDefault(uuid, VanishState.VISIBLE) != VanishState.VISIBLE;
    }

    public VanishState getVanishState(UUID uuid) {
        return vanishState.getOrDefault(uuid, VanishState.VISIBLE);
    }

    public boolean hasStaffPowers(UUID uuid) {
        return isInModMode(uuid) || isInVanish(uuid);
    }

    @Command(names = { "mod", "h", "modmode", "staff", "staffmode" }, permission = "foxtrot.staff")
    public static void mod(Player player) {
        StaffModeHandler handler = Foxtrot.getInstance().getStaffModeHandler();

        if (handler.isInModMode(player.getUniqueId())) {
            handler.disableModMode(player);
        } else {
            handler.enableModMode(player);
        }
    }

    @Command(names = { "v", "vanish", "vis" }, permission = "foxtrot.staff")
    public static void vanish(Player player) {
        StaffModeHandler handler = Foxtrot.getInstance().getStaffModeHandler();

        VanishState currentState = handler.getVanishState(player.getUniqueId());

        if (currentState == VanishState.VISIBLE) {
            handler.setVanish(player, VanishState.STAFF);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Vanish: &eEnabled"));
        } else if (currentState == VanishState.STAFF) {
            handler.setVanish(player, VanishState.INVISIBLE);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Vanish: &7Invisible for all"));
        } else {
            handler.setVanish(player, VanishState.VISIBLE);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Vanish: &cDisabled"));
        }
    }

    public void enableModMode(Player player) {
        modMode.add(player.getUniqueId());

        savedInventories.put(player.getUniqueId(), player.getInventory().getContents().clone());
        savedArmor.put(player.getUniqueId(), player.getInventory().getArmorContents().clone());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.CREATIVE);

        giveModItems(player);
        enableVanish(player);

        player.sendMessage(ChatColor.AQUA + "Mod Mode: " + ChatColor.GREEN + "Enabled");
        net.frozenorb.foxtrot.listener.LunarClientListener.updateNametag(player);
    }

    public void disableModMode(Player player) {
        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (savedInventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(savedInventories.remove(player.getUniqueId()));
        }

        if (savedArmor.containsKey(player.getUniqueId())) {
            player.getInventory().setArmorContents(savedArmor.remove(player.getUniqueId()));
        }

        player.updateInventory();

        modMode.remove(player.getUniqueId());

        disableVanish(player);

        player.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.RED + "Disabled");
        net.frozenorb.foxtrot.listener.LunarClientListener.updateNametag(player);
    }

    public void enableVanish(Player player) {
        vanishState.put(player.getUniqueId(), VanishState.STAFF);
        player.setMetadata("invisible", new FixedMetadataValue(Foxtrot.getInstance(), true));

        if (isInModMode(player.getUniqueId())) {
            short data = (short) 10;
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data(data)
                    .name(ChatColor.AQUA + "Vanish Toggle").build());
            player.updateInventory();
        }

        Proton.getInstance().getVisibilityHandler().update(player);

        net.frozenorb.foxtrot.listener.LunarClientListener.updateNametag(player);
    }

    public void disableVanish(Player player) {
        vanishState.put(player.getUniqueId(), VanishState.VISIBLE);
        player.removeMetadata("invisible", Foxtrot.getInstance());

        if (isInModMode(player.getUniqueId())) {
            short data = (short) 8;
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data(data)
                    .name(ChatColor.AQUA + "Vanish Toggle").build());
            player.updateInventory();
        }

        Proton.getInstance().getVisibilityHandler().update(player);

        net.frozenorb.foxtrot.listener.LunarClientListener.updateNametag(player);
    }

    public void enableFullVanish(Player player) {
        vanishState.put(player.getUniqueId(), VanishState.INVISIBLE);
        player.setMetadata("invisible", new FixedMetadataValue(Foxtrot.getInstance(), true));

        if (isInModMode(player.getUniqueId())) {
            short data = (short) 5;
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data(data)
                    .name(ChatColor.AQUA + "Vanish Toggle").build());
            player.updateInventory();
        }

        Proton.getInstance().getVisibilityHandler().update(player);

        net.frozenorb.foxtrot.listener.LunarClientListener.updateNametag(player);
    }

    public void setVanish(Player player, VanishState state) {
        switch (state) {
            case VISIBLE:
                disableVanish(player);
                break;
            case STAFF:
                enableVanish(player);
                break;
            case INVISIBLE:
                enableFullVanish(player);
                break;
        }
    }

    public void giveModItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, ItemBuilder.of(Material.COMPASS).name(ChatColor.AQUA + "Compass").build());
        player.getInventory().setItem(1, ItemBuilder.of(Material.BOOK).name(ChatColor.AQUA + "Invsee").build());
        player.getInventory().setItem(3,
                ItemBuilder.of(Material.LEASH).name(ChatColor.AQUA + "Teleport to PvP").build());
        player.getInventory().setItem(4,
                ItemBuilder.of(Material.DIAMOND).name(ChatColor.AQUA + "Random Teleport").build());
        player.getInventory().setItem(5, ItemBuilder.of(Material.ICE).name(ChatColor.AQUA + "Freeze").build());
        player.getInventory().setItem(8,
                ItemBuilder.of(Material.INK_SACK).data((short) 10).name(ChatColor.AQUA + "Vanish Toggle").build());

        player.updateInventory();
    }

}
