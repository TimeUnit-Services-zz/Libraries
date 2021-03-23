package  rip.lazze.libraries.kt.tablist

import rip.lazze.libraries.Library
import org.bukkit.Bukkit

class TabThread : Thread("stark - Tab Thread") {

    private val protocolLib = Bukkit.getServer().pluginManager.getPlugin("ProtocolLib")

    init {
        this.isDaemon = true
    }

    override fun run() {
        while (Library.getInstance().isEnabled && protocolLib != null && protocolLib.isEnabled) {
            for (online in Library.getInstance().server.getOnlinePlayers()) {
                try {
                    Library.getInstance().tabEngine.updatePlayer(online)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            try {
                sleep(250L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }

        }
    }

}