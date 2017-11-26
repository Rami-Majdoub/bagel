package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.creators.WorldCreator
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.roomId


class RoomManager(val rooms: ArrayList<Room>,
                  private val assets: AssetManager,
                  private val engine: Engine,
                  private val serializedObjects: ArrayList<SerializedMapObject>,
                  private val worldIO: WorldIO,
                  private val playerEntity: Entity): RoomWorldState{

    private var currentMapId = 0
    lateinit var mesh: Array<IntArray>

    override fun currentMapId() = currentMapId

    override fun mapPath(id: Int) = rooms[id].path

    override fun rooms() = rooms

    override fun roomWidth(id: Int) = rooms[id].width

    override fun roomHeight(id: Int) = rooms[id].height

    override fun roomPass(pass: Int, id: Int) = rooms[id].passes[pass]

    override fun roomMeshCoordinate(cell: Int, id: Int) = rooms[id].meshCoords[cell]

    override fun roomFor(x: Int, y: Int): Room? {
        rooms.forEach {
            if ( (it.meshCoords[0] == x && it.meshCoords[1] == y)) {
                return it
            }
        }

        return null
    }


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
            if (entityCreator.loadIdEntity(roomId, it.rectangle, objectPath, atlas, rand, playerEntity)){
                serializedObjects.add(SerializedMapObject(roomId, it.rectangle, objectPath, rand))
                Mappers.roomId[engine.entities.last()].serialized = serializedObjects.last()
            }

        }

    }

    fun createRoom(assetManager: AssetManager, path: String, id: Int): Room {
        return Room(assetManager, path, id)
    }

    fun createNewWorld(worldCreator: WorldCreator, assetManager: AssetManager) {
        rooms.add(createRoom(assetManager, "Maps/Map9.tmx", 0))
        rooms[currentMapId].meshCoords = intArrayOf(25, 25, 25, 25)
        worldCreator.createWorld(50, this)
        mesh = worldCreator.mesh
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
            createIdEntity(it.path, it.id, "flyingEnemy")

        }

        worldIO.prefs.putString("Continue", "Yes")
        worldIO.prefs.flush()
    }

    fun continueWorld() {

        mesh = worldIO.loadMesh()
        worldIO.loadWorld(serializedObjects, rooms)
        createStaticEntities()
        serializedObjects.forEach{
            entityCreator.loadIdEntity(
                    roomId = it.roomId,
                    rect = it.rect,
                    objectPath = it.objectPath,
                    atlas = assets["Packs/items.pack", TextureAtlas::class.java],
                    r = it.rand,
                    playerEntity = playerEntity)

            roomId[engine.entities.last()].serialized = it
            if (AI.has(engine.entities.last()))
            AI[engine.entities.last()].appeared = it.appeared
        }
        currentMapId = worldIO.prefs.getInteger("currentMap")

    }

}