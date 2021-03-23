package rip.lazze.libraries.utilities.commands.parameter.offlineplayer;

import java.util.UUID;

import rip.lazze.libraries.Library;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.lazze.libraries.kt.Callback;

public class OfflinePlayerWrapper {
    private String source;
    private UUID uniqueId;
    private String name;

    public OfflinePlayerWrapper(String source) {
        this.source = source;
    }

    public void loadAsync(final Callback<Player> callback) {
        (new BukkitRunnable() {
            public void run() {
                final Player player = OfflinePlayerWrapper.this.loadSync();
                (new BukkitRunnable() {
                    public void run() {
                        callback.callback(player);
                    }
                }).runTask(Library.getInstance());
            }
        }).runTaskAsynchronously(Library.getInstance());
    }

    public Player loadSync() {
        MinecraftServer server;
        EntityPlayer entity;
        CraftPlayer player;
        if ((this.source.charAt(0) == '"' || this.source.charAt(0) == '\'') && (this.source.charAt(this.source.length() - 1) == '"' || this.source.charAt(this.source.length() - 1) == '\'')) {
            this.source = this.source.replace("'", "").replace("\"", "");
            this.uniqueId = Bukkit.getOfflinePlayer(this.source).getUniqueId();
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            } else {
                this.name = Bukkit.getOfflinePlayer(this.uniqueId).getName();
                if (Bukkit.getPlayer(this.uniqueId) != null) {
                    return Bukkit.getPlayer(this.uniqueId);
                } else if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                    return null;
                } else {
                    server = ((CraftServer)Bukkit.getServer()).getServer();
                    entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
                    player = entity.getBukkitEntity();
                    if (player != null) {
                        player.loadData();
                    }

                    return player;
                }
            }
        } else if (Bukkit.getPlayer(this.source) != null) {
            return Bukkit.getPlayer(this.source);
        } else {
            this.uniqueId = Bukkit.getOfflinePlayer(this.source).getUniqueId();
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            } else {
                this.name = Bukkit.getOfflinePlayer(this.uniqueId).getName();
                if (Bukkit.getPlayer(this.uniqueId) != null) {
                    return Bukkit.getPlayer(this.uniqueId);
                } else if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                    return null;
                } else {
                    server = ((CraftServer)Bukkit.getServer()).getServer();
                    entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
                    player = entity.getBukkitEntity();
                    if (player != null) {
                        player.loadData();
                    }

                    return player;
                }
            }
        }
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }
}
