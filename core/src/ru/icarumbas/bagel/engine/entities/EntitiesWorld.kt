package ru.icarumbas.bagel.engine.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.Viewport
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.engine.controller.PlayerMoveController
import ru.icarumbas.bagel.engine.controller.UIController
import ru.icarumbas.bagel.engine.entities.factories.AnimationFactory
import ru.icarumbas.bagel.engine.entities.factories.BodyFactory
import ru.icarumbas.bagel.engine.entities.factories.EntityFactory
import ru.icarumbas.bagel.engine.io.EntitiesInfo
import ru.icarumbas.bagel.engine.io.SerializedMapObject
import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.engine.systems.other.*
import ru.icarumbas.bagel.engine.systems.physics.AwakeSystem
import ru.icarumbas.bagel.engine.systems.physics.WeaponSystem
import ru.icarumbas.bagel.engine.systems.velocity.FlyingSystem
import ru.icarumbas.bagel.engine.systems.velocity.JumpingSystem
import ru.icarumbas.bagel.engine.systems.velocity.RunningSystem
import ru.icarumbas.bagel.engine.systems.velocity.TeleportSystem
import ru.icarumbas.bagel.engine.world.MAPS_TOTAL
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.roomId
import ru.icarumbas.bagel.view.renderer.systems.AnimationSystem
import ru.icarumbas.bagel.view.renderer.systems.RenderingSystem
import ru.icarumbas.bagel.view.renderer.systems.TranslateSystem
import ru.icarumbas.bagel.view.renderer.systems.ViewportSystem


class EntitiesWorld (

        private val roomWorld: RoomWorld,
        private val worldIO: WorldIO,
        game: Bagel,
        world: World,
        assets: ResourceManager
) {

    private val entityFactory = EntityFactory(BodyFactory(world), AnimationFactory(), assets)
    private val entityFromLayerLoader = EntityFromLayerLoader(entityFactory, assets, this)

    private val ioEntities = ArrayList<SerializedMapObject>()

    val playerEntity = entityFactory.playerEntity()

    val engine = Engine().also {
        it.addEntityListener(
                BodyRemoval(
                        game,
                        world,
                        it,
                        worldIO,
                        roomWorld,
                        ioEntities,
                        playerEntity
                )
        )
    }

    fun update(dt: Float){
        engine.update(dt)
    }

    fun defineEngine(
            playerController: PlayerMoveController,
            UIController: UIController,
            viewport: Viewport,
            batch: Batch
    ){

        with (engine) {

            addSystem(RoomChangingSystem(roomWorld))
            addSystem(HealthSystem(roomWorld))
            addSystem(StateSystem(roomWorld))
            addSystem(AISystem(roomWorld))
            addSystem(OpeningSystem(UIController, roomWorld, entityFactory, playerEntity))
//            addSystem(LootSystem(hud, rm, playerEntity, entityDeleteList))

            /* Velocity */
            addSystem(RunningSystem(playerController, roomWorld))
            addSystem(JumpingSystem(playerController, roomWorld))
            addSystem(TeleportSystem(playerEntity, roomWorld))
            addSystem(FlyingSystem(playerEntity, roomWorld))

            /* Physic */
            addSystem(AwakeSystem(roomWorld))
            addSystem(WeaponSystem(UIController, roomWorld))

            /* Rendering */
            addSystem(ViewportSystem(viewport, roomWorld))
            addSystem(AnimationSystem(roomWorld))
            addSystem(TranslateSystem(roomWorld))
//            addSystem(ShaderSystem(rm))
            addSystem(RenderingSystem(roomWorld, batch))

            addEntity(playerEntity)
        }
    }

    fun createIdMapEntities(){
        roomWorld.rooms.forEach { room ->

            fun create(path: String, rand: Int = 1){
                entityFromLayerLoader.createIdEntitiesFromLayer(path, "vase", room.id, rand)
            }

            create("vase", 5)
            create("chair1", 3)
            create( "chair2", 3)
            create( "table", 3)
            create( "chandelier")
            create( "window", 2)
            create( "crateBarrel", 3)
            create( "smallBanner", 2)
            create( "chest", 2)
            create( "candle")
            create( "door", 2)
            create( "groundEnemy", 5)
            create( "flyingEnemy")

        }
    }

    fun createStaticMapEntities(){

        (0 until MAPS_TOTAL).forEach {

            fun create(objPath: String){
                entityFromLayerLoader.loadStaticEntitiesFromLayer("Maps/Map$it.tmx", objPath)
            }

            create("torch")
            create("ground")
            create("platform")
            create("spikes")
            create("lighting")
        }
    }

    fun loadIdEntities(){
        worldIO.loadEntitiesInfo().mapObjects.forEach {
            engine.addEntity(entityFactory.idMapObjectEntity(
                    it.roomId,
                    it.rect,
                    it.objectPath,
                    it.rand,
                    playerEntity
            ))
        }
    }

    fun saveEntites(){
        worldIO.saveInfo(EntitiesInfo(ioEntities))
    }

    fun saveEntityForSerialization(obj: SerializedMapObject){

        ioEntities.add(obj)
        roomId[engine.entities.last()].serialized = ioEntities.last()

    }
}