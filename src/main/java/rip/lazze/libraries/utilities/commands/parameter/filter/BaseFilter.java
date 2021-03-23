package rip.lazze.libraries.utilities.commands.parameter.filter;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

abstract class BaseFilter implements ParameterType<String> {
    protected final Set<Pattern> bannedPatterns = new HashSet();

    BaseFilter() {
    }

    public String transform(CommandSender sender, String value) {
        Iterator var3 = this.bannedPatterns.iterator();
        Pattern bannedPattern;
        do {
            if (!var3.hasNext()) {
                return value;
            }
            bannedPattern = (Pattern)var3.next();
        } while(!bannedPattern.matcher(value).find());
        sender.sendMessage(ChatColor.RED + "Command contains inappropriate content.");
        return null;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}