package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.engine.world.RoomWorld


fun Entity.inView(rooms: RoomWorld) = this.inView(rooms, rooms.currentMapId)

fun Entity.inView(rooms: RoomWorld, mapId: Int): Boolean {
    return (roomId.has(this) && roomId[this].id == mapId) ||
            (statik.has(this) && statik[this].mapPath == rooms.getMapPath(mapId)) ||
            (alwaysRender.has(this))
}

fun Entity.rotatedRight(): Boolean{
    return when {
        AI.has(this) -> AI[this].isTargetRight
        player.has(this) -> player[this].lastRight
        else -> true
    }
}
