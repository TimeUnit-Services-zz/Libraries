package rip.lazze.libraries.utilities.commands.bukkit;

import java.util.Set;
import rip.lazze.libraries.utilities.commands.CommandNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class BricksHelpTopic extends HelpTopic {
    private CommandNode node;

    public BricksHelpTopic(CommandNode node, Set<String> aliases) {
        this.node = node;
        this.name = "/" + node.getName();
        String description = node.getDescription();
        if (description.length() < 32) {
            this.shortText = description;
        } else {
            this.shortText = description.substring(0, 32);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(node.getDescription());
        sb.append("\n");
        sb.append(ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(node.getUsageForHelpTopic());
        if (aliases != null && aliases.size() > 0) {
            sb.append("\n");
            sb.append(ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(StringUtils.join(aliases, ", "));
        }

        this.fullText = sb.toString();
    }

    public boolean canSee(CommandSender commandSender) {
        return this.node.canUse(commandSender);
    }
}
