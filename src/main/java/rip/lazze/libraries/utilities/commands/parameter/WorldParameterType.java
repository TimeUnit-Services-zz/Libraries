package rip.lazze.libraries.utilities.commands.parameter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

public class WorldParameterType implements ParameterType<World> {
    public WorldParameterType() {
    }

    public World transform(CommandSender sender, String value) {
        World world = Bukkit.getWorld(value);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "No world with the name \"" + value + "\" found.");
            return null;
        } else {
            return world;
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
}
