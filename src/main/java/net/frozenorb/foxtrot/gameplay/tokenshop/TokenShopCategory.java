package net.frozenorb.foxtrot.gameplay.tokenshop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TokenShopCategory {
    private String id;
    private String displayName;
    private Material material = Material.CHEST;
    private int damage = 0;
    private int slot = 0;
    private boolean gkits = false;
    private boolean keys = false;
    private List<TokenShopProduct> products = new ArrayList<>();
}
