package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.STATIC_BIT
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator


class RoomManager(val rooms: ArrayList<Room>,
                  private val assets: AssetManager,
                  private val entityCreator: EntityCreator,
                  private val engine: Engine){

    var currentMapId = 0

    fun path(id: Int = currentMapId) = rooms[id].path

    fun size() = rooms.size

    fun width(id: Int = currentMapId) = rooms[id].width

    fun height(id: Int = currentMapId) = rooms[id].height

    fun pass(side: Int, id: Int = currentMapId) = rooms[id].passes[side]

    fun mesh(cell: Int, id: Int = currentMapId) = rooms[id].meshCoords[cell]

    fun loadEntities(){

        (0 until TILED_MAPS_TOTAL).forEach {
            loadStaticMapObject("Maps/New/map$it.tmx", "lighting", assets["Packs/items.pack", TextureAtlas::class.java])
            loadStaticMapObject("Maps/New/map$it.tmx", "torch", assets["Packs/items.pack", TextureAtlas::class.java])
            loadStaticMapObject("Maps/New/map$it.tmx", "ground")
            loadStaticMapObject("Maps/New/map$it.tmx", "platform")
        }

        rooms.forEach {
            loadIdMamObject(it.path, it.id, "vase", assets["Packs/items.pack", TextureAtlas::class.java])
        }

    }

    private fun loadIdMamObject(
            roomPath: String,
            roomId: Int,
            objectPath: String,
            atlas: TextureAtlas) {
        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.filterIsInstance<RectangleMapObject>()?.forEach {
            engine.addEntity(when(objectPath){
                "vase" -> {
                    val r = MathUtils.random(1, 4)
                    val size = when (r) {
                        1 -> Pair(132, 171)
                        2 -> Pair(65, 106)
                        3 -> Pair(120, 162)
                        else -> Pair(98, 72)
                    }

                    entityCreator.createMapObjectIdEntity(
                            it,
                            atlas,
                            "Vase ($r)",
                            size.first,
                            size.second,
                            roomId, BodyDef.BodyType.StaticBody)
                }
                else -> throw Exception("NO SUCH CLASS")
            })
        }
    }

    private fun loadStaticMapObject(roomPath: String,
                                    objectPath: String,
                                    atlas: TextureAtlas? = null){

        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.forEach {
            engine.addEntity(when(objectPath){
                "spikeTraps" -> Entity()
                "spikes" -> Entity()
                "portalDoor" -> Entity()
                "lighting" -> entityCreator.createMapObjectStaticAnimationEntity(
                        it, roomPath, atlas!!, "Lighting", 98, 154, .125f, 4, BodyDef.BodyType.StaticBody, STATIC_BIT, -1)
                "torch" -> entityCreator.createMapObjectStaticAnimationEntity(
                        it, roomPath, atlas!!, "Torch", 178, 116, .125f, 4, BodyDef.BodyType.StaticBody, STATIC_BIT, -1)
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
        worldCreator.createWorld(100, this)
    }

    fun continueWorld() {
        /*rooms = game.worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = game.worldIO.preferences.getInteger("CurrentMap")
        player.money = game.worldIO.preferences.getInteger("Money")
        game.worldIO.loadLastPlayerState(player)
        player.HP = game.worldIO.preferences.getInteger("HP")*/

    }

}