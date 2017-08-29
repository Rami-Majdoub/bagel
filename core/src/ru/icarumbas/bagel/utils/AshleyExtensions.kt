package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.Room
import ru.icarumbas.bagel.RoomManager


private val id = Mappers.roomId
private val static = Mappers.static
private val pl = Mappers.player
private val ai = Mappers.ai
private val run = Mappers.run
private val plWeapon = Mappers.plWeapon


fun Entity.inView(rm: RoomManager): Boolean {
         return (id.has(this) && id[this].id == rm.currentMapId) ||
                 (static.has(this) && static[this].mapPath == rm.path()) ||
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
