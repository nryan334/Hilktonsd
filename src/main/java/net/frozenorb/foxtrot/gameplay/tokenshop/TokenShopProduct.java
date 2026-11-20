package net.frozenorb.foxtrot.gameplay.tokenshop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TokenShopProduct {
    private String id;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private Material material = Material.STONE;
    private int damage = 0;
    private int slot = 0;
    private int cost = 5;
    private int promoPrice = -1;
    private List<String> commands = new ArrayList<>();
}
