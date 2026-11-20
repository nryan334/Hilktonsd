package net.frozenorb.foxtrot.team.menu.system;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.Callback;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.conversations.*;
import org.bson.types.ObjectId;

public class SystemCreateButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + ChatColor.BOLD.toString() + "Create Faction";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to create a new system faction.");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        
        final ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return ChatColor.YELLOW + "Please type a name for the new system faction. Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to stop.";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        Player sender = (Player) context.getForWhom();

                        if (input.equalsIgnoreCase("cancel")) {
                            sender.sendRawMessage(ChatColor.RED + "Creation cancelled.");
                            org.bukkit.Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> new SystemEditorMenu().openMenu(sender));
                            return END_OF_CONVERSATION;
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(input) != null) {
                            sender.sendRawMessage(ChatColor.RED + "A faction with that name already exists!");
                            return this;
                        }

                        Team team = new Team(input);
                        team.setUniqueId(new ObjectId());
                        team.setName(input);
                        team.setDTR(1);
                        Foxtrot.getInstance().getTeamHandler().setupTeam(team);
                        
                        sender.sendRawMessage(ChatColor.GREEN + "Created system faction " + ChatColor.WHITE + input + ChatColor.GREEN + ".");
                        org.bukkit.Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> new SystemEditorMenu().openMenu(sender));
                        return END_OF_CONVERSATION;
                    }
                }).withLocalEcho(false);

        org.bukkit.Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> player.beginConversation(factory.buildConversation(player)));
    }
}
