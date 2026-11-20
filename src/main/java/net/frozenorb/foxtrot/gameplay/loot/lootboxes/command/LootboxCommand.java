package net.frozenorb.foxtrot.gameplay.loot.lootboxes.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.Lootbox;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.LootboxItem;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu.LootboxEditorMenu;
import net.frozenorb.foxtrot.gameplay.loot.lootboxes.menu.LootboxListMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootboxCommand {

    @Command(names = { "lootbox", "lootbox help" }, permission = "op")
    public static void help(Player sender) {
        sender.sendMessage(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------------------");
        sender.sendMessage(ChatColor.RED + "/lootbox list");
        sender.sendMessage(ChatColor.RED + "/lootbox create <id>");
        sender.sendMessage(ChatColor.RED + "/lootbox delete <id>");
        sender.sendMessage(ChatColor.RED + "/lootbox edit <id>");
        sender.sendMessage(ChatColor.RED + "/lootbox give <player> <id> <amount>");
        sender.sendMessage(ChatColor.RED + "/lootbox additem <id> <chance> <type> [cmd]");
        sender.sendMessage(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------------------");
    }

    @Command(names = { "lootbox list" }, permission = "op")
    public static void list(Player sender) {
        new LootboxListMenu().openMenu(sender);
    }

    @Command(names = { "lootbox create" }, permission = "op")
    public static void create(Player sender, @Parameter(name = "id") String id) {
        if (Foxtrot.getInstance().getLootboxHandler().getLootbox(id) != null) {
            sender.sendMessage(ChatColor.RED + "Lootbox already exists!");
            return;
        }

        Lootbox lootbox = new Lootbox(id);
        Foxtrot.getInstance().getLootboxHandler().getLootboxes().put(id.toLowerCase(), lootbox);
        Foxtrot.getInstance().getLootboxHandler().save(lootbox);
        sender.sendMessage(ChatColor.GREEN + "Lootbox created!");
    }

    @Command(names = { "lootbox delete" }, permission = "op")
    public static void delete(Player sender, @Parameter(name = "id") String id) {
        Lootbox lootbox = Foxtrot.getInstance().getLootboxHandler().getLootbox(id);
        if (lootbox == null) {
            sender.sendMessage(ChatColor.RED + "Lootbox not found!");
            return;
        }

        Foxtrot.getInstance().getLootboxHandler().delete(lootbox);
        sender.sendMessage(ChatColor.RED + "Lootbox deleted!");
    }

    @Command(names = { "lootbox give" }, permission = "op")
    public static void give(CommandSender sender, @Parameter(name = "player") Player target,
            @Parameter(name = "id") String id, @Parameter(name = "amount", defaultValue = "1") int amount) {
        Lootbox lootbox = Foxtrot.getInstance().getLootboxHandler().getLootbox(id);
        if (lootbox == null) {
            sender.sendMessage(ChatColor.RED + "Lootbox not found!");
            return;
        }

        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(lootbox.getItemStack());
        }
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " " + id + " lootboxes to " + target.getName());
    }

    @Command(names = { "lootbox edit" }, permission = "op")
    public static void edit(Player sender, @Parameter(name = "id") String id) {
        Lootbox lootbox = Foxtrot.getInstance().getLootboxHandler().getLootbox(id);
        if (lootbox == null) {
            sender.sendMessage(ChatColor.RED + "Lootbox not found!");
            return;
        }

        new LootboxEditorMenu(lootbox).openMenu(sender);
    }

    @Command(names = { "lootbox additem" }, permission = "op")
    public static void addItem(Player sender, @Parameter(name = "id") String id,
            @Parameter(name = "chance") double chance, @Parameter(name = "executionType") String executionType,
            @Parameter(name = "command", defaultValue = "none", wildcard = true) String command) {
        Lootbox lootbox = Foxtrot.getInstance().getLootboxHandler().getLootbox(id);
        if (lootbox == null) {
            sender.sendMessage(ChatColor.RED + "Lootbox not found!");
            return;
        }

        ItemStack itemInHand = sender.getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You must hold an item!");
            return;
        }

        LootboxItem.ExecutionType type;
        try {
            type = LootboxItem.ExecutionType.valueOf(executionType.toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid type! (CONSOLE, PLAYER, ITEM_ONLY)");
            return;
        }

        List<String> commands = new ArrayList<>();
        if (!command.equalsIgnoreCase("none")) {
            commands.add(command);
        }

        LootboxItem item = new LootboxItem(itemInHand.clone(), chance, commands, type, true);
        lootbox.getItems().add(item);
        Foxtrot.getInstance().getLootboxHandler().save(lootbox);
        sender.sendMessage(ChatColor.GREEN + "Item added to " + id + "!");
    }
}
