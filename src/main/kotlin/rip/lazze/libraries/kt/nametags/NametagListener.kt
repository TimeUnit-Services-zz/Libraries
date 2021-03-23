package rip.lazze.libraries.kt.nametags

import rip.lazze.libraries.Library
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

internal class NametagListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.setMetadata(
            "starkNametag-LoggedIn",
            FixedMetadataValue(Library.getInstance(), true) as MetadataValue
        )
        Library.getInstance().nametagEngine.initiatePlayer(event.player)
        Library.getInstance().nametagEngine.reloadPlayer(event.player)
        Library.getInstance().nametagEngine.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.removeMetadata("starkNametag-LoggedIn", Library.getInstance())
        Library.getInstance().nametagEngine.teamMap.remove(event.player.name)
    }
}