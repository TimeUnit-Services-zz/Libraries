package rip.lazze.libraries.utilities.commands.parameter;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

public class BooleanParameterType implements ParameterType<Boolean> {
    private final Map<String, Boolean> MAP = Maps.newHashMap();

    public BooleanParameterType() {
        this.MAP.put("true", true);
        this.MAP.put("on", true);
        this.MAP.put("yes", true);
        this.MAP.put("false", false);
        this.MAP.put("off", false);
        this.MAP.put("no", false);
    }

    public Boolean transform(CommandSender sender, String source) {
        if (!this.MAP.containsKey(source.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid boolean.");
            return null;
        } else {
            return this.MAP.get(source.toLowerCase());
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return new ArrayList(this.MAP.keySet());
    }
}
