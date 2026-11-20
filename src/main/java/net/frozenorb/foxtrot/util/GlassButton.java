package net.frozenorb.foxtrot.util;

import cc.fyre.proton.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class GlassButton extends Button {

    private final byte damage;

    public GlassButton(int damage) {
        this.damage = (byte) damage;
    }

    @Override
    public String getName(Player player) {
        return " ";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    @Override
    public byte getDamageValue(Player player) {
        return damage;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
    }
}
