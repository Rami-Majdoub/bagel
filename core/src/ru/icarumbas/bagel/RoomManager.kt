package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.SerializedMapObject


class RoomManager(val rooms: ArrayList<Room>,
                  private val assets: AssetManager,
                  private val entityCreator: EntityCreator,
                  private val engine: Engine,
                  private val serializedObjects: ArrayList<SerializedMapObject>,
                  private val worldIO: WorldIO){

    var currentMapId = 0

    fun path(id: Int = currentMapId) = rooms[id].path

    fun size() = rooms.size

    fun width(id: Int = currentMapId) = rooms[id].width

    fun height(id: Int = currentMapId) = rooms[id].height

    fun pass(side: Int, id: Int = currentMapId) = rooms[id].passes[side]

    fun mesh(cell: Int, id: Int = currentMapId) = rooms[id].meshCoords[cell]

    private fun createStaticEntities(){
        (0 until TILED_MAPS_TOTAL).forEach {
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "lighting")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "torch")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "ground")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "platform")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "spikes")
        }
    }

    private fun createIdEntity(roomPath: String,
                               roomId: Int,
                               objectPath: String,
                               randomEnd: Int = 1,
                               atlas: TextureAtlas = assets["Packs/items.pack", TextureAtlas::class.java]){


        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]
        layer?.objects?.filterIsInstance<RectangleMapObject>()?.forEach {
            val rand = MathUtils.random(1, randomEnd)
            if (entityCreator.loadIdEntity(roomId, it.rectangle, objectPath, atlas, rand)){
                serializedObjects.add(SerializedMapObject(roomId, it.rectangle, objectPath, rand))
                Mappers.roomId[engine.entities.last()].serialized = serializedObjects.last()
            }

        }

    }

    fun createRoom(assetManager: AssetManager, path: String, id: Int): Room {
        return Room(assetManager, path, id)
    }

    fun createNewWorld(worldCreator: WorldCreator, assetManager: AssetManager) {
        rooms.add(createRoom(assetManager, "Maps/Map0.tmx", 0))
        rooms[currentMapId].meshCoords = intArrayOf(25, 25, 25, 25)
        worldCreator.createWorld(100, this)
        createStaticEntities()

        rooms.forEach {
            createIdEntity(it.path, it.id, "vase", 5)
            createIdEntity(it.path, it.id, "chair1", 3)
            createIdEntity(it.path, it.id, "chair2", 3)
            createIdEntity(it.path, it.id, "table", 3)
            createIdEntity(it.path, it.id, "chandelier")
            createIdEntity(it.path, it.id, "window", 2)
            createIdEntity(it.path, it.id, "crateBarrel", 3)
            createIdEntity(it.path, it.id, "smallBanner", 2)
            createIdEntity(it.path, it.id, "chest", 2)
            createIdEntity(it.path, it.id, "candle")
            createIdEntity(it.path, it.id, "door", 2)
            createIdEntity(it.path, it.id, "groundEnemy", 5)

        }

        worldIO.prefs.putString("Continue", "Yes")
        worldIO.prefs.flush()
    }

    fun continueWorld() {
        worldIO.loadWorld(serializedObjects, rooms)
        createStaticEntities()
        serializedObjects.forEach{
            entityCreator.loadIdEntity(it.roomId, it.rect, it.objectPath, assets["Packs/items.pack", TextureAtlas::class.java], it.rand)
            Mappers.roomId[engine.entities.last()].serialized = it
        }
    }

}