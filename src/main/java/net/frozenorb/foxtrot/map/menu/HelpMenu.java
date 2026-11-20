package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelpMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', "&8Network Help Menu");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || (i + 1) % 9 == 0) {
                toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
            }
        }

        toReturn.put(10, new HelpButton(
                "&c&lVote Rewards",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQxZmJlYTljMmQxOTA0MGU1NjdmMzg3YWI0NmIyZjhhM2ExZGE4ZWVjOWQzOTllMmU0YWRjZjA1YWRhOGEyYSJ9fX0=",
                Arrays.asList(
                        "&7Support our growth and get rewards!",
                        "",
                        " &4&l» &fRewards: &b3x Basic Keys",
                        " &4&l» &fStreaks: &eBonus Rewards every 7 days",
                        "",
                        "&aClick to open voting links!"),
                "/vote"));

        toReturn.put(12, new HelpButton(
                "&c&lStrive Store",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQxZmJlYTljMmQxOTA0MGU1NjdmMzg3YWI0NmIyZjhhM2ExZGE4ZWVjOWQzOTllMmU0YWRjZjA1YWRhOGEyYSJ9fX0=",
                Arrays.asList(
                        "&7Support the server financially.",
                        "",
                        " &4&l» &fOfficial: &estore.strivepvp.top",
                        " &4&l» &fItems: &7Ranks, Keys, Lootboxes",
                        "",
                        "&aClick to visit the store!"),
                "/buy"));

        toReturn.put(14, new HelpButton(
                "&9&lCommunity Discord",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3M2MxMmJmZmI1MjUxYTBiODhkNWFlNzVjNzI0N2NiMzlhNzVmZjFhODFjYmU0YzhhMzliMzExZGRlZGEifX19",
                Arrays.asList(
                        "&7Connect with our community.",
                        "",
                        " &4&l» &fDiscord: &bdiscord.gg/strivepvp",
                        " &4&l» &fSupport: &7Tickets and suggestions",
                        "",
                        "&aClick to join our Discord!"),
                "/discord"));

        toReturn.put(16, new HelpButton(
                "&e&lPersonal Settings",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U3ZDhjMjQyZDJlNGY4MDI4ZjkzMGJlNzZmMzUwMTRiMjFiNTI1NTIwOGIxYzA0MTgxYjI1NzQxMzFiNzVhIn19fQ==",
                Arrays.asList(
                        "&7Configure your gameplay experience.",
                        "",
                        " &4&l» &fFeatures: &7Founders, PMs, Staff Alerts",
                        " &4&l» &fSystem: &7Individual preferences",
                        "",
                        "&aClick to customize settings!"),
                "/settings"));

        toReturn.put(28, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "" + ChatColor.BOLD + "Token Shop";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "&7Spend your tokens on faction upgrades.",
                        "",
                        " &4&l» &fUtility: &7Base & Faller Tokens",
                        " &4&l» &fEconomy: &7Token-based progression",
                        "",
                        "&aClick to open Token Shop!").stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/tokenshop");
            }
        });

        toReturn.put(29, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "" + ChatColor.BOLD + "Ability Items";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.FIREWORK;
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "&7Master all special abilities.",
                        "",
                        " &4&l» &fVariety: &7Archer, Bard, Rogue",
                        " &4&l» &fCombat: &7Unique tactical items",
                        "",
                        "&aClick to view abilities!").stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/abilityitems");
            }
        });

        toReturn.put(32, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "" + ChatColor.BOLD + "KOTH Schedule";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WATCH;
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "&7View when KOTHs start.",
                        "",
                        " &4&l» &fStatus: &7Check active timers",
                        " &4&l» &fEvents: &7Timed capture objectives",
                        "",
                        "&aClick to view KOTH schedule!").stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/koth schedule");
            }
        });

        toReturn.put(33, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "" + ChatColor.BOLD + "Server Kits";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ENDER_CHEST;
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "&7Quick access to starting gear.",
                        "",
                        " &4&l» &fAccess: &7Ranked and special kits",
                        " &4&l» &fTimer: &7Cooldown management",
                        "",
                        "&aClick to view kits!").stream().map(ChatColor::translate).collect(Collectors.toList());
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/kits");
            }
        });

        toReturn.put(40, new HelpButton(
                "&c&lSOTW Schedule",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZlOGNmZjc1ZjdkNDMzMjYwYWYxZWNiMmY3NzNiNGJjMzgxZDk1MWRlNGUyZWI2NjE0MjM3NzlhNTkwZTcyYiJ9fX0=",
                Arrays.asList(
                        "&7Weekly event timelines.",
                        "",
                        " &4&l» &fEvents: &7EOTW, SOTW, F-Top",
                        " &4&l» &fTime: &7Dynamic server clock",
                        "",
                        "&aClick to view schedule!"),
                "/schedule"));

        toReturn.put(41, new HelpButton(
                "&c&lLeaderboards",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiYWYwYzU4OWE2YjU4MzUxMWQ4M2MyNjgyNDA4NDJkMzM2NDc3NGVjOWY1NjZkMWZkNGQzNDljZjQyZmIifX19",
                Arrays.asList(
                        "&7Who is the best of the best?",
                        "",
                        " &4&l» &fCategories: &7Kills, Deaths, Wins",
                        " &4&l» &fFactions: &7Highest DTR and Points",
                        "",
                        "&aClick to view top players!"),
                "/lb"));

        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 54;
    }

    private static class HelpButton extends Button {
        private final String name;
        private final String texture;
        private final List<String> lore;
        private final String command;

        public HelpButton(String name, String texture, List<String> lore, String command) {
            this.name = name;
            this.texture = texture;
            this.lore = lore;
            this.command = command;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.translateAlternateColorCodes('&', name);
        }

        @Override
        public List<String> getDescription(Player player) {
            return lore.stream().map(ChatColor::translate).collect(Collectors.toList());
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.SKULL_ITEM;
        }

        @Override
        public byte getDamageValue(Player player) {
            return (byte) 3;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat(command);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.copyOf(super.getButtonItem(player)).texture(texture).build();
        }
    }
}
