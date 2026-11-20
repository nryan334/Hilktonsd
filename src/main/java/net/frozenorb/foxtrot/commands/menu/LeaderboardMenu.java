package net.frozenorb.foxtrot.commands.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.gameplay.extra.stats.command.StatsTopCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamTopCommand;
import net.frozenorb.foxtrot.util.GlassButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class LeaderboardMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Leaderboards";
    }

    @Override
    public int size(Player player) {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            buttons.put(i, new GlassButton(15));
        }

        buttons.put(11, new Button() {
            @Override
            public String getName(Player player) { return null; }

            @Override
            public List<String> getDescription(Player player) { return null; }

            @Override
            public Material getMaterial(Player player) { return null; }

            @Override
            public ItemStack getButtonItem(Player player) {
                Map<StatsEntry, String> killsMap = Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(StatsTopCommand.StatsObjective.KILLS, 5);
                
                String top1Name = "None";
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");
                int index = 1;
                for (Map.Entry<StatsEntry, String> entry : killsMap.entrySet()) {
                    String name = UUIDUtils.name(entry.getKey().getOwner());
                    if (index == 1 && name != null) {
                        top1Name = name;
                    }
                    lore.add(ChatColor.RED.toString() + ChatColor.BOLD + index + ". " + ChatColor.WHITE + name + ChatColor.GRAY + " - " + ChatColor.WHITE + entry.getValue());
                    index++;
                }
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                if (!top1Name.equals("None")) {
                    meta.setOwner(top1Name);
                }
                meta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Top 5 Kills");
                meta.setLore(lore);
                skull.setItemMeta(meta);

                return skull;
            }
        });

        buttons.put(15, new Button() {
            @Override
            public String getName(Player player) { return null; }

            @Override
            public List<String> getDescription(Player player) { return null; }

            @Override
            public Material getMaterial(Player player) { return null; }

            @Override
            public ItemStack getButtonItem(Player player) {
                Map<StatsEntry, String> deathsMap = Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(StatsTopCommand.StatsObjective.DEATHS, 5);

                String top1Name = "None";
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");
                int index = 1;
                for (Map.Entry<StatsEntry, String> entry : deathsMap.entrySet()) {
                    String name = UUIDUtils.name(entry.getKey().getOwner());
                    if (index == 1 && name != null) {
                        top1Name = name;
                    }
                    lore.add(ChatColor.RED.toString() + ChatColor.BOLD + index + ". " + ChatColor.WHITE + name + ChatColor.GRAY + " - " + ChatColor.WHITE + entry.getValue());
                    index++;
                }
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                if (!top1Name.equals("None")) {
                    meta.setOwner(top1Name);
                }
                meta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Top 5 Deaths");
                meta.setLore(lore);
                skull.setItemMeta(meta);

                return skull;
            }
        });

        buttons.put(29, new Button() {
            @Override
            public String getName(Player player) { return null; }

            @Override
            public List<String> getDescription(Player player) { return null; }

            @Override
            public Material getMaterial(Player player) { return null; }

            @Override
            public ItemStack getButtonItem(Player player) {
                LinkedHashMap<Team, Integer> pointsMap = TeamTopCommand.getSortedRaidableTeams();

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");
                int index = 1;
                for (Map.Entry<Team, Integer> entry : pointsMap.entrySet()) {
                    if (entry.getKey().getOwner() == null) continue;
                    lore.add(ChatColor.RED.toString() + ChatColor.BOLD + index + ". " + ChatColor.WHITE + entry.getKey().getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + entry.getValue());
                    index++;
                    if (index > 5) break;
                }
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");

                return ItemBuilder.of(Material.PAPER).name(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Top 5 Factions Made Raidable").setLore(lore).build();
            }
        });

        buttons.put(31, new Button() {
            @Override
            public String getName(Player player) { return null; }

            @Override
            public List<String> getDescription(Player player) { return null; }

            @Override
            public Material getMaterial(Player player) { return null; }

            @Override
            public ItemStack getButtonItem(Player player) {
                LinkedHashMap<Team, Integer> pointsMap = TeamTopCommand.getSortedTrappingPoints();

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");
                int index = 1;
                for (Map.Entry<Team, Integer> entry : pointsMap.entrySet()) {
                    if (entry.getKey().getOwner() == null) continue;
                    lore.add(ChatColor.RED.toString() + ChatColor.BOLD + index + ". " + ChatColor.WHITE + entry.getKey().getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + entry.getValue());
                    index++;
                    if (index > 5) break;
                }
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");

                return ItemBuilder.of(Material.PAPER).name(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Top 5 Factions Trapping").setLore(lore).build();
            }
        });

        buttons.put(33, new Button() {
            @Override
            public String getName(Player player) { return null; }

            @Override
            public List<String> getDescription(Player player) { return null; }

            @Override
            public Material getMaterial(Player player) { return null; }

            @Override
            public ItemStack getButtonItem(Player player) {
                LinkedHashMap<Team, Integer> pointsMap = TeamTopCommand.getSortedPointTeams();

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");
                int index = 1;
                for (Map.Entry<Team, Integer> entry : pointsMap.entrySet()) {
                    if (entry.getKey().getOwner() == null) continue;
                    lore.add(ChatColor.RED.toString() + ChatColor.BOLD + index + ". " + ChatColor.WHITE + entry.getKey().getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + entry.getValue());
                    index++;
                    if (index > 5) break;
                }
                lore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");

                return ItemBuilder.of(Material.PAPER).name(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Top 5 Factions Points").setLore(lore).build();
            }
        });

        return buttons;
    }
}
