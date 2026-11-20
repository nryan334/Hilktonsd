package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeathbanKitCommand {

    @Command(names = {"deathban gkit apply"}, permission = "")
    public static void deathbanKitApply(Player player) {
        if (!Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
            player.sendMessage(ChatColor.RED + "You can only use this command in the deathban arena.");
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);

        ItemStack pearls = new ItemStack(Material.ENDER_PEARL, 16);
        ItemStack beef = new ItemStack(Material.COOKED_BEEF, 64);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, pearls);
        player.getInventory().setItem(8, beef);

        ItemStack pot = new ItemStack(Material.POTION, 1, (short) 16421);
        while (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(pot.clone());
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

        player.updateInventory();
        player.sendMessage(ChatColor.GREEN + "You have equipped the Deathban kit!");
    }
}
