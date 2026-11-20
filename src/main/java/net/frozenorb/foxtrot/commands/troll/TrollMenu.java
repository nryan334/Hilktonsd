package net.frozenorb.foxtrot.commands.troll;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.minecraft.server.v1_7_R4.PacketPlayOutExplosion;
import net.minecraft.server.v1_7_R4.PacketPlayOutGameStateChange;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrollMenu extends Menu {

    private final Player target;

    public TrollMenu(Player target) {
        this.target = target;
        this.setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Trolling: " + target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&c&lGame Crash");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Sends massive packets to crash"));
                desc.add(CC.translate("&7the target's Minecraft client."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (!target.isOnline()) return;
                PacketPlayOutExplosion packet = new PacketPlayOutExplosion(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, Collections.emptyList(), null);
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                player.sendMessage(CC.translate("&aCrashed " + target.getName() + "'s game."));
            }
        });

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&e&lNo Damage");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                boolean toggled = TrollListeners.noDamage.contains(target.getUniqueId());
                desc.add(CC.translate("&7Status: " + (toggled ? "&aEnabled" : "&cDisabled")));
                desc.add(CC.translate("&7Cancels all damage taken by the target."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_CHESTPLATE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (TrollListeners.noDamage.contains(target.getUniqueId())) {
                    TrollListeners.noDamage.remove(target.getUniqueId());
                    player.sendMessage(CC.translate("&cDisabled no-damage for " + target.getName() + "."));
                } else {
                    TrollListeners.noDamage.add(target.getUniqueId());
                    player.sendMessage(CC.translate("&aEnabled no-damage for " + target.getName() + "."));
                }
            }
        });

        buttons.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&b&lFake OP");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Sends the vanilla OP message"));
                desc.add(CC.translate("&7without actually giving permissions."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMMAND;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (!target.isOnline()) return;
                target.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[Server: Opped " + target.getName() + "]");
                player.sendMessage(CC.translate("&aSent Fake OP message to " + target.getName() + "."));
            }
        });

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&d&lSpin");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                boolean toggled = TrollListeners.spinning.contains(target.getUniqueId());
                desc.add(CC.translate("&7Status: " + (toggled ? "&aEnabled" : "&cDisabled")));
                desc.add(CC.translate("&7Spins the target's camera uncontrollably."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.MINECART;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (TrollListeners.spinning.contains(target.getUniqueId())) {
                    TrollListeners.spinning.remove(target.getUniqueId());
                    player.sendMessage(CC.translate("&cDisabled spinning for " + target.getName() + "."));
                } else {
                    TrollListeners.spinning.add(target.getUniqueId());
                    player.sendMessage(CC.translate("&aEnabled spinning for " + target.getName() + "."));
                }
            }
        });

        buttons.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&4&lFake TNT");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Spawns a fake TNT entity near the"));
                desc.add(CC.translate("&7target that explodes visually but"));
                desc.add(CC.translate("&7deals zero damage to blocks/players."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TNT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (!target.isOnline()) return;
                TNTPrimed tnt = (TNTPrimed) target.getWorld().spawnEntity(target.getLocation().add(0, 1, 0), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(40);
                tnt.setMetadata("fake_tnt", new FixedMetadataValue(Foxtrot.getInstance(), true));
                player.sendMessage(CC.translate("&aSpawned Fake TNT on " + target.getName() + "."));
            }
        });

        buttons.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&5&lEnd Credits");
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = new ArrayList<>();
                desc.add(CC.translate("&7Forces the target's client to show"));
                desc.add(CC.translate("&7the Minecraft End Credits screen."));
                return desc;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ENDER_PORTAL_FRAME;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (!target.isOnline()) return;
                PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(4, 0f);
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                player.sendMessage(CC.translate("&aSent End Credits to " + target.getName() + "."));
            }
        });

        return buttons;
    }
}
