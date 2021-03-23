package rip.lazze.libraries.utilities.commands.parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rip.lazze.libraries.Library;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    public OfflinePlayerParameterType() {
    }

    public OfflinePlayer transform(CommandSender sender, String source) {
        return !(sender instanceof Player) || !source.equalsIgnoreCase("self") && !source.equals("") ? Library.getInstance().getServer().getOfflinePlayer(source) : (Player)sender;
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