package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.Room


private val id = Mappers.roomId
private val static = Mappers.static
private val pl = Mappers.player


fun Entity.inView(currentMapId: Int, rooms: ArrayList<Room>): Boolean {
         return (id.has(this) && id[this].id == currentMapId) ||
                 (static.has(this) && static[this].mapPath == rooms[currentMapId].path) ||
                 pl.has(this)
}
