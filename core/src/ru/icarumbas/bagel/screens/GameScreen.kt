package ru.icarumbas.bagel.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.Bagel
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.bagel.*
import ru.icarumbas.bagel.creators.AnimationCreator
import ru.icarumbas.bagel.creators.B2DWorldCreator
import ru.icarumbas.bagel.creators.EntityCreator
import ru.icarumbas.bagel.creators.WorldCreator
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.AISystem
import ru.icarumbas.bagel.systems.other.HealthSystem
import ru.icarumbas.bagel.systems.other.RoomChangingSystem
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.systems.physics.AwakeSystem
import ru.icarumbas.bagel.systems.physics.ContactSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.systems.rendering.AnimationSystem
import ru.icarumbas.bagel.systems.rendering.RenderingSystem
import ru.icarumbas.bagel.systems.rendering.ViewportSystem
import ru.icarumbas.bagel.systems.velocity.JumpingSystem
import ru.icarumbas.bagel.systems.velocity.RunningSystem
import ru.icarumbas.bagel.utils.SerializedMapObject


class GameScreen(newWorld: Boolean, val game: Bagel): ScreenAdapter() {

    private val world = World(Vector2(0f, -9.8f), true)
    private val hud: Hud
    private val debugRenderer: DebugRenderer
    private val engine = Engine()
    private val mapRenderer: MapRenderer
    private val worldCleaner: WorldCleaner
    private val entityCreator: EntityCreator
    private val playerEntity: Entity
    private val rm: RoomManager
    private val orthoRenderer: OrthogonalTiledMapRenderer

    private val serializedObjects = ArrayList<SerializedMapObject>()
    private val rooms = ArrayList<Room>()

    init {

        val b2DWorldCreator = B2DWorldCreator(world)
        val animationCreator = AnimationCreator(game.assetManager)
        val worldCreator = WorldCreator(game.assetManager)
        entityCreator = EntityCreator(b2DWorldCreator, game.assetManager, engine, animationCreator)
        rm = RoomManager(rooms, game.assetManager, entityCreator, engine, serializedObjects, game.worldIO)

        if (newWorld) rm.createNewWorld(worldCreator, game.assetManager) else rm.continueWorld()

        val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))
        orthoRenderer = OrthogonalTiledMapRenderer(game.assetManager.get(rm.path()), 0.01f)

        mapRenderer = MapRenderer(orthoRenderer, rm, game.assetManager, viewport)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport)

        val playerBody = b2DWorldCreator.createPlayerBody()
        val coins = ArrayList<Body>()
        val entityDeleteList = ArrayList<Entity>()

        worldCleaner = WorldCleaner(entityDeleteList, engine, world, serializedObjects)

        playerEntity = entityCreator.createPlayerEntity(
                animationCreator,
                game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java],
                playerBody)


        hud = Hud(playerEntity)
        val contactSystem = ContactSystem(hud, playerEntity)
        world.setContactListener(contactSystem)

        with (engine) {

            // Other
            addSystem(RoomChangingSystem(rm))
            addSystem(StateSystem(rm))
            addSystem(HealthSystem(rm, world, coins, entityDeleteList))
            addSystem(AISystem(playerEntity, rm))

            // Velocity
            addSystem(RunningSystem(hud))
            addSystem(JumpingSystem(hud))

            // Physic
            addSystem(AwakeSystem(rm))
            addSystem(contactSystem)
            addSystem(WeaponSystem(hud, rm))

            // Rendering
            addSystem(AnimationSystem(rm))
            addSystem(ViewportSystem(viewport, rm))
            addSystem(RenderingSystem(rm, orthoRenderer.batch))

            addEntity(playerEntity)
        }


        Gdx.input.inputProcessor = hud.stage

        println("Size of rooms: ${rm.size()}")
        println("World bodies count: ${world.bodyCount}")
        println("Entities size: ${engine.entities.size()}")
    }

    override fun render(delta: Float) {
        world.step(1/45f, 6, 2)
        worldCleaner.update()
        mapRenderer.render()
        engine.update(delta)
        debugRenderer.render()
        hud.draw(rm)
    }

    override fun pause() {
        game.worldIO.saveWorld(serializedObjects, rooms)
    }

    override fun resize(width: Int, height: Int) {
        hud.stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }

}
