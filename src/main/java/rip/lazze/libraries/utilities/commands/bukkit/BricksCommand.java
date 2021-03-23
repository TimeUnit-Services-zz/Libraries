package rip.lazze.libraries.utilities.commands.bukkit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import rip.lazze.libraries.Library;
import net.minecraft.util.org.apache.commons.lang3.exception.ExceptionUtils;
import rip.lazze.libraries.utilities.commands.*;
import rip.lazze.libraries.utilities.commands.param.ParameterData;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.util.*;
import java.util.stream.Collectors;

public class BricksCommand extends Command implements PluginIdentifiableCommand {
    protected CommandNode node;
    private JavaPlugin owningPlugin;

    public BricksCommand(CommandNode node, JavaPlugin plugin) {
        super(node.getName(), "", "/", Lists.newArrayList(node.getRealAliases()));
        this.node = node;
        this.owningPlugin = plugin;
    }

    public boolean execute(final CommandSender sender, String label, String[] args) {
        label = label.replace(this.owningPlugin.getName().toLowerCase() + ":", "");
        String[] newArgs = this.concat(label, args);
        final Arguments arguments = (new ArgumentProcessor()).process(newArgs);
        final CommandNode executionNode = this.node.findCommand(arguments);
        final String realLabel = this.getFullLabel(executionNode);
        if (executionNode.canUse(sender)) {
            if (executionNode.isAsync()) {
                (new BukkitRunnable() {
                    public void run() {
                        try {
                            if (!executionNode.invoke(sender, arguments)) {
                                executionNode.getUsage(realLabel).send(sender);
                            }
                        } catch (CommandException var2) {
                            executionNode.getUsage(realLabel).send(sender);
                            sender.sendMessage(ChatColor.RED + "An error occurred while processing your command.");
                            if (sender.isOp()) {
                                BricksCommand.this.sendStackTrace(sender, var2);
                            }
                        }

                    }
                }).runTaskAsynchronously(this.owningPlugin);
            } else {
                try {
                    if (!executionNode.invoke(sender, arguments)) {
                        executionNode.getUsage(realLabel).send(sender);
                    }
                } catch (CommandException var9) {
                    executionNode.getUsage(realLabel).send(sender);
                    sender.sendMessage(ChatColor.RED + "An error occurred while processing your command.");
                    if (sender.isOp()) {
                        this.sendStackTrace(sender, var9);
                    }
                }
            }
        } else if (executionNode.isHidden()) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
        } else {
            sender.sendMessage(BricksCommandHandler.getConfig().getNoPermissionMessage());
        }

