package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddLoreCommand {

    @Command(names = {"testparticle"}, permission = "op")
    public static void particle(Player player, @Parameter(name = "type")String type) {
        final ParticleEffect particleEffect = ParticleEffect.valueOf(type.toUpperCase());

        particleEffect.display(0.0f, 0.0f, 0.0f, 0.01f, 1, player.getLocation(), 50);
    }

    @Command(names = {"blockbreakdown"}, permission = "op")
    public static void block(Player player) {
        final Block block = player.getTargetBlock(null,5);

        if (block == null) {
            player.sendMessage(ChatColor.RED + "You are not looking at a block.");
            return;
        }

        player.sendMessage(block.getType().name() + " Breakdown:");
        player.sendMessage("Temperature: " + block.getTemperature());
        player.sendMessage("Biome: " + block.getBiome().name());
        player.sendMessage("Humidity: " + block.getHumidity());
        player.sendMessage("Power: " + block.getBlockPower());
        player.sendMessage("Light from Blocks: " + block.getLightFromBlocks());
        player.sendMessage("Light from Sky: " + block.getLightFromSky());
        player.sendMessage("Light from Level: " + block.getLightLevel());
        player.sendMessage("Block Powered: " + block.isBlockPowered());
        player.sendMessage("Block In-directly Powered: " + block.isBlockIndirectlyPowered());
        player.sendMessage("Block Move Reaction: " + block.getPistonMoveReaction().name());
    }

    @Command(names = {"addlore"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "lore", wildcard = true)String name) {
        final ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must hold something!");
            return;
        }

        if (itemStack.getItemMeta() == null) {
            player.sendMessage(ChatColor.RED + "You may not add a lore to an item with no item meta!");
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();

        lore.add(ChatColor.translateAlternateColorCodes('&', name));

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(ChatColor.GOLD + "Added lore named " + ChatColor.WHITE + name + ChatColor.GOLD + ".");
    }

    @Command(names = {"alt scan"}, permission = "op")
    public static void altScan(Player player) {
        final List<Player> players = new ArrayList<>(Foxtrot.getInstance().getServer().getOnlinePlayers());

        player.sendMessage(ChatColor.GOLD + "Scanning for alts...");

        for (Player onlinePlayer : players) {
            final List<Player> alts = Foxtrot.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.getAddress().getAddress().getHostAddress().equalsIgnoreCase(onlinePlayer.getAddress().getAddress().getHostAddress()) && !it.getName().equalsIgnoreCase(onlinePlayer.getName())).collect(Collectors.toList());

            if (!alts.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', onlinePlayer.getName() + " &6has &f" + alts.size() + " &6alts online!"));
            }
        }
    }

}
