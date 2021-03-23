package rip.lazze.libraries.utilities.commands.parameter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;

import rip.lazze.libraries.utilities.ItemUtils;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.lazze.libraries.utilities.ItemUtils;
import rip.lazze.libraries.utilities.commands.param.ParameterType;

public class ItemStackParameterType implements ParameterType<ItemStack> {
    public ItemStackParameterType() {
    }

    public ItemStack transform(CommandSender sender, String source) {
        ItemStack item = ItemUtils.get(source);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "No item with the name " + source + " found.");
            return null;
        } else {
            return item;
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}