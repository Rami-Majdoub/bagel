package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.RoomManager


private val id = Mappers.roomId
private val static = Mappers.static
private val pl = Mappers.player
private val ai = Mappers.AI
private val plWeapon = Mappers.alwaysRender


fun Entity.inView(rm: RoomManager): Boolean {
         return (id.has(this) && id[this].id == rm.currentMapId) ||
                 (static.has(this) && static[this].mapPath == rm.path()) ||
                 pl.has(this) || plWeapon.has(this)
}

fun Entity.rotatedRight(): Boolean{
    return when {
        ai.has(this) -> ai[this].isTargetRight
        pl.has(this) -> pl[this].lastRight
        else -> true
    }
}
