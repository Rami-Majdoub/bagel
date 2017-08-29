package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager


class RoomManager(val rooms: ArrayList<Room>){

    var currentMapId = 0

    fun path() = rooms[currentMapId].path

    fun size() = rooms.size

    fun width(id: Int = currentMapId) = rooms[id].width

    fun height(id: Int = currentMapId) = rooms[id].height

    fun pass(side: Int, id: Int = currentMapId) = rooms[id].passes[side]

    fun mesh(cell: Int, id: Int = currentMapId) = rooms[id].meshCoords[cell]

    private fun loadEntities(b2DWorldCreator: B2DWorldCreator, engine: Engine){

        rooms.forEach {
            b2DWorldCreator.loadMapObject(it.path, "boxes", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "chandeliers", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "chests", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "statues", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "spikeTraps", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "spikes", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "portalDoor", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "chairs", engine, it.id)
            b2DWorldCreator.loadMapObject(it.path, "tables", engine, it.id)
        }

    }

    fun createRoom(assetManager: AssetManager, path: String, id: Int): Room {
        return Room(assetManager, path, id)
    }

    fun createNewWorld(worldCreator: WorldCreator,
                       b2DWorldCreator: B2DWorldCreator,
                       assetManager: AssetManager,
                       engine: Engine) {
        rooms.add(createRoom(assetManager, "Maps/Map4.tmx", 0))
        rooms[currentMapId].meshCoords = intArrayOf(25, 25, 25, 25)
        worldCreator.createWorld(100, this)
        loadEntities(b2DWorldCreator, engine)
    }

    fun continueWorld() {
        /*rooms = game.worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = game.worldIO.preferences.getInteger("CurrentMap")
        player.money = game.worldIO.preferences.getInteger("Money")
        game.worldIO.loadLastPlayerState(player)
        player.HP = game.worldIO.preferences.getInteger("HP")*/

    }

}