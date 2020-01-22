package me.elsiff.morefish.fishing

import me.elsiff.morefish.configuration.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

class FishPond {
    private var wid: UUID? = null
    private var x:   Int?  = 0
    private var z:   Int?  = 0
    private var x1:  Int?  = 0
    private var z1:  Int?  = 0

    init {
        //fish-pond: world,17,17,39,39
        val ss = Config.standard.string("fish-pond").split(",")
        if (ss.isNotEmpty()) wid = Bukkit.getWorld(ss[0])?.uid
        if (ss.size > 1) x  = ss[1].toInt()
        if (ss.size > 2) z  = ss[2].toInt()
        if (ss.size > 3) x1 = ss[3].toInt()
        if (ss.size > 4) z1 = ss[4].toInt()
    }

    fun check(loction: Location): Boolean? {
        return loction.world?.uid?.equals(wid)
    }
}