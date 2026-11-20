package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.gameplay.armorclass.menu.ArmorClassesMenu;
import net.frozenorb.foxtrot.gameplay.armorclass.menu.ArmorMainMenu;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreGetter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class HelpfulCommands {
    @Command(names = { "basebuild", "basetutorial" }, permission = "")
    public static void baseBuild(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "You have a Base Build Perk?");
        player.sendMessage(ChatColor.GRAY + "And don't know how to build a base? Check this out!");
        player.sendMessage(ChatColor.RED + "https://strivepvp.top/basetutorial");
        player.sendMessage("");
    }

    @Command(names = { "schedule" }, permission = "")
    public static void schedule(Player player) {

        final int date = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);

        new Menu() {
            @Override
            public String getTitle(Player player) {
                return "Schedule";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                for (int i = 0; i < 27; i++) {
                    toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, ""));
                }

                toReturn.put(10, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Saturday [SOTW]";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Friday SOTWs!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f3:00 PM EST: &cStart Of The World"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f3:30 PM EST: &cLootbox All"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f3:35 PM EST: &e&lPerk All"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:15 PM EST: &cSOTW Timer Ends"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f5:15 PM EST: &5Enderdragon Event"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f6:30 PM EST: &6OP LootBox All"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f7:00 PM EST: &cFirst KOTH"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f8:30 PM EST: &5&lSkills Key-All"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fSale Boosters &cCheck them on Scoreboard"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.SATURDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.SATURDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(11, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Sunday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();
                        // TODO: CHANGE THIS
                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Sunday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f2:00 PM EST: &c3x Airdrops/Perk/Mystery Box"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f2:00 PM EST: &bAirdrop All"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f2:30 PM EST: &cLootbox All"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f3:00 PM EST: &4Rage Event"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &bAirdrop All"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &5Overworld Citadel"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.SUNDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.SUNDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(12, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Monday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Monday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f12:00 PM EST: &4Nether Citadel"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &cRaider Event"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.MONDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.MONDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(13, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Tuesday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Tuesday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f12:00 PM EST: &dMayhem Event"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f5:00 PM EST: &4Nether Citadel"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.TUESDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.TUESDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(14, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Wednesday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Wednesday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &bTraveller Event"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &cFaction Tournament (Practice)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.WEDNESDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.WEDNESDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(15, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Thursday";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Wednesday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f12:00 AM EST: &d2x Points Starts"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f1:00 PM EST: &5Overworld Citadel"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &4Rage Event"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f11:59 PM EST: &d2x Points Ends"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.THURSDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.THURSDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                toReturn.put(16, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Friday [EOTW]";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + "Here is the schedule for Friday!");
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Scheduled Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f4:00 PM EST: &6Treasure Cove Voucher"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &f5:00 PM EST: &4&lEOTW starts (Map End)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Recurring Events");
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fCave Says every &c2 hours"));
                        toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4&l┃ &fKOTH &c(/koth schedule)"));
                        toReturn.add("");
                        toReturn.add(ChatColor.GREEN + "Here are all the events for this day");

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {

                        if (date == Calendar.FRIDAY) {
                            return Material.EMERALD_BLOCK;
                        } else if (date > Calendar.FRIDAY) {
                            return Material.REDSTONE_BLOCK;
                        }

                        return Material.GOLD_BLOCK;
                    }
                });

                return toReturn;
            }
        }.openMenu(player);

    }

    @Command(names = { "classes", "class", "armorclasses", "armorclass" }, permission = "")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            new ArmorMainMenu().openMenu(player);
            return;
        }

        new ArmorClassesMenu(Category.ALL).openMenu(player);
    }

    @Command(names = { "outpost", "outposts" }, permission = "")
    public static void outpost(Player player) {
        if (DisableCommand.outpost) {
            player.sendMessage(ChatColor.RED + "Outposts are currently disabled.");
            return;
        }

    }
}
