package rip.lazze.libraries.utilities.commands.parameter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.kt.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Getter
public class DurationParameterType {
    private Long duration;
    private String source;
    private boolean perm;

    public DurationParameterType(long duration, String source, boolean perm) {
        this.duration = duration;
        this.source = source;
        this.perm = perm;
    }

    public static class Type implements ParameterType {
        public DurationParameterType transform(CommandSender sender, String source) {
            try {

                final int toReturn = TimeUtils.parseTime(source); // parse time

                if ((toReturn * 1000L) <= 0) {
                    sender.sendMessage(ChatColor.RED + "Duration must be higher then 0.");
                    return null;
                }

                return new DurationParameterType(toReturn * 1000L, source, false); // return if it valid
            } catch (NullPointerException | IllegalArgumentException ex) {
                return new DurationParameterType(Long.valueOf(Integer.MAX_VALUE), source, true); // return permanent if it is invalid
            }
        }

        public List tabComplete(Player sender, Set flags, String source) {
            List completions = new ArrayList();
            return completions;
        }
    }
}