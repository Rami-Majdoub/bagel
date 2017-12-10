package ru.icarumbas.bagel.engine.world

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ru.icarumbas.bagel.engine.io.SerializedMapObject


class RoomWorld(val rooms: ArrayList<Room>,
                private val assets: AssetManager,
                private val engine: Engine,
                private val serializedObjects: ArrayList<SerializedMapObject>,
                private val worldIO: WorldIO,
                private val playerEntity: Entity): RoomWorldState {

    private var currentMapId = 0
    lateinit var mesh: Array<IntArray>


    override fun getCurrentMapId() = currentMapId

    override fun getMapPath(id: Int) = rooms[id].path

    override fun getRooms() = rooms

    override fun getRoomWidth(id: Int) = rooms[id].width

    override fun getRoomHeight(id: Int) = rooms[id].height

    override fun getRoomPass(pass: Int, id: Int) = rooms[id].passes[pass]

    override fun getRoomMeshCoordinate(cell: Int, id: Int) = rooms[id].meshCoords[cell]

    override fun getRoomFor(x: Int, y: Int): Room? {
        rooms.forEach {
            if ( (it.meshCoords[0] == x && it.meshCoords[1] == y)) {
                return it
            }
        }

        return null
    }

    fun createNewWorld(worldCreator: WorldCreator, assetManager: AssetManager) {

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