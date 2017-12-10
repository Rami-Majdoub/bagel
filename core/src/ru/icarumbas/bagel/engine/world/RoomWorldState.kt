package ru.icarumbas.bagel.engine.world


interface RoomWorldState {

    fun getCurrentMapId(): Int

    fun setCurrentMapId(id: Int)

    fun getMapPath(id: Int = getCurrentMapId()): String

    fun getRooms(): ArrayList<Room>

    fun getRoomWidth(id: Int = getCurrentMapId()): Float

    fun getRoomHeight(id: Int = getCurrentMapId()): Float

    fun getRoomPass(pass: Int, id: Int = getCurrentMapId()): Int

    fun getRoomMeshCoordinate(cell: Int, id: Int = getCurrentMapId()): Int

    fun getRoomFor(x: Int, y: Int): Room?

}