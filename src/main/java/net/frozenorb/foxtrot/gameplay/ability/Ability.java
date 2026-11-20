package net.frozenorb.foxtrot.gameplay.ability;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public abstract class Ability implements Listener {

    public ItemStack hassanStack;
    public String fullDescription;

    public Ability() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public ItemStack getItem() {
        ItemStack baseItem = hassanStack;

        if (baseItem == null) {
            baseItem = ItemBuilder.of(this.getMaterial())
                    .name(this.getDisplayName() == null ? this.getName() : this.getDisplayName())
                    .setLore(this.getLore())
                    .build();
        }

        final List<String> finalLore = new ArrayList<>(baseItem.getItemMeta().getLore());

        if (Foxtrot.getInstance().getMapHandler() != null
                && Foxtrot.getInstance().getMapHandler().getAbilityHandler() != null) {
            if (Foxtrot.getInstance().getCrateHandler() != null) {
                List<String> crateNames = new ArrayList<>();
                for (net.frozenorb.foxtrot.gameplay.loot.crate.Crate crate : Foxtrot.getInstance().getCrateHandler()
                        .getCrates()) {
                    boolean containsAbility = false;
                    for (net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem item : crate.getItems()) {
                        if (item.getItemStack() != null && this.isSimilar(item.getItemStack())) {
                            containsAbility = true;
                            break;
                        }
                    }
                    if (containsAbility) {
                        crateNames.add(crate.getDisplayName());
                    }
                }

                if (!crateNames.isEmpty()) {
                    for (String crateName : crateNames) {
                        String categoryLore = ChatColor.translateAlternateColorCodes('&',
                                "&fCan be found in the &e" + crateName + "&f!");
                        boolean alreadyHas = false;
                        for (String line : finalLore) {
                            if (ChatColor.stripColor(line).equals(ChatColor.stripColor(categoryLore))) {
                                alreadyHas = true;
                                break;
                            }
                        }

                        if (!alreadyHas) {
                            if (!finalLore.isEmpty() && !finalLore.get(finalLore.size() - 1).isEmpty()) {
                                finalLore.add("");
                            }
                            finalLore.add(categoryLore);
                        }
                    }
                }
            }
        }

        return ItemBuilder.copyOf(baseItem)
                .setLore(finalLore)
                .build();
    }

    public abstract String getName();

    public abstract Material getMaterial();

    public abstract String getDisplayName();

    public abstract List<String> getLore();

    public abstract Boolean isAllowedAtLocation(Location location);

    public abstract long getCooldown();

    public abstract Category getCategory();

    public abstract String getDescription();

    public boolean inPartnerPackage() {
        return false;
    }

    public boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        String displayName = this.getDisplayName() == null ? this.getName() : this.getDisplayName();
        if (!ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())
                .equalsIgnoreCase(ChatColor.stripColor(displayName))) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        List<String> abilityLore = this.getLore();
        if (abilityLore == null || abilityLore.isEmpty()) {
            return true;
        }
        return ChatColor.stripColor(itemStack.getItemMeta().getLore().get(0))
                .equalsIgnoreCase(ChatColor.stripColor(abilityLore.get(0)));
    }

    public void removeCooldown(Player player) {
        AbilityHandler.getCooldown().remove(player.getUniqueId(), this);
    }

    public void applyCooldown(Player player) {
        String desc = this.fullDescription != null ? this.fullDescription : this.getDescription();

        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7You have activated the &f" + this.getDisplayName() + "&7!"));
        if (desc != null && !desc.isEmpty()) {
            player.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', desc));
        }
        player.sendMessage("");

        if (player.hasMetadata("DEBUG")) {
            player.sendMessage(ChatColor.RED + "Skipped cooldown as you had Debug mode enabled!");
            return;
        }

        Foxtrot.getInstance().getMapHandler().getAbilityHandler().applyCooldown(this, player);
        this.fullDescription = null;
    }

    public void applyCooldown(Team team, Player target) {
        String desc = this.fullDescription != null ? this.fullDescription : this.getDescription();

        team.sendMessage(ChatColor.translateAlternateColorCodes('&',
                target.getName() + " &chas used &f" + this.getDisplayName()
                        + " &cand put your entire team on cooldown for &l"
                        + TimeUtils.formatIntoDetailedString((int) (this.getCooldown() / 1000))));

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7You have activated the &f" + this.getDisplayName() + "&7!"));
        if (desc != null && !desc.isEmpty()) {
            target.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', desc));
        }
        target.sendMessage("");

        for (Player onlineMember : team.getOnlineMembers()) {
            if (onlineMember.hasMetadata("DEBUG")) {
                onlineMember.sendMessage(ChatColor.RED + "Skipped cooldown as you had Debug mode enabled!");
                continue;
            }

            Foxtrot.getInstance().getMapHandler().getAbilityHandler().applyCooldown(this, onlineMember);
        }
        this.fullDescription = null;
    }

    public void applyCooldown(Team team, Player target, long time) {
        String desc = this.fullDescription != null ? this.fullDescription : this.getDescription();

        team.sendMessage(ChatColor.translateAlternateColorCodes('&',
                target.getName() + " &chas used &f" + this.getDisplayName()
                        + " &cand put your entire team on cooldown for &l"
                        + TimeUtils.formatIntoDetailedString((int) (time / 1000))));

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7You have activated the &f" + this.getDisplayName() + "&7!"));
        if (desc != null && !desc.isEmpty()) {
            target.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', desc));
        }
        target.sendMessage("");

        for (Player onlineMember : team.getOnlineMembers()) {
            if (onlineMember.hasMetadata("DEBUG")) {
                onlineMember.sendMessage(ChatColor.RED + "Skipped cooldown as you had Debug mode enabled!");
                continue;
            }

            Foxtrot.getInstance().getMapHandler().getAbilityHandler().applyCooldown(this, onlineMember, time);
        }
        this.fullDescription = null;
    }

    public boolean hasCooldown(Player player) {
        return this.hasCooldown(player, true);
    }

    public boolean hasCooldown(Player player, boolean sendMessage) {
        final AbilityHandler abilityHandler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();

        if (abilityHandler.getGlobalCooldowns().containsKey(player.getUniqueId())) {
            long remaining = abilityHandler.getGlobalCooldowns().get(player.getUniqueId()) - System.currentTimeMillis();
            if (remaining > 0) {
                if (sendMessage) {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lAbility Items"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&7You cannot use any ability items for another &b"
                                    + String.format("%.1f", (double) remaining / 1000.0) + "s&7."));
                    player.sendMessage("");
                }
                return true;
            } else {
                abilityHandler.getGlobalCooldowns().remove(player.getUniqueId());
            }
        }

        final long current = abilityHandler.getRemaining(this, player);

        if (current <= 0) {
            return false;
        }

        if (sendMessage) {
            player.sendMessage(ChatColor.RED + "You cannot use the " + this.getDisplayName() + ChatColor.RED
                    + " for another " + ChatColor.BOLD + TimeUtils.formatIntoDetailedString((int) (current / 1000))
                    + ChatColor.RED + ".");
        }

        return true;
    }

    public long getRemaining(Player player) {
        return Foxtrot.getInstance().getMapHandler().getAbilityHandler().getRemaining(this, player);
    }

    public void takeItem(Player player) {
        if (player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }
    }

    public Boolean DTR(Location location) {
        return isAllowedAtLocation(location);
    }

    private final List<String> metadata = new ArrayList<>();

    public List<String> getMetadata() {
        return this.metadata;
    }

    public boolean hasMetadata(String tag) {
        return this.metadata.stream().anyMatch(tag::equalsIgnoreCase);
    }

    public void addMetadata(String tag) {
        if (!hasMetadata(tag)) {
            this.metadata.add(tag.toUpperCase());
        }
    }

    public void removeMetadata(String tag) {
        this.metadata.removeIf(t -> t.equalsIgnoreCase(tag));
    }
}
