package rip.lazze.libraries.config;

import rip.lazze.libraries.config.annotations.ConfigData;
import rip.lazze.libraries.config.annotations.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configuration {
    private YamlConfiguration config;

    private JavaPlugin plugin;

    private File file;

    private File directory;

    public Configuration(JavaPlugin plugin) {
        this(plugin, "config.yml");
    }

    public Configuration(JavaPlugin plugin, String filename) {
        this(plugin, filename, plugin.getDataFolder().getPath());
    }

    public Configuration(JavaPlugin plugin, String filename, String directory) {
        this.plugin = plugin;
        this.directory = new File(directory);
        this.file = new File(directory, filename);
        this.config = new YamlConfiguration();
        createFile();
    }

    public void createFile() {
        if (!this.directory.exists())
            this.directory.mkdirs();
        if (!this.file.exists())
            try {
                this.file.createNewFile();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        try {
            this.config.load(this.file);
        } catch (InvalidConfigurationException | IOException var2) {
            var2.printStackTrace();
        }
    }

    public void save() {
        Field[] toSave = getClass().getDeclaredFields();
        Field[] e = toSave;
        int var3 = toSave.length;
        for (int var4 = 0; var4 < var3; var4++) {
            Field f = e[var4];
            if (f.isAnnotationPresent(ConfigData.class)) {
                ConfigData configData = f.getAnnotation(ConfigData.class);
                try {
                    f.setAccessible(true);
                    Object e1 = f.get(this);
                    Object configValue = null;
                    if (f.isAnnotationPresent(ConfigSerializer.class)) {
                        ConfigSerializer serializer = f.getAnnotation(ConfigSerializer.class);
                        if (e1 instanceof List) {
                            configValue = new ArrayList();
                            Iterator o = ((List) configValue).iterator();
                            while (o.hasNext()) {
                                Object o1 = o.next();
                                AbstractSerializer<Object> as = serializer.serializer().newInstance();
                                ((List<String>) configValue).add(as.toString(o1));
                            }
                        } else {
                            AbstractSerializer<Object> var16 = serializer.serializer().newInstance();
                            configValue = var16.toString(e1);
                        }
                    } else if (e1 instanceof List) {
                        configValue = new ArrayList();
                        Iterator var15 = ((List) e1).iterator();
                        while (var15.hasNext()) {
                            Object var17 = var15.next();
                            ((List<String>) configValue).add(var17.toString());
                        }
                    }
                    if (configValue == null)
                        configValue = e1;
                    this.config.addDefault(configData.path(), configValue);
                    this.config.set(configData.path(), configValue);
                    System.out.println("Setting: " + configData.path() + " to " + e1);
                } catch (InstantiationException | IllegalAccessException var14) {
                    var14.printStackTrace();
                }
            }
        }
        try {
            this.config.save(this.file);
        } catch (IOException var13) {
            var13.printStackTrace();
        }
    }

    public void load() {
        Field[] toLoad = getClass().getDeclaredFields();
        Field[] var2 = toLoad;
        int var3 = toLoad.length;
        for (int var4 = 0; var4 < var3; var4++) {
            Field f = var2[var4];
            f.setAccessible(true);
            System.out.println("Loading field: " + f.getName());
            if (f.isAnnotationPresent(ConfigData.class)) {
                ConfigData configData = f.<ConfigData>getAnnotation(ConfigData.class);
                System.out.println("Loading data: " + configData.path());
                if (this.config.contains(configData.path())) {
                    f.setAccessible(true);
                    if (!f.isAnnotationPresent(ConfigSerializer.class)) {
                        try {
                            System.out.println("Setting data: " + this.config.get(configData.path()));
                            if (this.config.isList(configData.path())) {
                                f.set(this, this.config.getList(configData.path()));
                            } else {
                                f.set(this, this.config.get(configData.path()));
                            }
                        } catch (IllegalAccessException var11) {
                            var11.printStackTrace();
                        }
                    } else if (this.config.isList(configData.path())) {
                        try {
                            List var14 = this.config.getStringList(configData.path());
                            ArrayList<Object> deserializedList = new ArrayList();
                            Iterator<String> var9 = var14.iterator();
                            while (var9.hasNext()) {
                                String s = var9.next();
                                deserializedList.add(deserializeValue(f, s));
                            }
                            f.set(this, deserializedList);
                        } catch (InstantiationException | IllegalAccessException var13) {
                            System.out.println("Error reading list in configuration file: " + this.config.getName() + " path: " + configData.path());
                            var13.printStackTrace();
                        }
                    } else {
                        try {
                            Object e = this.config.get(configData.path());
                            f.set(this, deserializeValue(f, e.toString()));
                        } catch (InstantiationException | IllegalAccessException var12) {
                            System.out.println("Error reading value in configuration file: " + this.config.getName() + " path: " + configData.path());
                            var12.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public File getFile() {
        return this.file;
    }

    public Object deserializeValue(Field f, Object value) throws IllegalAccessException, InstantiationException {
        AbstractSerializer serializer = f.getAnnotation(ConfigSerializer.class).serializer().newInstance();
        return serializer.fromString(value.toString());
    }
}

