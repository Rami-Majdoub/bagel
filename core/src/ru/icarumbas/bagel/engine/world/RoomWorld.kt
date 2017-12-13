package ru.icarumbas.bagel.engine.world

import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.io.WorldInfo
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.view.renderer.MapRenderer
import kotlin.properties.Delegates


class RoomWorld(

        val assets: ResourceManager,
        mapRenderer: MapRenderer

) {

    lateinit var rooms: ArrayList<Room>
    lateinit var mesh: Array<IntArray>

    var currentMapId: Int by Delegates.observable(0) {
        _, _, new ->
        mapRenderer.renderer.map = assets.getTiledMap(rooms[new].path)
    }

    fun createNewWorld(){
        RoomWorldCreator(50, assets).also {
            rooms = it.createWorld()
            mesh = it.mesh
        }
    }

    fun loadWorld(worldIO: WorldIO){
        with (worldIO.loadWorldInfo()) {
            this@RoomWorld.rooms = rooms
            this@RoomWorld.mesh = mesh
        }
    }

    fun saveWorld(worldIO: WorldIO){
        worldIO.saveInfo(WorldInfo(rooms, mesh))
    }

    fun getMapPath(id: Int = currentMapId) = rooms[id].path

    fun getRoomWidth(id: Int = currentMapId) = rooms[id].width

    fun getRoomHeight(id: Int = currentMapId) = rooms[id].height

    fun getRoomPass(pass: Int, id: Int = currentMapId) = rooms[id].passes[pass]

    fun getRoomMeshCoordinate(cell: Int, id: Int = currentMapId) = rooms[id].meshCoords[cell]

    fun getRoomForMeshCoordinate(x: Int, y: Int) = rooms.find { it.meshCoords[0] == x && it.meshCoords[1] == y }

}