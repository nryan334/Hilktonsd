package net.frozenorb.foxtrot.gameplay.loot.crate.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hxmilton (JavaProject)
 * @Contact hxmiltonn (discord)
 */

public class CrateEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + "Crate Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 0;
        for (Crate crate : Foxtrot.getInstance().getCrateHandler().getCrates()) {
            buttons.put(slot++, new CrateButton(crate));
        }

        buttons.put(45, new CreateCrateButton());

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 54;
    }

    private static class CrateButton extends Button {
        private final Crate crate;

        public CrateButton(Crate crate) {
            this.crate = crate;
        }

        @Override
        public String getName(Player player) {
            return ChatColor.translateAlternateColorCodes('&', crate.getDisplayName());
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();
            description.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + crate.getId());
            description.add("");
            description.add(ChatColor.YELLOW + "Click to edit this crate.");
            description.add(ChatColor.RED + "Shift-Click to delete this crate.");
            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return crate.getMaterial();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isShiftClick()) {
                Foxtrot.getInstance().getCrateHandler().deleteCrate(crate);
                player.sendMessage(ChatColor.RED + "Deleted crate " + crate.getId() + ".");
                new CrateEditorMenu().openMenu(player);
                return;
            }

            new CrateEditMenu(crate).openMenu(player);
        }
    }

    private static class CreateCrateButton extends Button {
        @Override
        public String getName(Player player) {
            return ChatColor.GREEN + ChatColor.BOLD.toString() + "Create New Crate";
        }

        @Override
        public List<String> getDescription(Player player) {
            return new ArrayList<>();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.NETHER_STAR;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                    .withFirstPrompt(new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return ChatColor.YELLOW
                                    + "Please enter an ID for the new crate (e.g. 'Epic'). Type 'cancel' to exit.";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if (input.equalsIgnoreCase("cancel")) {
                                return Prompt.END_OF_CONVERSATION;
                            }

                            if (Foxtrot.getInstance().getCrateHandler().findById(input).isPresent()) {
                                context.getForWhom()
                                        .sendRawMessage(ChatColor.RED + "A crate with that ID already exists.");
                                return this;
                            }

                            Crate crate = new Crate(input, "&6" + input + " Crate", Material.CHEST, (short) 0,
                                    Material.CHEST, (short) 0, org.bukkit.Color.ORANGE, new ArrayList<>(), new ArrayList<>(), 3, null, new ArrayList<>());
                            Foxtrot.getInstance().getCrateHandler().getCrates().add(crate);
                            context.getForWhom().sendRawMessage(ChatColor.GREEN + "Created crate " + input + ".");

                            new CrateEditMenu(crate).openMenu((Player) context.getForWhom());
                            return Prompt.END_OF_CONVERSATION;
                        }
                    })
                    .withLocalEcho(false);
            player.beginConversation(factory.buildConversation(player));
        }
    }
}
