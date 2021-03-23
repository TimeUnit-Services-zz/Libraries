package rip.lazze.libraries.utilities.commands.parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;

public class PlayerParameterType implements ParameterType<Player> {
    public PlayerParameterType() {
    }

    public Player transform(CommandSender sender, String value) {
        if (!(sender instanceof Player) || !value.equalsIgnoreCase("self") && !value.equals("")) {
            Player player = Bukkit.getServer().getPlayer(value);
            if (player != null && (!(sender instanceof Player) || BricksVisibilityHandler.treatAsOnline(player, (Player)sender))) {
                return player;
            } else {
                sender.sendMessage(ChatColor.RED + "No player with the name \"" + value + "\" found.");
                return null;
            }
        } else {
            return (Player)sender;
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
