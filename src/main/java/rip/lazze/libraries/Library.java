package rip.lazze.libraries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rip.lazze.libraries.kt.nametags.NametagEngine;
import rip.lazze.libraries.kt.scoreboard.ScoreboardEngine;
import rip.lazze.libraries.kt.tablist.TabEngine;
import rip.lazze.libraries.redis.RedisCommand;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import rip.lazze.libraries.serialization.*;

public class Library extends JavaPlugin {

    public static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).setPrettyPrinting().serializeNulls().create();
    public static final Gson PLAIN_GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).serializeNulls().create();
    public static Library instance;
    public static boolean testing;
    public ScoreboardEngine scoreboardEngine;
    public TabEngine tabEngine;
    public NametagEngine nametagEngine;
    private JedisPool localJedisPool;
    private JedisPool backboneJedisPool;
    private long localRedisLastError;
    private long backboneRedisLastError;

    public static Library getInstance() {
        return Library.instance;
    }

    public void onEnable() {

        instance = this;
        testing = this.getConfig().getBoolean("testing", false);
        this.saveDefaultConfig();
        try {
            this.localJedisPool = new JedisPool(new JedisPoolConfig(), getConfig().getString("Redis.Host"), 6379, 20000, !getConfig().contains("Redis.Password") ? null : getConfig().getString("Redis.Password"), getConfig().getInt("Redis.DbId", 0));
        } catch (Exception e) {
            this.localJedisPool = null;
            e.printStackTrace();
            this.getLogger().warning("Couldn't connect to a Redis instance at " + this.getConfig().getString("Redis.Host") + ".");
        }
        try {
            this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), getConfig().getString("BackboneRedis.Host"), 6379, 20000, !getConfig().contains("BackboneRedis.Password") ? null : getConfig().getString("BackboneRedis.Password"), getConfig().getInt("BackboneRedis.DbId", 0));
        } catch (Exception e) {
            this.backboneJedisPool = null;
            e.printStackTrace();
            this.getLogger().warning("Couldn't connect to a Backbone Redis instance at " + this.getConfig().getString("BackboneRedis.Host") + ".");
        }

        scoreboardEngine = new ScoreboardEngine();
        scoreboardEngine.load();
        tabEngine = new TabEngine();
        tabEngine.load();
        nametagEngine = new NametagEngine();
        nametagEngine.load();
    }

    public void onDisable() {
        this.localJedisPool.close();
        this.backboneJedisPool.close();
    }

    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = this.localJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            if (jedis != null) {
                this.localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null)
                this.localJedisPool.returnResource(jedis);
        }
        return result;
    }

    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {
        if (testing)
            return null;
        Jedis jedis = this.backboneJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        } catch (Exception var8) {
            var8.printStackTrace();
            this.backboneRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null)
                this.backboneJedisPool.returnResource(jedis);
        }
        return result;
    }

    @Deprecated
    public long getLocalRedisLastError() {
        return this.localRedisLastError;
    }

    @Deprecated
    public long getBackboneRedisLastError() {
        return this.backboneRedisLastError;
    }

    public JedisPool getLocalJedisPool() {
        return this.localJedisPool;
    }

    public JedisPool getBackboneJedisPool() {
        return this.backboneJedisPool;
    }


}