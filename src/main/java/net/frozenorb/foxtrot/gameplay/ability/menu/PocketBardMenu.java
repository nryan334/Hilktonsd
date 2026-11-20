package net.frozenorb.foxtrot.gameplay.ability.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.PocketBard;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PocketBardMenu extends Menu {

    private PocketBard ability;

    @Override
    public String getTitle(Player player) {
        return "Select a Bard Effect";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10,
                new BardEffectButton(PotionEffectType.INCREASE_DAMAGE, 1, "Strength II", Material.BLAZE_POWDER));
        buttons.put(12,
                new BardEffectButton(PotionEffectType.DAMAGE_RESISTANCE, 2, "Resistance III", Material.IRON_INGOT));
        buttons.put(14,
                new BardEffectButton(PotionEffectType.REGENERATION, 2, "Regeneration III", Material.GHAST_TEAR));
        buttons.put(16, new BardEffectButton(PotionEffectType.SPEED, 2, "Speed III", Material.SUGAR));

        return buttons;
    }

    private class BardEffectButton extends Button {
        private final PotionEffectType effect;
        private final int amplifier;
        private final String name;
        private final Material material;

        public BardEffectButton(PotionEffectType effect, int amplifier, String name, Material material) {
            this.effect = effect;
            this.amplifier = amplifier;
            this.name = name;
            this.material = material;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + name;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return material;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (!ability.isSimilar(player.getItemInHand())) {
                player.sendMessage(ChatColor.RED + "You must be holding the Pocket Bard to use it!");
                player.closeInventory();
                return;
            }

            if (!ability.isAllowedAtLocation(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "You cannot use this here!");
                player.closeInventory();
                return;
            }

            if (player.getItemInHand().getAmount() <= 1) {
                player.setItemInHand(null);
            } else {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }

            player.closeInventory();
            applyEffect(player, effect, amplifier, name);
            ability.applyCooldown(player);
        }
    }

    private void applyEffect(Player player, PotionEffectType effect, int amplifier, String effectName) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        PotionEffect potionEffect = new PotionEffect(effect, 5 * 20, amplifier);

        player.addPotionEffect(potionEffect, true);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage(ChatColor.GREEN + "You have activated " + ChatColor.BOLD + effectName + ChatColor.GREEN
                + " for your team!");

        if (team != null) {
            for (Player member : team.getOnlineMembers()) {
                if (member.getLocation().distanceSquared(player.getLocation()) <= 400) {
                    member.addPotionEffect(potionEffect, true);
                    if (!member.equals(player)) {
                        member.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " activated "
                                + ChatColor.BOLD + effectName + ChatColor.YELLOW + " via Pocket Bard!");
                    }
                }
            }
        }
    }
}
