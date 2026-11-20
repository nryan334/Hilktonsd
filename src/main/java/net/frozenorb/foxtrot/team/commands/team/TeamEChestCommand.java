package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class TeamEChestCommand {

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                Player player = (Player) event.getPlayer();
                if (player.hasMetadata("editing_vault_team") && player.hasMetadata("editing_vault_id")) {
                    String teamName = player.getMetadata("editing_vault_team").get(0).asString();
                    int vaultId = player.getMetadata("editing_vault_id").get(0).asInt();

                    Team team = Foxtrot.getInstance().getTeamHandler().getTeam(teamName);
                    if (team != null) {
                        try {
                            String base64 = itemStackArrayToBase64(event.getInventory().getContents());
                            team.getVaults()[vaultId] = base64;
                            team.flagForSave();
                            player.sendMessage(ChatColor.GREEN + "Faction Vault #" + (vaultId + 1)
                                    + " has been successfully saved.");
                        } catch (Exception e) {
                            player.sendMessage(ChatColor.RED + "Failed to save Faction Vault #" + (vaultId + 1) + "!");
                            e.printStackTrace();
                        }
                    }
                    player.removeMetadata("editing_vault_team", Foxtrot.getInstance());
                    player.removeMetadata("editing_vault_id", Foxtrot.getInstance());
                }
            }
        }, Foxtrot.getInstance());
    }

    @Command(names = { "team echest", "t echest", "f echest", "faction echest", "fac echest", "team vault", "t vault",
            "f vault", "faction vault", "fac vault" }, permission = "")
    public static void teamEChest(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use faction vaults!");
            return;
        }

        if (team.getStrikes() >= 3) {
            player.sendMessage(ChatColor.RED + "Your faction is disqualified and cannot access faction vaults!");
            return;
        }

        new TeamEChestMenu(team).openMenu(player);
    }

    public static class TeamEChestMenu extends Menu {
        private final Team team;

        public TeamEChestMenu(Team team) {
            this.team = team;
        }

        @Override
        public String getTitle(Player player) {
            return ChatColor.BLUE + "Faction Vaults (" + team.getName() + ")";
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();

            for (int i = 0; i < 9; i++) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15));
            }

            for (int i = 0; i < 5; i++) {
                final int vaultId = i;
                buttons.put(2 + i, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.GOLD + ChatColor.BOLD.toString() + "Faction Vault #" + (vaultId + 1);
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Click to open this virtual shared vault.");
                        lore.add(ChatColor.GRAY + "All members of your faction share this inventory.");
                        return lore;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return Material.ENDER_CHEST;
                    }

                    @Override
                    public void clicked(Player player, int slot, org.bukkit.event.inventory.ClickType clickType) {
                        player.closeInventory();

                        Inventory inventory = Bukkit.createInventory(null, 54,
                                ChatColor.LIGHT_PURPLE + "Faction Vault #" + (vaultId + 1));
                        try {
                            ItemStack[] contents = itemStackArrayFromBase64(team.getVaults()[vaultId]);
                            inventory.setContents(contents);
                        } catch (IOException e) {
                            player.sendMessage(ChatColor.RED + "Error loading vault contents.");
                            e.printStackTrace();
                        }

                        player.setMetadata("editing_vault_team",
                                new FixedMetadataValue(Foxtrot.getInstance(), team.getName()));
                        player.setMetadata("editing_vault_id", new FixedMetadataValue(Foxtrot.getInstance(), vaultId));

                        player.openInventory(inventory);
                        player.sendMessage(ChatColor.YELLOW + "Opening " + ChatColor.GOLD + "Faction Vault #"
                                + (vaultId + 1) + ChatColor.YELLOW + "...");
                    }
                });
            }

            return buttons;
        }
    }

    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            if (data == null || data.trim().isEmpty() || data.trim().equalsIgnoreCase("null")) {
                return new ItemStack[54];
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            return new ItemStack[54];
        }
    }
}
