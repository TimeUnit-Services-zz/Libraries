package rip.lazze.libraries.utilities.commands;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import rip.lazze.libraries.Library;
import lombok.NonNull;
import mkremins.fanciful.FancyMessage;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import rip.lazze.libraries.utilities.commands.param.ParameterData;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.beans.ConstructorProperties;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandNode {
    @NonNull
    private String name;
    private Set<String> aliases = new HashSet();
    @NonNull
    private String permission;
    private boolean requiresplayer;
    private boolean shouldcomplete;
    private String description;
    private boolean async;
    private boolean hidden;
    protected Method method;
    protected Class<?> owningClass;
    private List<String> validFlags;
    private List<Data> parameters;
    private Map<String, CommandNode> children = new TreeMap();
    private CommandNode parent;

    public CommandNode(Class<?> owningClass) {
        this.owningClass = owningClass;
    }

    public void registerCommand(CommandNode commandNode) {
        commandNode.setParent(this);
        this.children.put(commandNode.getName(), commandNode);
    }

    public boolean hasCommand(String name) {
        return this.children.containsKey(name.toLowerCase());
    }

    public CommandNode getCommand(String name) {
        return this.children.get(name.toLowerCase());
    }

    public boolean hasCommands() {
        return this.children.size() > 0;
    }

    public CommandNode findCommand(Arguments arguments) {
        if (arguments.getArguments().size() > 0) {
            String trySub = (String) arguments.getArguments().get(0);
            if (this.hasCommand(trySub)) {
                arguments.getArguments().remove(0);
                CommandNode returnNode = this.getCommand(trySub);
                return returnNode.findCommand(arguments);
            }
        }
        return this;
    }

    public boolean isValidFlag(String test) {
        return test.length() == 1 ? this.validFlags.contains(test) : this.validFlags.contains(test.toLowerCase());
    }

    public boolean canUse(CommandSender sender) {
        if (this.permission == null)
            return true;
        String permission = this.permission;
        switch (permission) {
            case "console":
                return sender instanceof org.bukkit.command.ConsoleCommandSender;
            case "op":
                return sender.isOp();
            case "":
                return true;
        }
        return sender.hasPermission(this.permission);
    }

    public FancyMessage getUsage(String realLabel) {
        FancyMessage usage = (new FancyMessage("Usage: /" + realLabel)).color(ChatColor.RED);
        if (!Strings.isNullOrEmpty(getDescription()))
            usage.tooltip(ChatColor.YELLOW + getDescription());
        ArrayList flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter(data -> data instanceof FlagData).map(data -> (FlagData)data).collect(Collectors.toList()));
        ArrayList<ParameterData> parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter(data -> data instanceof ParameterData).map(data -> (ParameterData)data).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(getDescription()))
                usage.tooltip(ChatColor.YELLOW + getDescription());
            Iterator<FlagData> index = flags.iterator();
            while (index.hasNext()) {
                FlagData data = index.next();
                String required = data.getNames().get(0);
                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                    if (!Strings.isNullOrEmpty(getDescription()))
                        usage.tooltip(ChatColor.YELLOW + getDescription());
                }
                flagFirst = false;
                usage.then("-" + required).color(ChatColor.AQUA);
                if (!Strings.isNullOrEmpty(data.getDescription()))
                    usage.tooltip(ChatColor.GRAY + data.getDescription());
            }
            usage.then(") ").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(getDescription()))
                usage.tooltip(ChatColor.YELLOW + getDescription());
        }
        if (!parameters.isEmpty())
            for (int var9 = 0; var9 < parameters.size(); var9++) {
                ParameterData var10 = parameters.get(var9);
                boolean var11 = var10.getDefaultValue().isEmpty();
                usage.then((var11 ? "<" : "[") + var10.getName() + (var10.isWildcard() ? "..." : "") + (var11 ? ">" : "]") + ((var9 != parameters.size() - 1) ? " " : "")).color(ChatColor.RED);
                if (!Strings.isNullOrEmpty(getDescription()))
                    usage.tooltip(ChatColor.YELLOW + getDescription());
            }
        return usage;
    }

    public FancyMessage getUsage() {
        FancyMessage usage = new FancyMessage("");
        ArrayList flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter(data -> data instanceof FlagData).map(data -> (FlagData)data).collect(Collectors.toList()));
        ArrayList<ParameterData> parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter(data -> data instanceof ParameterData).map(data -> (ParameterData)data).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            Iterator<FlagData> index = flags.iterator();
            while (index.hasNext()) {
                FlagData data = index.next();
                String required = data.getNames().get(0);
                if (!flagFirst)
                    usage.then(" | ").color(ChatColor.RED);
                flagFirst = false;
                usage.then("-" + required).color(ChatColor.AQUA);
                if (!Strings.isNullOrEmpty(data.getDescription()))
                    usage.tooltip(ChatColor.GRAY + data.getDescription());
            }
            usage.then(") ").color(ChatColor.RED);
        }
        if (!parameters.isEmpty())
            for (int var8 = 0; var8 < parameters.size(); var8++) {
                ParameterData var9 = parameters.get(var8);
                boolean var10 = var9.getDefaultValue().isEmpty();
                usage.then((var10 ? "<" : "[") + var9.getName() + (var9.isWildcard() ? "..." : "") + (var10 ? ">" : "]") + ((var8 != parameters.size() - 1) ? " " : "")).color(ChatColor.RED);
            }
        return usage;
    }

    public boolean invoke(CommandSender sender, Arguments arguments) throws CommandException {
        if (this.method == null) {
            if (this.hasCommands()) {
                if (this.getSubCommands(sender, true).isEmpty()) {
                    if (this.isHidden()) {
                        sender.sendMessage(SpigotConfig.unknownCommandMessage);
                    } else {
                        sender.sendMessage(ChatColor.RED + "No permission.");
                    }
                }
            } else {
                sender.sendMessage(SpigotConfig.unknownCommandMessage);
            }

            return true;
        } else {
            List<Object> objects = new ArrayList(this.method.getParameterCount());
            objects.add(sender);
            int index = 0;
            Iterator var5 = this.parameters.iterator();

            while (true) {
                while (var5.hasNext()) {
                    Data unknownData = (Data) var5.next();
                    if (unknownData instanceof FlagData) {
                        FlagData flagData = (FlagData) unknownData;
                        boolean value = flagData.getDefaultValue();
                        Iterator var18 = flagData.getNames().iterator();

                        while (var18.hasNext()) {
                            String s = (String) var18.next();
                            if (arguments.hasFlag(s)) {
                                value = !value;
                                break;
                            }
                        }

                        objects.add(flagData.getMethodIndex(), value);
                    } else if (unknownData instanceof ParameterData) {
                        ParameterData parameterData = (ParameterData) unknownData;

                        String argument;
                        try {
                            argument = arguments.getArguments().get(index);
                        } catch (Exception var13) {
                            if (parameterData.getDefaultValue().isEmpty()) {
                                return false;
                            }

                            argument = parameterData.getDefaultValue();
                        }

                        if (parameterData.isWildcard() && (argument.isEmpty() || !argument.equals(parameterData.getDefaultValue()))) {
                            argument = arguments.join(index);
                        }

                        ParameterType<?> type = BricksCommandHandler.getParameterType(parameterData.getType());
                        if (parameterData.getParameterType() != null) {
                            try {
                                type = (ParameterType) parameterData.getParameterType().newInstance();
                            } catch (IllegalAccessException | InstantiationException var12) {
                                var12.printStackTrace();
                                throw new CommandException("Failed to create ParameterType instance: " + parameterData.getParameterType().getName(), var12);
                            }
                        }

                        if (type == null) {
                            Class<?> t = parameterData.getParameterType() == null ? parameterData.getType() : parameterData.getParameterType();
                            sender.sendMessage(ChatColor.RED + "No parameter type found: " + t.getSimpleName());
                            return true;
                        }

                        Object result = type.transform(sender, argument);
                        if (result == null) {
                            return true;
                        }

                        objects.add(parameterData.getMethodIndex(), result);
                        ++index;
                    }
                }

                try {
                    final Stopwatch stopwatch = new Stopwatch();
                    stopwatch.start();
                    this.method.invoke(null, objects.toArray());
                    stopwatch.stop();
                    final int executionThreshold = Library.getInstance().getConfig().getInt("Command.TimeThreshold", 10);
                    if (!this.async && stopwatch.elapsedMillis() >= executionThreshold) { //&& this.logToConsole
                        Library.getInstance().getLogger().warning("Command '/" + this.getFullLabel() + "' took " + stopwatch.elapsedMillis() + "ms!");
                    }
                    return true;
                } catch (InvocationTargetException | IllegalAccessException var11) {
                    var11.printStackTrace();
                    throw new CommandException("An error occurred while executing the command", var11);
                }
            }
        }
    }

    public List<String> getSubCommands(CommandSender sender, boolean print) {
        List<String> commands = new ArrayList();
        if (this.canUse(sender)) {
            String command = (sender instanceof Player ? "/" : "") + this.getFullLabel() + (this.parameters != null ? " " + this.getUsage().toOldMessageFormat() : "") + (!Strings.isNullOrEmpty(this.description) ? ChatColor.GRAY + " - " + this.getDescription() : "");
            if (this.parent == null) {
                commands.add(command);
            } else if (this.parent.getName() != null && BricksCommandHandler.ROOT_NODE.getCommand(this.parent.getName()) != this.parent) {
                commands.add(command);
            }

            if (this.hasCommands()) {
                Iterator var5 = this.getChildren().values().iterator();

                while (var5.hasNext()) {
                    CommandNode n = (CommandNode) var5.next();
                    commands.addAll(n.getSubCommands(sender, false));
                }
            }
        }

        if (!commands.isEmpty() && print) {
            sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 35));
            Iterator var7 = commands.iterator();

            while (var7.hasNext()) {
                String command = (String) var7.next();
                sender.sendMessage(ChatColor.RED + command);
            }

            sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 35));
        }

        return commands;
    }

    public Set<String> getRealAliases() {
        Set<String> aliases = this.getAliases();
        aliases.remove(this.getName());
        return aliases;
    }

    public String getFullLabel() {
        List<String> labels = new ArrayList();

        for (CommandNode node = this; node != null; node = node.getParent()) {
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
        return builder.toString().trim();
    }

    public String getUsageForHelpTopic() {
        return this.method != null && this.parameters != null ? "/" + this.getFullLabel() + " " + ChatColor.stripColor(this.getUsage().toOldMessageFormat()) : "";
    }

    @ConstructorProperties({"name", "permission"})
    public CommandNode(@NonNull String name, @NonNull String permission) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.name = name;
            this.permission = permission;
        }
    }

    @ConstructorProperties({"name", "aliases", "permission", "description", "async", "requiresplayer", "shouldcomplete", "hidden", "method", "owningClass", "validFlags", "parameters", "children", "parent"})
    public CommandNode(@NonNull String name, Set<String> aliases, @NonNull String permission, String description, boolean async, boolean requiresplayer, boolean shouldcomplete, boolean hidden, Method method, Class<?> owningClass, List<String> validFlags, List<Data> parameters, Map<String, CommandNode> children, CommandNode parent) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.name = name;
            this.aliases = aliases;
            this.permission = permission;
            this.description = description;
            this.async = async;
            this.requiresplayer = requiresplayer;
            this.shouldcomplete = shouldcomplete;
            this.hidden = hidden;
            this.method = method;
            this.owningClass = owningClass;
            this.validFlags = validFlags;
            this.parameters = parameters;
            this.children = children;
            this.parent = parent;
        }
    }

    public CommandNode() {
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name");
        } else {
            this.name = name;
        }
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    @NonNull
    public String getPermission() {
        return this.permission;
    }

    public void setPermission(@NonNull String permission) {
        if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.permission = permission;
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAsync() {
        return this.async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isRequiresplayer() {
        return this.requiresplayer;
    }

    public void setRequiresplayer(boolean requiresplayer) {
        this.requiresplayer = requiresplayer;
    }

    public boolean ShouldComplete() {
        return this.shouldcomplete;
    }

    public void setShouldComplete(boolean shouldcomplete) {
        this.shouldcomplete = shouldcomplete;
    }


    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getOwningClass() {
        return this.owningClass;
    }

    public List<String> getValidFlags() {
        return this.validFlags;
    }

    public void setValidFlags(List<String> validFlags) {
        this.validFlags = validFlags;
    }

    public List<Data> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<Data> parameters) {
        this.parameters = parameters;
    }

    public Map<String, CommandNode> getChildren() {
        return this.children;
    }

    public CommandNode getParent() {
        return this.parent;
    }

    public void setParent(CommandNode parent) {
        this.parent = parent;
    }
}