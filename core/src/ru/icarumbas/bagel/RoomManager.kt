package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.STATIC_BIT
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.components.other.DamageComponent
import ru.icarumbas.bagel.components.other.StateComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.creators.AnimationCreator
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator
import ru.icarumbas.bagel.systems.other.StateSwapSystem


class RoomManager(val rooms: ArrayList<Room>,
                  private val assets: AssetManager,
                  private val entityCreator: EntityCreator,
                  private val engine: Engine,
                  private val animCreator: AnimationCreator){

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
            loadIdMamObject(it.path, it.id, "chair1", assets["Packs/items.pack", TextureAtlas::class.java])
            loadIdMamObject(it.path, it.id, "chair2", assets["Packs/items.pack", TextureAtlas::class.java])
            loadIdMamObject(it.path, it.id, "table", assets["Packs/items.pack", TextureAtlas::class.java])
            loadIdMamObject(it.path, it.id, "chandelier", assets["Packs/items.pack", TextureAtlas::class.java])
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
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                }

                "chair1" -> {
                    entityCreator.createMapObjectIdEntity(
                            it,
                            atlas,
                            "Chair (1)",
                            70,
                            128,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                }

                "chair2" -> {
                    entityCreator.createMapObjectIdEntity(
                            it,
                            atlas,
                            "Chair (2)",
                            70,
                            128,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                }

                "table" -> {
                    entityCreator.createMapObjectIdEntity(
                            it,
                            atlas,
                            "Table",
                            137,
                            69,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                }

                "chandelier" -> {
                    entityCreator.createMapObjectIdEntity(
                            it,
                            atlas,
                            "Chandelier",
                            243,
                            120,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Chandelier", 4, .125f, Animation.PlayMode.LOOP, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING, StateSwapSystem.DEAD))))
                }

                else -> throw Exception("NO SUCH CLASS: $objectPath")
            })
        }
    }

    private fun loadStaticMapObject(roomPath: String,
                                    objectPath: String,
                                    atlas: TextureAtlas? = null){

        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.forEach {
            engine.addEntity(when(objectPath){
                "lighting" -> {
                    entityCreator.createMapObjectStaticEntity(
                            it, roomPath, atlas!!, "Lighting", 98, 154, BodyDef.BodyType.StaticBody, STATIC_BIT, -1)
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Lighting", 4, .125f, Animation.PlayMode.LOOP, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING))))
                }
                "torch" -> {
                    entityCreator.createMapObjectStaticEntity(
                            it, roomPath, atlas!!, "Torch", 178, 116, BodyDef.BodyType.StaticBody, STATIC_BIT, -1)
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Torch", 4, .125f, Animation.PlayMode.LOOP, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING))))
                }
                "ground" -> entityCreator.createGroundEntity(it, roomPath, GROUND_BIT)
                "platform" -> entityCreator.createGroundEntity(it, roomPath, PLATFORM_BIT)
                else -> throw Exception("NO SUCH CLASS $objectPath")
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