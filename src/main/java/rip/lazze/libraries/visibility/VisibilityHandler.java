package rip.lazze.libraries.visibility;

import org.bukkit.entity.Player;

public interface VisibilityHandler {
    VisibilityAction getAction(final Player p0, final Player p1);
}
