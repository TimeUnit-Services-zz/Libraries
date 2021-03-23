package  rip.lazze.libraries.kt.tablist

import org.bukkit.entity.Player

interface LayoutProvider {

    fun provide(player: Player): TabLayout

}