package ru.icarumbas.bagel


interface RoomWorldState {

    fun currentMapId(): Int

    fun mapPath(id: Int = currentMapId()): String

    fun rooms(): ArrayList<Room>

    fun roomWidth(id: Int = currentMapId()): Float

    fun roomHeight(id: Int = currentMapId()): Float

    fun roomPass(pass: Int, id: Int = currentMapId()): Int

    fun roomMeshCoordinate(cell: Int, id: Int = currentMapId()): Int

    fun roomFor(x: Int, y: Int): Room?

}