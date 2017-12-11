package ru.icarumbas.bagel.engine.world


class RoomWorld {

    private val roomWorldCreator = RoomWorldCreator(50)

    val rooms = roomWorldCreator.createWorld()
    var currentMapId = 0


    fun getMapPath(id: Int = currentMapId) = rooms[id].path

    fun getRoomWidth(id: Int = currentMapId) = rooms[id].width

    fun getRoomHeight(id: Int = currentMapId) = rooms[id].height

    fun getRoomPass(pass: Int, id: Int = currentMapId) = rooms[id].passes[pass]

    fun getRoomMeshCoordinate(cell: Int, id: Int) = rooms[id].meshCoords[cell]

    fun getRoomForMeshCoordinate(x: Int, y: Int) = rooms.find { it.meshCoords[0] == x && it.meshCoords[1] == y }

}