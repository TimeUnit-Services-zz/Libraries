package rip.lazze.libraries.utilities.commands.parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;

public class UUIDParameterType implements ParameterType<UUID> {
    public UUIDParameterType() {
    }

    public UUID transform(CommandSender sender, String source) {
        if (!(sender instanceof Player) || !source.equalsIgnoreCase("self") && !source.equals("")) {
            UUID uuid = Bukkit.getOfflinePlayer(source).getUniqueId();
            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + source + " has never joined the server.");
                return null;
            } else {
                return uuid;
            }
        } else {
            return ((Player)sender).getUniqueId();
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList();
        Iterator var5 = Bukkit.getOnlinePlayers().iterator();

        while(var5.hasNext()) {
            Player player = (Player)var5.next();
            if (BricksVisibilityHandler.treatAsOnline(player, sender)) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}
