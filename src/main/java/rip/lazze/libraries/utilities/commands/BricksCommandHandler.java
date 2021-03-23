package rip.lazze.libraries.utilities.commands;

import rip.lazze.libraries.Library;
import rip.lazze.libraries.utilities.ClassUtil;
import rip.lazze.libraries.utilities.commands.bukkit.BricksCommand;
import rip.lazze.libraries.utilities.commands.bukkit.BricksCommandMap;
import rip.lazze.libraries.utilities.commands.bukkit.BricksHelpTopic;
import rip.lazze.libraries.utilities.commands.param.ParameterType;
import rip.lazze.libraries.utilities.commands.parameter.filter.NormalFilter;
import rip.lazze.libraries.utilities.commands.parameter.filter.StrictFilter;
import rip.lazze.libraries.utilities.commands.parameter.offlineplayer.OfflinePlayerWrapper;
import rip.lazze.libraries.utilities.commands.parameter.offlineplayer.OfflinePlayerWrapperParameterType;
import rip.lazze.libraries.utilities.commands.utils.EasyClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rip.lazze.libraries.utilities.commands.parameter.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

public final class BricksCommandHandler {
    public static CommandNode ROOT_NODE = new CommandNode();
    protected static Map<Class<?>, ParameterType<?>> PARAMETER_TYPE_MAP = new HashMap();
    protected static CommandMap commandMap;
    protected static Map<String, Command> knownCommands;
    private static CommandConfiguration config = (new CommandConfiguration()).setNoPermissionMessage("&cNo permission.");

    public BricksCommandHandler() {
    }

    public static void init() {
        /*registerClass(BuildCommand.class);
        registerClass(EvalCommand.class);
        registerClass(CommandInfoCommand.class);
        registerClass(VisibilityDebugCommand.class);*/
        (new BukkitRunnable() {
            public void run() {
                try {
                    BricksCommandHandler.swapCommandMap();
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        }).runTaskLater(Library.getInstance(), 5L);
    }

    public static void registerParameterType(Class<?> clazz, ParameterType<?> type) {
        PARAMETER_TYPE_MAP.put(clazz, type);
    }

    public static ParameterType getParameterType(Class<?> clazz) {
        return PARAMETER_TYPE_MAP.get(clazz);
    }

    public static CommandConfiguration getConfig() {
        return config;
    }

    public static void setConfig(CommandConfiguration config) {
        BricksCommandHandler.config = config;
    }

    public static void registerMethod(Method method) {
        method.setAccessible(true);
        Set<CommandNode> nodes = (new MethodProcessor()).process(method);
        if (nodes != null) {
            nodes.forEach((node) -> {
                if (node != null) {
                    BricksCommand command = new BricksCommand(node, JavaPlugin.getProvidingPlugin(method.getDeclaringClass()));
                    register(command);
                    node.getChildren().values().forEach((n) -> {
                        registerHelpTopic(n, node.getAliases());
                    });
                }

            });
        }

    }

    protected static void registerHelpTopic(CommandNode node, Set<String> aliases) {
        if (node.method != null) {
            Bukkit.getHelpMap().addTopic(new BricksHelpTopic(node, aliases));
        }

        if (node.hasCommands()) {
            node.getChildren().values().forEach((n) -> {
                registerHelpTopic(n, null);
            });
        }

    }

    private static void register(BricksCommand command) {
        try {
            Map<String, Command> knownCommands = getKnownCommands();
            Iterator iterator = knownCommands.entrySet().iterator();

            while(iterator.hasNext()) {
                Entry<String, Command> entry = (Entry)iterator.next();
                if (entry.getValue().getName().equalsIgnoreCase(command.getName())) {
                    entry.getValue().unregister(commandMap);
                    iterator.remove();
                }
            }

            Iterator var6 = command.getAliases().iterator();

            while(var6.hasNext()) {
                String alias = (String)var6.next();
                knownCommands.put(alias, command);
            }

            command.register(commandMap);
            knownCommands.put(command.getName(), command);
        } catch (Exception var5) {
        }

    }

    public static void registerClass(Class<?> clazz) {
        Method[] var1 = clazz.getMethods();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Method method = var1[var3];
            registerMethod(method);
        }

    }

    public static void unregisterClass(Class<?> clazz) {
        Map<String, Command> knownCommands = getKnownCommands();
        Iterator iterator = knownCommands.values().iterator();

        while(iterator.hasNext()) {
            Command command = (Command)iterator.next();
            if (command instanceof BricksCommand) {
                CommandNode node = ((BricksCommand)command).getNode();
                if (node.getOwningClass() == clazz) {
                    command.unregister(commandMap);
                    iterator.remove();
                }
            }
        }

    }

    public static void registerPackage(Plugin plugin, String packageName) {
        ClassUtil.getClassesInPackage(plugin, packageName).forEach(BricksCommandHandler::registerClass);
    }

    public static void registerAll(Plugin plugin) {
        registerPackage(plugin, plugin.getClass().getPackage().getName());
    }

    private static void swapCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        Object oldCommandMap = commandMapField.get(Bukkit.getServer());
        BricksCommandMap newCommandMap = new BricksCommandMap(Bukkit.getServer());
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & -17);
        knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));
        commandMapField.set(Bukkit.getServer(), newCommandMap);
    }

    protected static CommandMap getCommandMap() {
        return (CommandMap)(new EasyClass(Bukkit.getServer())).getField("commandMap").get();
    }

    protected static Map<String, Command> getKnownCommands() {
        return (Map)(new EasyClass(commandMap)).getField("knownCommands").get();
    }

    static {
        registerParameterType(Boolean.TYPE, new BooleanParameterType());
        registerParameterType(Integer.TYPE, new IntegerParameterType());
        registerParameterType(Double.TYPE, new DoubleParameterType());
        registerParameterType(Float.TYPE, new FloatParameterType());
        registerParameterType(String.class, new StringParameterType());
        registerParameterType(GameMode.class, new GameModeParameterType());
        registerParameterType(Player.class, new PlayerParameterType());
        registerParameterType(World.class, new WorldParameterType());
        registerParameterType(ItemStack.class, new ItemStackParameterType());
        registerParameterType(OfflinePlayer.class, new OfflinePlayerParameterType());
        registerParameterType(UUID.class, new UUIDParameterType());
        registerParameterType(OfflinePlayerWrapper.class, new OfflinePlayerWrapperParameterType());
        registerParameterType(NormalFilter.class, new NormalFilter());
        registerParameterType(StrictFilter.class, new StrictFilter());
        commandMap = getCommandMap();
        knownCommands = getKnownCommands();
    }
}