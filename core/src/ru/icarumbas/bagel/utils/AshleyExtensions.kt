package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.engine.world.RoomWorldState


private val id = Mappers.roomId
private val static = Mappers.static
private val pl = Mappers.player
private val ai = Mappers.AI
private val plWeapon = Mappers.alwaysRender


inline fun <reified T : Component> mapperFor(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)

fun Entity.inView(rooms: RoomWorldState) = this.inView(rooms, rooms.getCurrentMapId())

fun Entity.inView(rooms: RoomWorldState, mapId: Int): Boolean {
    return (id.has(this) && id[this].id == mapId) ||
            (static.has(this) && static[this].mapPath == rooms.getMapPath(mapId)) ||
            pl.has(this) || plWeapon.has(this)
}

fun Entity.rotatedRight(): Boolean{
    return when {
        ai.has(this) -> ai[this].isTargetRight
        pl.has(this) -> pl[this].lastRight
        else -> true
    }
}
