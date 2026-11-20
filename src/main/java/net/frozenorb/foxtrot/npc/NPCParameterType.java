package net.frozenorb.foxtrot.npc;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NPCParameterType implements ParameterType<NPC> {

    @Override
    public NPC transform(CommandSender sender, String source) {
        NPC npc = Foxtrot.getInstance().getNpcHandler().getNpcs().get(source);
        if (npc == null) {
            sender.sendMessage(ChatColor.RED + "NPC '" + source + "' not found.");
            return null;
        }
        return npc;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Foxtrot.getInstance().getNpcHandler().getNpcs().keySet().stream()
                .filter(id -> id.toLowerCase().startsWith(source.toLowerCase()))
                .collect(Collectors.toList());
    }
}
