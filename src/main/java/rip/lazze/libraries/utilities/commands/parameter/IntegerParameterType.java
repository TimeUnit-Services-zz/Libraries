package rip.lazze.libraries.utilities.commands.parameter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

public class IntegerParameterType implements ParameterType<Integer> {
    public IntegerParameterType() {
    }

    public Integer transform(CommandSender sender, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException var4) {
            sender.sendMessage(ChatColor.RED + value + " is not a valid number.");
            return null;
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}