        return true;
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine, boolean shouldcomplete) {
        if (!(sender instanceof Player)) {
            return ImmutableList.of();
        } else {
            String[] rawArgs = cmdLine.replace(this.owningPlugin.getName().toLowerCase() + ":", "").split(" ");
            if (rawArgs.length < 1) {
                return !this.node.canUse(sender) ? ImmutableList.of() : ImmutableList.of();
            } else {
                Arguments arguments = (new ArgumentProcessor()).process(rawArgs);
                CommandNode realNode = this.node.findCommand(arguments);
                if (!realNode.canUse(sender)) {
                    return ImmutableList.of();
                } else {
                    List<String> realArgs = arguments.getArguments();
                    int currentIndex = realArgs.size() - 1;
                    if (currentIndex < 0) {
                        currentIndex = 0;
                    }

                    if (cmdLine.endsWith(" ") && realArgs.size() >= 1) {
                        ++currentIndex;
                    }

                    if (currentIndex < 0) {
                        return ImmutableList.of();
                    } else {
                        List<String> completions = new ArrayList();
                        if (realNode.hasCommands()) {
                            String name = realArgs.size() == 0 ? "" : (String)realArgs.get(realArgs.size() - 1);
                            if (!shouldcomplete) {
                                for (final Player player : Library.getInstance().getServer().getOnlinePlayers()) {
                                    if (StringUtils.startsWithIgnoreCase(player.getName(), name)) {
                                        completions.add(player.getName());
                                    }
                                }
                                if (completions.size() > 0) {
                                    return completions;
                                }
                            }
                            completions.addAll(realNode.getChildren().values().stream().filter((node) -> {
                                return node.canUse(sender) && (StringUtils.startsWithIgnoreCase(node.getName(), name) || StringUtils.isEmpty(name));
                            }).map(CommandNode::getName).collect(Collectors.toList()));
                            if (completions.size() > 0) {
                                return completions;
                            }
                        }

                        if (rawArgs[rawArgs.length - 1].equalsIgnoreCase(realNode.getName()) && !cmdLine.endsWith(" ")) {
                            return ImmutableList.of();
                        } else {
                            String argumentBeingCompleted;
                            if (realNode.getValidFlags() != null && !realNode.getValidFlags().isEmpty()) {
                                Iterator var16 = realNode.getValidFlags().iterator();

                                label102:
                                while(true) {
                                    String flags;
                                    do {
                                        do {
                                            if (!var16.hasNext()) {
                                                if (completions.size() > 0) {
                                                    return completions;
                                                }
                                                break label102;
                                            }

                                            flags = (String)var16.next();
                                            argumentBeingCompleted = rawArgs[rawArgs.length - 1];
                                        } while(!Flag.FLAG_PATTERN.matcher(argumentBeingCompleted).matches() && !argumentBeingCompleted.equals("-"));
                                    } while(!StringUtils.startsWithIgnoreCase(flags, argumentBeingCompleted.substring(1, argumentBeingCompleted.length())) && !argumentBeingCompleted.equals("-"));

                                    completions.add("-" + flags);
                                }
                            }

                            try {
                                ParameterType<?> parameterType = null;
                                ParameterData data = null;
                                if (realNode.getParameters() != null) {
                                    List<ParameterData> params = (List)realNode.getParameters().stream().filter((d) -> {
                                        return d instanceof ParameterData;
                                    }).map((d) -> {
                                        return (ParameterData)d;
                                    }).collect(Collectors.toList());
                                    int fixed = Math.max(0, currentIndex - 1);
                                    data = (ParameterData)params.get(fixed);
                                    parameterType = BricksCommandHandler.getParameterType(data.getType());
                                    if (data.getParameterType() != null) {
                                        try {
                                            parameterType = (ParameterType)data.getParameterType().newInstance();
                                        } catch (IllegalAccessException | InstantiationException var14) {
                                            var14.printStackTrace();
                                        }
                                    }
                                }

                                if (parameterType != null) {
                                    if (currentIndex < realArgs.size() && ((String)realArgs.get(currentIndex)).equalsIgnoreCase(realNode.getName())) {
                                        realArgs.add("");
                                        ++currentIndex;
                                    }

                                    argumentBeingCompleted = currentIndex < realArgs.size() && realArgs.size() != 0 ? (String)realArgs.get(currentIndex) : "";
                                    List<String> suggested = parameterType.tabComplete((Player)sender, data.getTabCompleteFlags(), argumentBeingCompleted);
                                    String finalArgumentBeingCompleted = argumentBeingCompleted;
                                    completions.addAll((Collection)suggested.stream().filter((s) -> {
                                        return StringUtils.startsWithIgnoreCase(s, finalArgumentBeingCompleted);
                                    }).collect(Collectors.toList()));
                                }
                            } catch (Exception var15) {
                            }

                            return completions;
                        }
                    }
                }
            }
        }
    }

    public Plugin getPlugin() {
        return this.owningPlugin;
    }

    private String[] concat(String label, String[] args) {
        String[] labelAsArray = new String[]{label};
        String[] newArgs = new String[args.length + 1];
        System.arraycopy(labelAsArray, 0, newArgs, 0, 1);
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }

    private String getFullLabel(CommandNode node) {
        ArrayList labels;
        for(labels = new ArrayList(); node != null; node = node.getParent()) {
            String name = node.getName();
            if (name != null) {
                labels.add(name);
            }
        }

        Collections.reverse(labels);
        labels.remove(0);
        StringBuilder builder = new StringBuilder();
        labels.forEach((s) -> {
            builder.append(s).append(' ');
        });
        return builder.toString();
    }

    private void sendStackTrace(CommandSender sender, Exception exception) {
        String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
        sender.sendMessage(ChatColor.RED + "Message: " + rootCauseMessage);
        String cause = ExceptionUtils.getStackTrace(exception);
        StringTokenizer tokenizer = new StringTokenizer(cause);
        String exceptionType = "";
        String details = "";
        boolean parsingNeeded = false;

        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equalsIgnoreCase("Caused")) {
                tokenizer.nextToken();
                parsingNeeded = true;
                exceptionType = tokenizer.nextToken();
            } else if (token.equalsIgnoreCase("at") && parsingNeeded) {
                details = tokenizer.nextToken();
                break;
            }
        }

        sender.sendMessage(ChatColor.RED + "Exception: " + exceptionType.replace(":", ""));
        sender.sendMessage(ChatColor.RED + "Details:");
        sender.sendMessage(ChatColor.RED + details);
    }

    public CommandNode getNode() {
        return this.node;
    }
}
