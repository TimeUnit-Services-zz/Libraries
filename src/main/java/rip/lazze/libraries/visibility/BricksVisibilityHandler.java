package rip.lazze.libraries.visibility;

import com.google.common.base.Preconditions;
import rip.lazze.libraries.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.*;

public class BricksVisibilityHandler {
    private static Map<String, VisibilityHandler> handlers;
    private static Map<String, OverrideHandler> overrideHandlers;
    private static boolean initiated;

    static {
        BricksVisibilityHandler.handlers = new LinkedHashMap<>();
        BricksVisibilityHandler.overrideHandlers = new LinkedHashMap<>();
        BricksVisibilityHandler.initiated = false;
    }

    public static void init() {
        Preconditions.checkState(!BricksVisibilityHandler.initiated);
        BricksVisibilityHandler.initiated = true;
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerJoin(final PlayerJoinEvent event) {
                BricksVisibilityHandler.update(event.getPlayer());
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onTabComplete(final PlayerChatTabCompleteEvent event) {
                final String token = event.getLastToken();
                final Collection completions = event.getTabCompletions();
                completions.clear();
                System.out.println("Test: " + token);
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (BricksVisibilityHandler.treatAsOnline(player, event.getPlayer())) {
                        if (!StringUtil.startsWithIgnoreCase(player.getName(), token)) {
                            continue;
                        }
                        completions.add(player.getName());
                    }
                }
            }
        }, (Plugin) Library.getInstance());
    }

    public static void registerHandler(final String identifier, final VisibilityHandler handler) {
        BricksVisibilityHandler.handlers.put(identifier, handler);
    }

    public static void registerOverride(final String identifier, final OverrideHandler handler) {
        BricksVisibilityHandler.overrideHandlers.put(identifier, handler);
    }

    public static void update(final Player player) {
        if (BricksVisibilityHandler.handlers.isEmpty() && BricksVisibilityHandler.overrideHandlers.isEmpty()) {
            return;
        }
        updateAllTo(player);
        updateToAll(player);
    }

    @Deprecated
    public static void updateAllTo(final Player viewer) {
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            } else {
                viewer.showPlayer(target);
            }
        }
    }

    @Deprecated
    public static void updateToAll(final Player target) {
        for (final Player viewer : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            } else {
                viewer.showPlayer(target);
            }
        }
    }

    public static boolean treatAsOnline(final Player target, final Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("basic.staff");
    }

    private static boolean shouldSee(final Player target, final Player viewer) {
        for (final OverrideHandler overrideHandler : BricksVisibilityHandler.overrideHandlers.values()) {
            if (overrideHandler.getAction(target, viewer) != OverrideAction.SHOW) {
                continue;
            }
            return true;
        }
        for (final VisibilityHandler visibilityHandler : BricksVisibilityHandler.handlers.values()) {
            if (visibilityHandler.getAction(target, viewer) != VisibilityAction.HIDE) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static List<String> getDebugInfo(final Player target, final Player viewer) {
        final List<String> debug = new ArrayList<String>();
        Boolean canSee = null;
        for (final Map.Entry<String, OverrideHandler> entry : BricksVisibilityHandler.overrideHandlers.entrySet()) {
            final Object handler = entry.getValue();
            final Enum action = ((OverrideHandler) handler).getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action == OverrideAction.SHOW && canSee == null) {
                canSee = true;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Overriding Handler: \"" + entry.getKey() + "\": " + action);
        }
        for (final Map.Entry<String, VisibilityHandler> entry2 : BricksVisibilityHandler.handlers.entrySet()) {
            final Object handler = entry2.getValue();
            final Enum action = ((VisibilityHandler) handler).getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action == VisibilityAction.HIDE && canSee == null) {
                canSee = false;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Normal Handler: \"" + entry2.getKey() + "\": " + action);
        }
        if (canSee == null) {
            canSee = true;
        }
        debug.add(ChatColor.AQUA + "Result: " + viewer.getName() + " " + (canSee ? "can" : "cannot") + " see " + target.getName());
        return debug;
    }
}
