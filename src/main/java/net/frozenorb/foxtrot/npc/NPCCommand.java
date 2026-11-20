package net.frozenorb.foxtrot.npc;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.MojangUtil;
import cc.fyre.proton.util.Pair;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.npc.menu.NPCEditorMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCCommand {

    @Command(names = { "npc create" }, permission = "hilton.npc.create")
    public static void create(Player player, @Parameter(name = "id") String id) {
        if (Foxtrot.getInstance().getNpcHandler().getNpcs().containsKey(id)) {
            player.sendMessage(ChatColor.RED + "An NPC with that name already exists.");
            return;
        }

        Foxtrot.getInstance().getNpcHandler().createNPC(id, player.getLocation());
        player.sendMessage(ChatColor.GREEN + "NPC '" + id + "' created at your position.");
    }

    @Command(names = { "npc delete" }, permission = "hilton.npc.delete")
    public static void delete(Player player, @Parameter(name = "npc") NPC npc) {
        Foxtrot.getInstance().getNpcHandler().removeNPC(npc.getId());
        player.sendMessage(ChatColor.YELLOW + "NPC '" + npc.getId() + "' deleted.");
    }

    @Command(names = { "npc editor" }, permission = "hilton.npc.editor")
    public static void editor(Player player) {
        new NPCEditorMenu().openMenu(player);
    }

    @Command(names = { "npc addline" }, permission = "hilton.npc.edit")
    public static void addLine(Player player, @Parameter(name = "npc") NPC npc,
            @Parameter(name = "text", wildcard = true) String text) {
        npc.getHologramLines().add(text);
        Foxtrot.getInstance().getNpcHandler().updateHolograms(npc);
        player.sendMessage(ChatColor.GREEN + "Added line to NPC " + npc.getId());
    }

    @Command(names = { "npc setline" }, permission = "hilton.npc.edit")
    public static void setLine(Player player, @Parameter(name = "npc") NPC npc, @Parameter(name = "index") int index,
            @Parameter(name = "text", wildcard = true) String text) {
        if (index < 0 || index >= npc.getHologramLines().size()) {
            player.sendMessage(ChatColor.RED + "Invalid line index.");
            return;
        }
        npc.getHologramLines().set(index, text);
        Foxtrot.getInstance().getNpcHandler().updateHolograms(npc);
        player.sendMessage(ChatColor.GREEN + "Updated line #" + (index + 1) + " for NPC " + npc.getId());
    }

    @Command(names = { "npc addcommand" }, permission = "hilton.npc.edit")
    public static void addCommand(Player player, @Parameter(name = "npc") NPC npc,
            @Parameter(name = "command", wildcard = true) String command) {
        npc.getCommands().add(command);
        Foxtrot.getInstance().getNpcHandler().updateNPC(npc);
        player.sendMessage(ChatColor.GREEN + "Added command to NPC " + npc.getId());
    }

    @Command(names = { "npc removeline" }, permission = "hilton.npc.edit")
    public static void removeLine(Player player, @Parameter(name = "npc") NPC npc, @Parameter(name = "index") int index) {
        if (index < 0 || index >= npc.getHologramLines().size()) {
            player.sendMessage(ChatColor.RED + "Invalid line index. Must be between 0 and " + (npc.getHologramLines().size() - 1));
            return;
        }
        npc.getHologramLines().remove(index);
        Foxtrot.getInstance().getNpcHandler().updateHolograms(npc);
        player.sendMessage(ChatColor.GREEN + "Removed line #" + index + " from NPC " + npc.getId());
    }

    @Command(names = { "npc clearhologram" }, permission = "hilton.npc.edit")
    public static void clearHologram(Player player, @Parameter(name = "npc") NPC npc) {
        npc.getHologramLines().clear();
        Foxtrot.getInstance().getNpcHandler().updateHolograms(npc);
        player.sendMessage(ChatColor.GREEN + "Cleared all hologram lines from NPC " + npc.getId());
    }

    @Command(names = { "npc movehere" }, permission = "hilton.npc.edit")
    public static void moveHere(Player player, @Parameter(name = "npc") NPC npc) {
        npc.setLocation(player.getLocation());
        Foxtrot.getInstance().getNpcHandler().respawnNPC(npc);
        player.sendMessage(ChatColor.GREEN + "Moved NPC '" + npc.getId() + "' to your current location.");
    }

    @Command(names = { "npc skin" }, permission = "hilton.npc.edit", async = true)
    public static void setSkin(Player player, @Parameter(name = "npc") NPC npc,
            @Parameter(name = "player") String targetName) {
        try {
            UUID uuid = UUIDUtils.uuid(targetName);

            if (uuid == null) {
                uuid = MojangUtil.getFromMojang(targetName);
            }

            if (uuid == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return;
            }

            Pair<String, String> skin = MojangUtil.getSkin(uuid);
            if (skin == null) {
                player.sendMessage(ChatColor.RED + "Could not fetch skin.");
                return;
            }

            npc.setSkinName(targetName);
            npc.setSkinValue(skin.getKey());
            npc.setSkinSignature(skin.getValue());

            Foxtrot.getInstance().getNpcHandler().respawnNPC(npc);
            player.sendMessage(ChatColor.GREEN + "NPC skin updated to " + targetName);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "An error occurred while fetching the skin.");
            e.printStackTrace();
        }
    }

    @Command(names = { "npc setoffset" }, permission = "hilton.npc.edit")
    public static void setOffset(Player player, @Parameter(name = "npc") NPC npc, @Parameter(name = "offset") double offset) {
        npc.setHologramOffset(offset);
        Foxtrot.getInstance().getNpcHandler().updateHolograms(npc);
        Foxtrot.getInstance().getNpcHandler().updateNPC(npc);
        player.sendMessage(ChatColor.GREEN + "Hologram offset for NPC '" + npc.getId() + "' set to " + offset + ".");
    }
}
