package net.frozenorb.foxtrot.server.voucher.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.rank.Rank;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VoucherListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT"))
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null)
            return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return;

        String templateId = null;
        for (String line : item.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(line);
            if (stripped.startsWith("HIDDEN:")) {
                templateId = stripped.replace("HIDDEN:", "");
                break;
            }
        }

        if (templateId == null)
            return;

        VoucherTemplate template = Foxtrot.getInstance().getVoucherHandler().fromName(templateId).orElse(null);

        if (template == null) {
            player.sendMessage(ChatColor.RED + "This voucher template no longer exists.");
            return;
        }

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }
        player.updateInventory();

        String value = template.getValue();
        String duration = template.getDuration();
        String category = template.getCategory().toUpperCase();

        String rewardName = value;
        if (category.equals("COMMAND")) rewardName = "a mystery reward";
        if (category.equals("MONEY") || category.equals("STORE")) rewardName = "$" + value;
        if (category.equals("LIVES")) rewardName = value + " lives";

        for (String msg : new String[]{
                "",
                "&4&lVoucher Redeemed",
                "&b" + player.getName() + " &7has redeemed " + template.getDisplayName() + " &7and won &b" + rewardName + "&7!",
                "&7Get these perks and more at &bstore.strivepvp.top",
                ""
        }) {
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }

        if (category.equals("RANK")) {
            Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
            if (profile != null) {
                Rank rank = Neutron.getInstance().getRankHandler().fromName(value);
                if (rank != null) {
                    long durationLong = duration.equalsIgnoreCase("perm") ? Long.MAX_VALUE
                            : cc.fyre.proton.util.TimeUtils.parseTime(duration) * 1000L;

                    Grant grant = new Grant(rank, player.getUniqueId(), durationLong, "Voucher Redeemed");
                    profile.getGrants().add(grant);
                    profile.recalculateGrants();
                    profile.save();
                    player.sendMessage(ChatColor.GREEN + "The rank " + rank.getColor() + rank.getName()
                            + ChatColor.GREEN + " has been applied to your account.");
                } else {
                    player.sendMessage(
                            ChatColor.RED + "Error: The rank associated with this voucher no longer exists.");
                }
            }
        } else if (category.equals("COMMAND")) {
            String cmd = value.replace("_", " ").replace("{player}", player.getName());
            Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(),
                    cmd);
        } else if (category.equals("MONEY")) {
            try {
                double amount = Double.parseDouble(value);
                Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), amount);
                player.sendMessage(ChatColor.GREEN + "$" + amount + " has been added to your balance!");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Error: Invalid money amount in voucher template.");
            }
        } else if (category.equals("LIVES")) {
            try {
                int livesAmount = Integer.parseInt(value);
                int currentLives = Foxtrot.getInstance().getFriendLivesMap().getLives(player.getUniqueId());
                Foxtrot.getInstance().getFriendLivesMap().setLives(player.getUniqueId(), currentLives + livesAmount);
                player.sendMessage(ChatColor.GREEN + "" + livesAmount + " lives have been added to your account!");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Error: Invalid lives amount in voucher template.");
            }
        } else if (category.equals("STORE")) {
            try {
                double amount = Double.parseDouble(value);
                Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
                    String code = net.frozenorb.foxtrot.util.TebexAPI.createGiftCard(amount, "Voucher Redeemed by " + player.getName());

                    if (code != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lStore Voucher Redeemed!"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aValue: &f$" + amount));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCode: &f" + code));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7(This code can be used at checkout on our store)"));
                    } else {
                        player.sendMessage(ChatColor.RED + "There was an error generating your giftcard. Please check if the Tebex Secret is set in config.yml or contact an admin.");
                    }
                });
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Error: Invalid money amount in voucher template.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Error: Unhandled voucher category '" + category + "'.");
        }
    }
}
