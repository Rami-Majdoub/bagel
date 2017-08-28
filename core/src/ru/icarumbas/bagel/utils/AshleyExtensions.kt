package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.Room


private val id = Mappers.roomId
private val static = Mappers.static
private val pl = Mappers.player
private val ai = Mappers.ai
private val run = Mappers.run
private val plWeapon = Mappers.plWeapon


fun Entity.inView(currentMapId: Int, rooms: ArrayList<Room>): Boolean {
         return (id.has(this) && id[this].id == currentMapId) ||
                 (static.has(this) && static[this].mapPath == rooms[currentMapId].path) ||
                 pl.has(this) || plWeapon.has(this)
}

fun Entity.rotatedRight(): Boolean{
    if (ai.has(this)) {
        return ai[this].isPlayerRight
    } else
        if (run.has(this)){
            return run[this].lastRight
        } else
            return true
}
