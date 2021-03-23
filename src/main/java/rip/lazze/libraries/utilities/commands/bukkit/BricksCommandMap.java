package rip.lazze.libraries.utilities.commands.bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import rip.lazze.libraries.utilities.commands.CommandNode;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class BricksCommandMap extends SimpleCommandMap {

    public BricksCommandMap(Server server) {
        super(server);
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");
        int spaceIndex = cmdLine.indexOf(32);
        String prefix;
        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList();
            Map<String, Command> knownCommands = this.knownCommands;
            prefix = sender instanceof Player ? "/" : "";
            Iterator var17 = knownCommands.entrySet().iterator();

            while(var17.hasNext()) {
                Entry<String, Command> commandEntry = (Entry)var17.next();
                String name = (String)commandEntry.getKey();
                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    Command command = (Command)commandEntry.getValue();
                    if (command instanceof BricksCommand) {
                        CommandNode executionNode = ((BricksCommand)command).node.getCommand(name);
                        if (executionNode == null) {
                            executionNode = ((BricksCommand)command).node;
                        }

                        if (!executionNode.hasCommands()) {
                            CommandNode testNode = executionNode.getCommand(name);
                            if (testNode == null) {
                                testNode = ((BricksCommand)command).node.getCommand(name);
                            }

                            if (testNode.canUse(sender)) {
                                completions.add(prefix + name);
                            }
                        } else if (executionNode.getSubCommands(sender, false).size() != 0) {
                            completions.add(prefix + name);
                        }
                    } else if (command.testPermissionSilent(sender)) {
                        completions.add(prefix + name);
                    }
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        } else {
            String commandName = cmdLine.substring(0, spaceIndex);
            Command target = this.getCommand(commandName);
            if (target == null) {
                return null;
            } else if (!target.testPermissionSilent(sender)) {
                return null;
            } else {
                prefix = cmdLine.substring(spaceIndex + 1, cmdLine.length());
                String[] args = prefix.split(" ");

                try {
                    List<String> completions = target instanceof BricksCommand ? ((BricksCommand)target).tabComplete(sender, cmdLine, ((BricksCommand)target).getNode().ShouldComplete()) : target.tabComplete(sender, commandName, args);
                    if (completions != null) {
                        Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
                    }

                    return completions;
                } catch (CommandException var13) {
                    throw var13;
                } catch (Throwable var14) {
                    throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, var14);
                }
            }
        }
    }
}
