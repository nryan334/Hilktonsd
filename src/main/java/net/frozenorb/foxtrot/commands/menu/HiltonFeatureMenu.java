package net.frozenorb.foxtrot.commands.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.fetch.ServerStatus;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HiltonFeatureMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_RED + "Server Features";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new FeatureButton(
                "Bosses",
                () -> !Foxtrot.getInstance().getBossHandler().isDisabled(),
                () -> Foxtrot.getInstance().getBossHandler()
                        .setDisabled(!Foxtrot.getInstance().getBossHandler().isDisabled())));



        buttons.put(12, new FeatureButton(
                "StriveSays",
                () -> !Foxtrot.getInstance().getCaveSaysHandler().isDisabled(),
                () -> Foxtrot.getInstance().getCaveSaysHandler()
                        .setDisabled(!Foxtrot.getInstance().getCaveSaysHandler().isDisabled())));

        Server souls = Universe.getInstance().getUniverseHandler().serverFromName("Souls");
        buttons.put(13, new FeatureButton(
                "Souls Whitelist",
                () -> souls != null && souls.getStatus() == ServerStatus.WHITELISTED,
                () -> {
                    if (souls != null) {
                        souls.setStatus(souls.getStatus() == ServerStatus.WHITELISTED ? ServerStatus.ONLINE
                                : ServerStatus.WHITELISTED);
                    }
                }));

        buttons.put(19, new FeatureButton("Deaths GUI", () -> !DisableCommand.deaths,
                () -> DisableCommand.deaths = !DisableCommand.deaths));
        buttons.put(20, new FeatureButton("Team View", () -> !DisableCommand.teamView,
                () -> DisableCommand.teamView = !DisableCommand.teamView));
        buttons.put(21, new FeatureButton("Anti-Clean", () -> !DisableCommand.antiClean,
                () -> DisableCommand.antiClean = !DisableCommand.antiClean));
        buttons.put(22, new FeatureButton("Daily Kit", () -> !DisableCommand.dailyKit,
                () -> DisableCommand.dailyKit = !DisableCommand.dailyKit));
        buttons.put(23, new FeatureButton("Crapple Limit", () -> DisableCommand.crapplelimit,
                () -> DisableCommand.crapplelimit = !DisableCommand.crapplelimit));
        buttons.put(24, new FeatureButton("Bonus Damage", () -> DisableCommand.damage,
                () -> DisableCommand.damage = !DisableCommand.damage));
        buttons.put(25, new FeatureButton("Backpacks", () -> DisableCommand.backPack,
                () -> DisableCommand.backPack = !DisableCommand.backPack));
        buttons.put(28, new FeatureButton("Bounties", () -> DisableCommand.bounty,
                () -> DisableCommand.bounty = !DisableCommand.bounty));
        buttons.put(29, new FeatureButton("Outpost", () -> !DisableCommand.outpost,
                () -> DisableCommand.outpost = !DisableCommand.outpost));
        buttons.put(30, new FeatureButton("Trap Activator", () -> DisableCommand.trapActivator,
                () -> DisableCommand.trapActivator = !DisableCommand.trapActivator));

        return buttons;
    }

    private static class FeatureButton extends Button {
        private final String featureName;
        private final Supplier<Boolean> enabledSupplier;
        private final Runnable toggleAction;

        public FeatureButton(String featureName, Supplier<Boolean> enabledSupplier, Runnable toggleAction) {
            this.featureName = featureName;
            this.enabledSupplier = enabledSupplier;
            this.toggleAction = toggleAction;
        }

        @Override
        public String getName(Player player) {
            return (enabledSupplier.get() ? ChatColor.GREEN : ChatColor.RED) + featureName;
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(
                    ChatColor.GRAY + "Status: " + (enabledSupplier.get() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to toggle!");
            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return enabledSupplier.get() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            toggleAction.run();
            boolean newState = enabledSupplier.get();
            player.sendMessage(ChatColor.GOLD + featureName + ChatColor.YELLOW + " is now "
                    + (newState ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED") + ".");
        }

        public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
            return true;
        }
    }
}
