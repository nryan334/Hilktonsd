package net.frozenorb.foxtrot.gameplay.loot.airdrop.command;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.universe.UniverseAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDrop;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDropHandler;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.menu.AirDropEditorMenu;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class AirDropCommand {

    @Command(names = { "airdrops give", "airdrop give" }, permission = "command.airdrops.give", hidden = true)
    public static void execute(CommandSender sender, @Parameter(name = "target") Player target,
            @Parameter(name = "type") String type, @Parameter(name = "amount") int amount) {
        AirDrop airDrop = Foxtrot.getInstance().getAirDropHandler().getAirDrop(type).orElse(null);
        if (airDrop == null) {
            sender.sendMessage(ChatColor.RED + "Airdrop type not found.");
            return;
        }

        if (amount > 64 && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't give more than 64 Airdrops without OP!");
            return;
        }

        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have been given a " + airDrop.getDisplayName() + "&c!"));
        sender.sendMessage(ChatColor
                .translate("&cGave &f" + target.getName() + " " + amount + "x " + airDrop.getDisplayName() + "&6."));

        if (!(sender instanceof ConsoleCommandSender)) {
            Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                    NeutronConstants.MANAGER_PERMISSION,
                    ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', 
                            LIGHT_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f"
                                    + (sender instanceof Player ? ((Player) sender).getDisplayName()
                                            : DARK_RED + BOLD.toString() + "Command Block")
                                    + " &7has given &f" + target.getName() + "'s &b&l" + amount + "x " + airDrop.getId()
                                    + " Airdrops&7."))));
        }

        ItemStack itemStack = airDrop.getItemStack().clone();
        itemStack.setAmount(amount);

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItem(target.getLocation(), itemStack);
        } else {
            target.getInventory().addItem(itemStack);
            target.updateInventory();
        }
    }

    @Command(names = { "airdrops editor" }, permission = "op")
    public static void editor(Player player) {
        new AirDropEditorMenu().openMenu(player);
    }

    @Command(names = { "airdrops create" }, permission = "op")
    public static void create(Player player, @Parameter(name = "id") String id) {
        if (Foxtrot.getInstance().getAirDropHandler().getAirDrop(id).isPresent()) {
            player.sendMessage(ChatColor.RED + "Airdrop with that ID already exists.");
            return;
        }

        AirDrop airDrop = new AirDrop(id);
        Foxtrot.getInstance().getAirDropHandler().getAirDrops().put(id.toLowerCase(), airDrop);
        Foxtrot.getInstance().getAirDropHandler().saveAirDrop(airDrop);
        player.sendMessage(ChatColor.GREEN + "Created airdrop: " + id);
    }

    @Command(names = { "airdrops reload" }, permission = "op", hidden = true)
    public static void reload(Player player) {
        Foxtrot.getInstance().getAirDropHandler().loadLootTable();
        player.sendMessage(ChatColor.GREEN + "Reloaded Airdrops.");
    }

    // Legacy
    public static boolean isAirdrop(ItemStack itemStack) {
        return Foxtrot.getInstance().getAirDropHandler().getAirDropByItemStack(itemStack).isPresent();
    }
}
