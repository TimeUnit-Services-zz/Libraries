package rip.lazze.libraries.utilities.commands.parameter;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameModeParameterType implements ParameterType<GameMode> {

    private static final Map<String, GameMode> MAP = new HashMap<>();

    static {
        MAP.put("c", GameMode.CREATIVE);
        MAP.put("creative", GameMode.CREATIVE);
        MAP.put("1", GameMode.CREATIVE);

        MAP.put("s", GameMode.SURVIVAL);
        MAP.put("survival", GameMode.SURVIVAL);
        MAP.put("0", GameMode.SURVIVAL);
    }

    public GameMode transform(CommandSender sender, String source) {
        if (!MAP.containsKey(source.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid game mode.");
            return (null);
        }

        return MAP.get(source.toLowerCase());
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return (MAP.keySet().stream().filter(string -> StringUtils.startsWithIgnoreCase(string, source)).collect(Collectors.toList()));
    }
}