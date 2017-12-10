package ru.icarumbas.bagel.engine.io

import ru.icarumbas.bagel.engine.world.Room


sealed class IOInfo

class WorldInfo(
        val rooms: ArrayList<Room>,
        val mesh: Array<IntArray>
) : IOInfo()

class PlayerInfo(
        val position: Pair<Float, Float>,
        val currentMap: Int
) : IOInfo()

class MinimapInfo(
        val openedRooms: ArrayList<Int>
) : IOInfo()

class EntitiesInfo(
        val mapObjects: ArrayList<SerializedMapObject>
) : IOInfo()