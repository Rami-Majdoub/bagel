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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import ru.icarumbas.*
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.creators.AnimationCreator
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator
import ru.icarumbas.bagel.systems.other.StateSwapSystem
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.SerializedMapObject
import kotlin.experimental.or


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

    private fun createIdEntity(roomPath: String,
                               roomId: Int,
                               objectPath: String,
                               r: Int = 1,
                               atlas: TextureAtlas = assets["Packs/items.pack", TextureAtlas::class.java]){


        val layer = assets.get(roomPath, TiledMap::class.java).layers[objectPath]
        layer?.objects?.filterIsInstance<RectangleMapObject>()?.forEach {
            val r = MathUtils.random(1, r)
            if (loadIdEntity(roomId, it.rectangle, objectPath, atlas, r)){
                serializedObjects.add(SerializedMapObject(roomId, it.rectangle, objectPath, r))
                Mappers.roomId[engine.entities.last()].serialized = serializedObjects.last()
            }

        }

    }

    private fun loadIdEntity(roomId: Int,
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

                    entityCreator.createMapObjectEntity(
                            rect,
                            size.first,
                            size.second,
                            BodyDef.BodyType.StaticBody,
                            BREAKABLE_BIT,
                            WEAPON_BIT,
                            atlas.findRegion("Vase ($r)"))
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                            .add(RoomIdComponent(roomId))
                }

                "window" -> {
                    entityCreator.createMapObjectEntity(
                            rect,
                            86,
                            169,
                            BodyDef.BodyType.StaticBody,
                            BREAKABLE_BIT,
                            WEAPON_BIT,
                            atlas.findRegion("Window Small ($r)"))
                            .add(RoomIdComponent(roomId))

                }

                "chair1" -> {
                    if (r == 2) return false
                    entityCreator.createMapObjectEntity(
                            rect,
                            70,
                            128,
                            BodyDef.BodyType.StaticBody,
                            BREAKABLE_BIT,
                            WEAPON_BIT,
                            atlas.findRegion("Chair (1)"))
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                            .add(RoomIdComponent(roomId))

                }

                "chair2" -> {
                    if (r == 2) return false
                    entityCreator.createMapObjectEntity(
                            rect,
                            70,
                            128,
                            BodyDef.BodyType.StaticBody,
                            BREAKABLE_BIT,
                            WEAPON_BIT,
                            atlas.findRegion("Chair (2)"))
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                            .add(RoomIdComponent(roomId))

                }

                "table" -> {
                    if (r == 2) return false
                    entityCreator.createMapObjectEntity(
                            rect,
                            137,
                            69,
                            BodyDef.BodyType.StaticBody,
                            BREAKABLE_BIT,
                            WEAPON_BIT,
                            atlas.findRegion("Table"))
                            .add(DamageComponent(5))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.DEAD))))
                            .add(RoomIdComponent(roomId))

                }

                "chandelier" -> {
                    entityCreator.createMapObjectEntity(
                            rect,
                            243,
                            120,
                            BodyDef.BodyType.StaticBody,
                            BREAKABLE_BIT,
                            WEAPON_BIT)
                            .add(DamageComponent(5))
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Chandelier", 4, .125f, atlas))))
                            .add(StateComponent(
                                    ImmutableArray(Array.with(StateSwapSystem.STANDING, StateSwapSystem.DEAD)),
                                    MathUtils.random()
                                    ))
                            .add(RoomIdComponent(roomId))

                }

                "groundEnemy" -> {
                    val skeletonAtlas = assets["Packs/Skeleton.pack", TextureAtlas::class.java]
                    when (r) {
                        1, 2, 3, 4, 5 -> {
                            entityCreator.createMapObjectEntity(
                                    rect,
                                    128,
                                    228,
                                    BodyDef.BodyType.DynamicBody,
                                    AI_BIT,
                                    WEAPON_BIT or GROUND_BIT or PLATFORM_BIT)
                                    .add(DamageComponent(2 * roomId + 1))
                                    .add(AnimationComponent(hashMapOf(
                                            StateSwapSystem.STANDING to Animation(
                                                    .125f,
                                                    skeletonAtlas.findRegions("idle"),
                                                    Animation.PlayMode.LOOP),
                                            StateSwapSystem.ATTACKING to Animation(
                                                    .125f,
                                                    skeletonAtlas.findRegions("hit"),
                                                    Animation.PlayMode.LOOP),
                                            StateSwapSystem.DEAD to Animation(
                                                    .125f,
                                                    skeletonAtlas.findRegions("die"),
                                                    Animation.PlayMode.NORMAL),
                                            StateSwapSystem.APPEARING to Animation(
                                                    .125f,
                                                    skeletonAtlas.findRegions("appear"),
                                                    Animation.PlayMode.LOOP),
                                            StateSwapSystem.RUNNING to Animation(
                                                    .125f,
                                                    skeletonAtlas.findRegions("go"),
                                                    Animation.PlayMode.LOOP))
                                    ))
                                    .add(StateComponent(
                                            ImmutableArray(Array.with(
                                                    StateSwapSystem.STANDING,
                                                    StateSwapSystem.ATTACKING,
                                                    StateSwapSystem.DEAD,
                                                    StateSwapSystem.APPEARING,
                                                    StateSwapSystem.RUNNING))
                                    ))
                                    .add(AttackComponent(
                                            strength = 2 * roomId + 1,
                                            attackSpeed = 1f,
                                            nearAttackStrength = 2 * roomId + 1,
                                            knockback = Vector2(.1f, .1f)))
                                    .add(RoomIdComponent(roomId))
                                    .add(RunComponent(.4f, 4f))
                                    .add(AIComponent())

                        }
                        else -> return false
                    }
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
                    entityCreator.createMapObjectEntity(
                            (it as RectangleMapObject).rectangle,
                            98,
                            154,
                            BodyDef.BodyType.StaticBody,
                            STATIC_BIT,
                            -1,
                            atlas.findRegion("Lighting"))
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Lighting", 4, .125f, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING)),
                                    MathUtils.random()))
                            .add(StaticComponent(roomPath))
                }
                "torch" -> {
                    entityCreator.createMapObjectEntity(
                            (it as RectangleMapObject).rectangle,
                            178,
                            116,
                            BodyDef.BodyType.StaticBody,
                            STATIC_BIT,
                            -1,
                            atlas.findRegion("Torch"))
                            .add(AnimationComponent(hashMapOf(StateSwapSystem.STANDING to
                                    animCreator.create("Torch", 4, .125f, atlas))))
                            .add(StateComponent(ImmutableArray(Array.with(StateSwapSystem.STANDING)),
                                    MathUtils.random()))
                            .add(StaticComponent(roomPath))

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

        rooms.forEach {
            createIdEntity(it.path, it.id, "vase", 4)
            createIdEntity(it.path, it.id, "chair1", 3)
            createIdEntity(it.path, it.id, "chair2", 3)
            createIdEntity(it.path, it.id, "table", 3)
            createIdEntity(it.path, it.id, "chandelier")
            createIdEntity(it.path, it.id, "window", 2)
            createIdEntity(it.path, it.id, "groundEnemy", 5)
        }
    }

    fun continueWorld() {
        worldIO.loadWorld(serializedObjects, rooms)
        createStaticEntities()
        serializedObjects.forEach{
            loadIdEntity(it.roomId, it.rect, it.objectPath, assets["Packs/items.pack", TextureAtlas::class.java], it.rand)
        }
    }

}