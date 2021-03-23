package rip.lazze.libraries.utilities;

import com.google.common.collect.ImmutableSet;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

public final class ClassUtil {
    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        for (URL url : ClasspathHelper.forClassLoader(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader(), plugin.getClass().getClassLoader())) {
            Vfs.Dir dir = Vfs.fromURL(url);
            try {
                for (Vfs.File file : dir.getFiles()) {
                    String name = file.getRelativePath().replace("/", ".").replace(".class", "");
                    if (name.startsWith(packageName))
                        classes.add(Class.forName(name));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                dir.close();
            }
        }
        return ImmutableSet.copyOf(classes);
    }
}