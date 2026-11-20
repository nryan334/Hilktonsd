package net.frozenorb.foxtrot.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.util.UUID;

public class SkullUtils {

    public static ItemStack createCustomSkull(String base64) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (base64 == null || base64.isEmpty()) return head;

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "CustomSkull");
            profile.getProperties().put("textures", new Property("textures", base64));

            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }
}
