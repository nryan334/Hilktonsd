package net.frozenorb.foxtrot.staff;

import cc.fyre.proton.visibility.action.VisibilityAction;
import cc.fyre.proton.visibility.provider.VisibilityProvider;
import org.bukkit.entity.Player;

public class StaffVisibilityAdapter implements VisibilityProvider {

    private final StaffModeHandler handler;

    public StaffVisibilityAdapter(StaffModeHandler handler) {
        this.handler = handler;
    }

    @Override
    public VisibilityAction getAction(Player player, Player viewer) {
        StaffModeHandler.VanishState targetState = handler.getVanishState(player.getUniqueId());
        
        if (targetState == StaffModeHandler.VanishState.VISIBLE) {
            return VisibilityAction.NEUTRAL;
        }

        if (!viewer.hasPermission("foxtrot.staff")) {
            return VisibilityAction.HIDE;
        }

        StaffModeHandler.VanishState viewerState = handler.getVanishState(viewer.getUniqueId());
        boolean viewerVanished = viewerState != StaffModeHandler.VanishState.VISIBLE;

        if (targetState == StaffModeHandler.VanishState.STAFF) {
            if (!viewerVanished) {
                return VisibilityAction.HIDE;
            }
            return VisibilityAction.NEUTRAL;
        }

        if (targetState == StaffModeHandler.VanishState.INVISIBLE) {
            if (!viewerVanished) {
                return VisibilityAction.HIDE;
            } else {
                if (player.isOp() && !viewer.isOp()) {
                    return VisibilityAction.HIDE;
                }
                return VisibilityAction.NEUTRAL;
            }
        }

        return VisibilityAction.NEUTRAL;
    }
}
