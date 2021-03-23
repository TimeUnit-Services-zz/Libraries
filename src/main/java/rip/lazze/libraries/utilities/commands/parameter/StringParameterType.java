package rip.lazze.libraries.utilities.commands.parameter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

public class StringParameterType implements ParameterType<String> {
    public StringParameterType() {
    }

    public String transform(CommandSender sender, String value) {
        return value;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}
