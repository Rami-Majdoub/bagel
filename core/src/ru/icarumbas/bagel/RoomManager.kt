package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator


class RoomManager(val rooms: ArrayList<Room>,
                  private val assets: AssetManager,
                  private val entityCreator: EntityCreator){

    var currentMapId = 0

    fun path(id: Int = currentMapId) = rooms[id].path

    fun size() = rooms.size

    fun width(id: Int = currentMapId) = rooms[id].width

    fun height(id: Int = currentMapId) = rooms[id].height

    fun pass(side: Int, id: Int = currentMapId) = rooms[id].passes[side]

    fun mesh(cell: Int, id: Int = currentMapId) = rooms[id].meshCoords[cell]

    fun loadEntities(engine: Engine){

        (0..TILED_MAPS_TOTAL).forEach {
            loadStaticMapObject("Maps/New/map$it.tmx", "lighting", engine, assets["Packs/items.pack", TextureAtlas::class.java])
            loadStaticMapObject("Maps/New/map$it.tmx", "ground", engine)
            loadStaticMapObject("Maps/New/map$it.tmx", "platform", engine)
        }

        rooms.forEach {
//            loadDynamicMamObject()
        }

    }

    private fun loadDynamicMamObject(
                             objectPath: String,
                             engine: Engine,
                             roomId: Int,
                             textureAtlas: TextureAtlas) {
        /*val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.filterIsInstance<RectangleMapObject>()?.forEach {
            engine.addEntity(when(objectPath){
                "boxes" -> createBoxEntity(it.rectangle, roomId)
                "chandeliers" -> createChandelierEntity(it.rectangle, roomId)
                "chests" -> Entity()
                "statues" -> Entity()
                "spikeTraps" -> Entity()
                "spikes" -> Entity()
                "portalDoor" -> Entity()
                "chairs" -> Entity()
                "tables" -> Entity()
                else -> throw Exception("NO SUCH CLASS")
            })
        }*/
    }

    private fun loadStaticMapObject(roomPath: String,
                            objectPath: String,
                            engine: Engine,
                            textureAtlas: TextureAtlas? = null){

        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.forEach {
            engine.addEntity(when(objectPath){
                "spikeTraps" -> Entity()
                "spikes" -> Entity()
                "portalDoor" -> Entity()
                "lighting" -> entityCreator.createLightingEntity(it, roomPath, textureAtlas!!)
                "ground" -> entityCreator.createGroundEntity(it, roomPath, GROUND_BIT)
                "platform" -> entityCreator.createGroundEntity(it, roomPath, PLATFORM_BIT)
                else -> throw Exception("NO SUCH CLASS")
            })
        }
    }

    fun createRoom(assetManager: AssetManager, path: String, id: Int): Room {
        return Room(assetManager, path, id)
    }

    fun createNewWorld(worldCreator: WorldCreator, assetManager: AssetManager) {
        rooms.add(createRoom(assetManager, "Maps/New/map0.tmx", 0))
        rooms[currentMapId].meshCoords = intArrayOf(25, 25, 25, 25)
        //worldCreator.createWorld(100, this)
    }

    fun continueWorld() {
        /*rooms = game.worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = game.worldIO.preferences.getInteger("CurrentMap")
        player.money = game.worldIO.preferences.getInteger("Money")
        game.worldIO.loadLastPlayerState(player)
        player.HP = game.worldIO.preferences.getInteger("HP")*/

    }

}