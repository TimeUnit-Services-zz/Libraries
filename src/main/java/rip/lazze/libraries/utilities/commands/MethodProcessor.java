package rip.lazze.libraries.utilities.commands;

import com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rip.lazze.libraries.utilities.commands.param.Param;
import rip.lazze.libraries.utilities.commands.param.ParameterData;
import org.bukkit.command.CommandSender;
import rip.lazze.libraries.utilities.commands.param.Param;
import rip.lazze.libraries.utilities.commands.param.ParameterData;

public class MethodProcessor implements Processor<Method, Set<CommandNode>> {
    public MethodProcessor() {
    }

    public Set<CommandNode> process(Method value) {
        if (value.isAnnotationPresent(Command.class) && value.getParameterCount() >= 1 && CommandSender.class.isAssignableFrom(value.getParameterTypes()[0])) {
            Command command = (Command)value.getAnnotation(Command.class);
            Class<?> owningClass = value.getDeclaringClass();
            List<String> flagNames = new ArrayList();
            List<Data> allParams = new ArrayList();
            if (value.getParameterCount() > 1) {
                for(int i = 1; i < value.getParameterCount(); ++i) {
                    Parameter parameter = value.getParameters()[i];
                    if (parameter.isAnnotationPresent(Param.class)) {
                        Param param = (Param)parameter.getAnnotation(Param.class);
                        ParameterData data = new ParameterData(param.name(), param.defaultValue(), parameter.getType(), param.wildcard(), i, Sets.newHashSet(param.tabCompleteFlags()), parameter.isAnnotationPresent(Type.class) ? ((Type)parameter.getAnnotation(Type.class)).value() : null);
                        allParams.add(data);
                    } else {
                        if (!parameter.isAnnotationPresent(Flag.class)) {
                            throw new IllegalArgumentException("Every parameter, other than the sender, must have the Param or the Flag annotation! (" + value.getDeclaringClass().getName() + ":" + value.getName() + ")");
                        }

                        Flag flag = (Flag)parameter.getAnnotation(Flag.class);
                        FlagData data = new FlagData(Arrays.asList(flag.value()), flag.description(), flag.defaultValue(), i);
                        allParams.add(data);
                        flagNames.addAll(Arrays.asList(flag.value()));
                    }
                }
            }

            Set<CommandNode> registered = new HashSet();
            String[] var22 = command.names();
            int var24 = var22.length;

            for(int var26 = 0; var26 < var24; ++var26) {
                String name = var22[var26];
                boolean first = true;
                boolean change = true;
                boolean hadChild = false;
                name = name.toLowerCase().trim();
                String[] cmdNames;
                if (name.contains(" ")) {
                    cmdNames = name.split(" ");
                } else {
                    cmdNames = new String[]{name};
                }

                String primary = cmdNames[0];
                CommandNode workingNode = new CommandNode(owningClass);
                if (BricksCommandHandler.ROOT_NODE.hasCommand(primary)) {
                    workingNode = BricksCommandHandler.ROOT_NODE.getCommand(primary);
                    change = false;
                }

                if (change) {
                    workingNode.setName(cmdNames[0]);
                } else {
                    workingNode.getAliases().add(cmdNames[0]);
                }

                CommandNode parentNode = new CommandNode(owningClass);
                if (workingNode.hasCommand(cmdNames[0])) {
                    parentNode = workingNode.getCommand(cmdNames[0]);
                } else {
                    parentNode.setName(cmdNames[0]);
                    parentNode.setPermission("");
                }

                if (cmdNames.length > 1) {
                    hadChild = true;
                    workingNode.registerCommand(parentNode);
                    CommandNode childNode = new CommandNode(owningClass);

                    for(int i = 1; i < cmdNames.length; ++i) {
                        String subName = cmdNames[i];
                        childNode.setName(subName);
                        if (parentNode.hasCommand(subName)) {
                            childNode = parentNode.getCommand(subName);
                        }

                        parentNode.registerCommand(childNode);
                        if (i == cmdNames.length - 1) {
                            childNode.setMethod(value);
                            childNode.setAsync(command.async());
                            childNode.setRequiresplayer(command.requiresPlayer());
                            childNode.setHidden(command.hidden());
                            childNode.setPermission(command.permission());
                            childNode.setDescription(command.description());
                            childNode.setValidFlags(flagNames);
                            childNode.setParameters(allParams);
                        } else {
                            parentNode = childNode;
                            childNode = new CommandNode(owningClass);
                        }
                    }
                }

                if (!hadChild) {
                    parentNode.setMethod(value);
                    parentNode.setAsync(command.async());
                    parentNode.setRequiresplayer(command.requiresPlayer());
                    parentNode.setHidden(command.hidden());
                    parentNode.setPermission(command.permission());
                    parentNode.setDescription(command.description());
                    parentNode.setValidFlags(flagNames);
                    parentNode.setParameters(allParams);
                    workingNode.registerCommand(parentNode);
                }

                first = false;
                BricksCommandHandler.ROOT_NODE.registerCommand(workingNode);
                registered.add(workingNode);
            }

            return registered;
        } else {
            return null;
        }
    }
}
