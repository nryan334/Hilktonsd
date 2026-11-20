package net.frozenorb.foxtrot.server.voucher.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.VoucherTemplate;
import net.frozenorb.foxtrot.server.voucher.menu.VoucherEditorMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VoucherCommand {

    @Command(names = {"voucher create"}, permission = "foxtrot.command.voucher.create")
    public static void create(Player player, @Parameter(name = "id") String id) {
        if (Foxtrot.getInstance().getVoucherHandler().fromName(id).isPresent()) {
            player.sendMessage(ChatColor.RED + "A voucher with that ID already exists.");
            return;
        }

        VoucherTemplate template = new VoucherTemplate(id);
        template.save();
        Foxtrot.getInstance().getVoucherHandler().getTemplates().add(template);

        player.sendMessage(ChatColor.GREEN + "Created new voucher template '" + id + "'.");
        new VoucherEditorMenu().openMenu(player);
    }

    @Command(names = {"voucher delete"}, permission = "foxtrot.command.voucher.delete")
    public static void delete(CommandSender sender, @Parameter(name = "template") VoucherTemplate template) {
        template.delete();
        Foxtrot.getInstance().getVoucherHandler().getTemplates().remove(template);
        sender.sendMessage(ChatColor.GREEN + "Deleted voucher template '" + template.getId() + "'.");
    }

    @Command(names = {"voucher editor", "vouchers editor"}, permission = "foxtrot.command.voucher.editor")
    public static void editor(Player player) {
        new VoucherEditorMenu().openMenu(player);
    }

    @Command(names = {"voucher give", "vouchers give"}, permission = "foxtrot.command.voucher.give")
    public static void give(CommandSender sender, @Parameter(name = "target") Player target, @Parameter(name = "template") VoucherTemplate template, @Parameter(name = "amount", defaultValue = "1") int amount) {
        ItemStack item = createPhysicalVoucher(template);
        item.setAmount(amount);

        target.getInventory().addItem(item);
        target.updateInventory();

        sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " " + amount + "x " + template.getId() + " voucher(s).");
    }
    
    @Command(names = {"voucher giveaway"}, permission = "foxtrot.command.voucher.giveaway")
    public static void giveaway(CommandSender sender, @Parameter(name = "template") VoucherTemplate template, @Parameter(name = "amount", defaultValue = "1") int amount) {
        ItemStack item = createPhysicalVoucher(template);
        item.setAmount(amount);

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.getInventory().addItem(item);
            onlinePlayer.updateInventory();
            onlinePlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + "x " + ChatColor.translateAlternateColorCodes('&', template.getDisplayName()) + ChatColor.GREEN + "!");
        }

        sender.sendMessage(ChatColor.GREEN + "Gave all online players " + amount + "x " + template.getId() + " voucher(s).");
    }

    public static ItemStack createPhysicalVoucher(VoucherTemplate template) {
        Material mat = Material.matchMaterial(template.getMaterial());
        if (mat == null) mat = Material.PAPER;
        ItemBuilder builder = ItemBuilder.of(mat).data(template.getData());
        builder.name(ChatColor.translateAlternateColorCodes('&', template.getDisplayName()));

        List<String> lore = new ArrayList<>();
        if (template.getLore() != null && !template.getLore().isEmpty()) {
            for (String line : template.getLore()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        
        lore.add("");
        lore.add(ChatColor.GRAY + "Right-Click to redeem.");
        lore.add(ChatColor.BLACK + "HIDDEN:" + template.getId());

        builder.setLore(lore);
        return builder.build();
    }

    public static void spawnFireworks(org.bukkit.Location location, int amount, int power, org.bukkit.Color color, org.bukkit.FireworkEffect.Type fireworkEffect) {
        org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.FIREWORK);
        org.bukkit.inventory.meta.FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(power);
        fireworkMeta.addEffect(org.bukkit.FireworkEffect.builder().with(fireworkEffect).with(org.bukkit.FireworkEffect.Type.BURST).withColor(color).flicker(true).build());
        firework.setFireworkMeta(fireworkMeta);
        for (int i = 0; i < amount; i++) {
            org.bukkit.entity.Firework entityFirework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.FIREWORK);
            entityFirework.setFireworkMeta(fireworkMeta);
        }
    }
}
