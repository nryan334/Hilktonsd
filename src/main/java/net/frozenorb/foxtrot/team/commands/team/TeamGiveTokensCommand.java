package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamGiveTokensCommand {
    @Command(names = { "givebasetokens" }, permission = "hcteams.givebasetokens")
    public static void basetokens(CommandSender sender, @Parameter(name = "team") final Team team,
            @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }

        team.setBaseTokens(team.getBaseTokens() + amount);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&eYou have successfully added &b" + amount + "&e base tokens to &a" + team.getName() + "&e."));
    }

    @Command(names = { "givefallertokens" }, permission = "hcteams.givefallertokens")
    public static void fallertokens(CommandSender sender, @Parameter(name = "team") final Team team,
            @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }

        team.setFallTrapTokens(team.getFallTrapTokens() + amount);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&eYou have successfully added &b" + amount + "&e faller tokens to &a" + team.getName() + "&e."));
    }

    @Command(names = { "givebasetokenitem" }, permission = "hilton.givebasetokenitem")
    public static void giveBaseTokenItem(CommandSender sender, @Parameter(name = "player") Player target,
            @Parameter(name = "amount") int amount) {
        org.bukkit.inventory.ItemStack item = cc.fyre.proton.util.ItemBuilder.of(org.bukkit.Material.STAINED_GLASS)
                .data((short) 14)
                .name(ChatColor.translateAlternateColorCodes('&', "&b&lBase Token"))
                .setLore(java.util.Arrays.asList(
                        ChatColor.GRAY + "Right-Click to redeem a Base Token",
                        ChatColor.GRAY + "for your team. You must be in a faction."))
                .build();
        item.setAmount(amount);
        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Base Tokens to " + target.getName() + ".");
    }

    @Command(names = { "givefallertokenitem" }, permission = "hilton.givefallertokenitem")
    public static void giveFallerTokenItem(CommandSender sender, @Parameter(name = "player") Player target,
            @Parameter(name = "amount") int amount) {
        org.bukkit.inventory.ItemStack item = cc.fyre.proton.util.ItemBuilder.of(org.bukkit.Material.HOPPER)
                .name(ChatColor.translateAlternateColorCodes('&', "&4&lFaller Token"))
                .setLore(java.util.Arrays.asList(
                        ChatColor.GRAY + "Right-Click to redeem a Faller Token",
                        ChatColor.GRAY + "for your team. You must be in a faction."))
                .build();
        item.setAmount(amount);
        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Faller Tokens to " + target.getName() + ".");
    }
}
