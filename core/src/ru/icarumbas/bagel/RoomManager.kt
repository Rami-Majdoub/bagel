package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
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
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.SerializedMapObject


class RoomManager(val rooms: ArrayList<Room>,
                  private val assets: AssetManager,
                  private val entityCreator: EntityCreator,
                  private val engine: Engine,
                  private val animCreator: AnimationCreator,
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
            loadStaticMapObject("Maps/New/map$it.tmx", "lighting")
            loadStaticMapObject("Maps/New/map$it.tmx", "torch")
            loadStaticMapObject("Maps/New/map$it.tmx", "ground")
            loadStaticMapObject("Maps/New/map$it.tmx", "platform")
        }
    }

    private fun createIdEntities(){

        rooms.forEach {
            createIdMapObject(it.path, it.id, "vase", 4)
            createIdMapObject(it.path, it.id, "chair1", 2)
            createIdMapObject(it.path, it.id, "chair2", 2)
            createIdMapObject(it.path, it.id, "table", 2)
            createIdMapObject(it.path, it.id, "chandelier")
            createIdMapObject(it.path, it.id, "window", 2)
        }

    }

    private fun createIdMapObject(roomPath: String,
                        roomId: Int,
                        objectPath: String,
                        r: Int = 1,
                        atlas: TextureAtlas = assets["Packs/items.pack", TextureAtlas::class.java]){

        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]
        layer?.objects?.filterIsInstance<RectangleMapObject>()?.forEach {
            val r = MathUtils.random(1, r)
            if (loadIdMapObject(roomId, it.rectangle, objectPath, atlas, r)){
                serializedObjects.add(SerializedMapObject(roomId, it.rectangle, objectPath, r))
                Mappers.roomId[engine.entities.last()].serialized = serializedObjects.last()
            }

        }

    }

    private fun loadIdMapObject(roomId: Int,
                                rect: Rectangle,
                                objectPath: String,
                                atlas: TextureAtlas,
                                r: Int): Boolean {

            engine.addEntity(when(objectPath){
                "vase" -> {
                    val size = when (r) {
                        1 -> Pair(132, 171)
                        2 -> Pair(65, 106)
                        3 -> Pair(120, 162)
                        else -> Pair(98, 72)
                    }

                    entityCreator.createMapObjectIdEntity(
                            rect,
                            atlas,
                            "Vase ($r)",
                            size.first,
                            size.second,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                }

                "window" -> {
                    entityCreator.createMapObjectIdEntity(
                            rect,
                            atlas,
                            "Window Small ($r)",
                            86,
                            169,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                }

                "chair1" -> {
                    if (r == 2) return false
                    entityCreator.createMapObjectIdEntity(
                            rect,
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
                    if (r == 2) return false
                    entityCreator.createMapObjectIdEntity(
                            rect,
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
                    if (r == 2) return false
                    entityCreator.createMapObjectIdEntity(
                            rect,
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
                            rect,
                            atlas,
                            "Chandelier",
                            243,
                            120,
                            roomId,
                            BodyDef.BodyType.StaticBody)
                            .add(DamageComponent(5))
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Chandelier", 4, .125f, Animation.PlayMode.LOOP, atlas))))
                            .add(StateComponent(
                                    ImmutableArray(Array.with(StateSwapSystem.STANDING, StateSwapSystem.DEAD)),
                                    MathUtils.random()
                                    ))
                }

                else -> throw Exception("NO SUCH CLASS: $objectPath")
            })
        return true
    }

    private fun loadStaticMapObject(roomPath: String,
                                    objectPath: String,
                                    atlas: TextureAtlas = assets["Packs/items.pack", TextureAtlas::class.java]){

        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]

        layer?.objects?.forEach {
            engine.addEntity(when(objectPath){
                "lighting" -> {
                    entityCreator.createMapObjectStaticEntity(
                            (it as RectangleMapObject).rectangle, roomPath, atlas, "Lighting", 98, 154, BodyDef.BodyType.StaticBody, STATIC_BIT, -1)
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Lighting", 4, .125f, Animation.PlayMode.LOOP, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING)),
                                    MathUtils.random()))
                }
                "torch" -> {
                    entityCreator.createMapObjectStaticEntity(
                            (it as RectangleMapObject).rectangle, roomPath, atlas, "Torch", 178, 116, BodyDef.BodyType.StaticBody, STATIC_BIT, -1)
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Torch", 4, .125f, Animation.PlayMode.LOOP, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING)),
                                    MathUtils.random()))
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
        createStaticEntities()
        createIdEntities()
    }

    fun continueWorld() {
        worldIO.loadWorld(serializedObjects, rooms)
        serializedObjects.forEach{
            loadIdMapObject(it.roomId, it.rect, it.objectPath, assets["Packs/items.pack", TextureAtlas::class.java], it.rand)
        }
        createStaticEntities()
    }

}