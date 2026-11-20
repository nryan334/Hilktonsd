package net.frozenorb.foxtrot.gameplay.abilitytrader;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TraderCommand {

    @Command(names = { "trader spawn" }, permission = "foxtrot.command.trader")
    public static void traderSpawn(Player player) {
        AbilityTraderHandler handler = Foxtrot.getInstance().getMapHandler().getAbilityTraderHandler();
        handler.spawnNPC();
        player.sendMessage(ChatColor.GREEN + "Forced a spawn of the Ability Trader NPC.");
    }

    @Command(names = { "trader despawn" }, permission = "foxtrot.command.trader")
    public static void traderDespawn(Player player) {
        AbilityTraderHandler handler = Foxtrot.getInstance().getMapHandler().getAbilityTraderHandler();
        handler.despawnNPC();
        player.sendMessage(ChatColor.GREEN + "Despawned all Ability Trader NPCs.");
    }

    @Command(names = { "trader tp", "trader teleport" }, permission = "foxtrot.command.trader")
    public static void traderTp(Player player) {
        AbilityTraderHandler handler = Foxtrot.getInstance().getMapHandler().getAbilityTraderHandler();
        if (handler.getActiveNPC() == null || !handler.getActiveNPC().isValid()) {
            player.sendMessage(ChatColor.RED + "There is currently no active Ability Trader NPC spawned!");
            return;
        }

        player.teleport(handler.getActiveNPC().getLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported you to the Ability Trader NPC.");
    }

    @Command(names = { "trader info" }, permission = "")
    public static void traderInfo(Player player) {
        AbilityTraderHandler handler = Foxtrot.getInstance().getMapHandler().getAbilityTraderHandler();
        boolean spawned = handler != null && handler.getActiveNPC() != null && handler.getActiveNPC().isValid();

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lAbility Trader"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7The Ability Trader is an NPC where you can exchange your unwanted abilities"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7and $5,000 for a specific ability you want. Find him in the Warzone."));
        player.sendMessage("");

        if (spawned) {
            org.bukkit.Location loc = handler.getActiveNPC().getLocation();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f┃ Status: &cSpawned"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&f┃ Location: &c" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f┃ Status: &cNot Spawned"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f┃ Location: &cNot Defined."));
        }
    }
}
