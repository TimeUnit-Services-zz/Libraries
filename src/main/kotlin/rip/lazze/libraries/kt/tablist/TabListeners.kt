package  rip.lazze.libraries.kt.tablist

import rip.lazze.libraries.Library
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable

class TabListeners : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                Library.getInstance().tabEngine.addPlayer(event.player)
            }
        }.runTaskLater(Library.getInstance(), 10L)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        Library.getInstance().tabEngine.removePlayer(event.player)
        TabLayout.remove(event.player)
    }

}