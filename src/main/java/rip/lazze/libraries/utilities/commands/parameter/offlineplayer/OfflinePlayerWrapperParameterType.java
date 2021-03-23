package rip.lazze.libraries.utilities.commands.parameter.offlineplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.visibility.BricksVisibilityHandler;

public class OfflinePlayerWrapperParameterType implements ParameterType<OfflinePlayerWrapper> {
    public OfflinePlayerWrapperParameterType() {
    }

    public OfflinePlayerWrapper transform(CommandSender sender, String source) {
        return new OfflinePlayerWrapper(source);
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
