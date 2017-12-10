package ru.icarumbas.bagel.engine.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.bagel.engine.controller.PlayerController
import ru.icarumbas.bagel.engine.controller.UIController
import ru.icarumbas.bagel.engine.io.SerializedMapObject
import ru.icarumbas.bagel.engine.systems.other.*
import ru.icarumbas.bagel.engine.systems.physics.AwakeSystem
import ru.icarumbas.bagel.engine.systems.physics.WeaponSystem
import ru.icarumbas.bagel.engine.systems.velocity.FlyingSystem
import ru.icarumbas.bagel.engine.systems.velocity.JumpingSystem
import ru.icarumbas.bagel.engine.systems.velocity.RunningSystem
import ru.icarumbas.bagel.engine.systems.velocity.TeleportSystem
import ru.icarumbas.bagel.engine.world.RoomWorldState
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.factories.EntityCreator
import ru.icarumbas.bagel.view.renderer.systems.AnimationSystem
import ru.icarumbas.bagel.view.renderer.systems.RenderingSystem
import ru.icarumbas.bagel.view.renderer.systems.TranslateSystem
import ru.icarumbas.bagel.view.renderer.systems.ViewportSystem


class ECSManager(private val world: World,
                 private val assets: AssetManager,
                 playerController: PlayerController,
                 uiController: UIController,
                 roomsState: RoomWorldState) {

    val engine = Engine()
    val serializedObjects = ArrayList<SerializedMapObject>()
    val playerEntity: Entity


    init {
        playerEntity = EntityCreator.playerEntity(world, assets)


        with (engine) {

            /* Other */
            addSystem(RoomChangingSystem(roomsState))
            addSystem(HealthSystem(roomsState, world))
            addSystem(StateSystem(roomsState))
            addSystem(AISystem(roomsState))
            addSystem(OpeningSystem(uiController, roomsState))
//            addSystem(LootSystem(hud, rm, playerEntity, entityDeleteList))

            /* Velocity */
            addSystem(RunningSystem(playerController, roomsState))
            addSystem(JumpingSystem(playerController, roomsState))
            addSystem(TeleportSystem(playerEntity, roomsState))
            addSystem(FlyingSystem(playerEntity, roomsState))

            /* Physic */
            addSystem(AwakeSystem(roomsState))
            addSystem(WeaponSystem(uiController, roomsState))

            /* Rendering */
            addSystem(ViewportSystem(viewport, roomsState))
            addSystem(AnimationSystem(roomsState))
            addSystem(TranslateSystem(roomsState))
//            addSystem(ShaderSystem(rm))
            addSystem(RenderingSystem(roomsState, orthoRenderer.batch))

            /* Entities */
            addEntity(playerEntity)
        }
    }

    fun createStaticMapEntities(path: String, objPath: String, world: World){
        (0 until TILED_MAPS_TOTAL).forEach {
            EntityCreator.("Maps/Map$it.tmx", "lighting")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "torch")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "ground")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "platform")
            entityCreator.loadStaticMapObject("Maps/Map$it.tmx", "spikes")
        }
    }



    fun createIdEntities(roomPath: String,
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
}