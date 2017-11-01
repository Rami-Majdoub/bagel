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
import ru.icarumbas.bagel.creators.*
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.screens.scenes.UInputListener
import ru.icarumbas.bagel.systems.other.*
import ru.icarumbas.bagel.systems.physics.AwakeSystem
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.systems.rendering.AnimationSystem
import ru.icarumbas.bagel.systems.rendering.RenderingSystem
import ru.icarumbas.bagel.systems.rendering.TranslateSystem
import ru.icarumbas.bagel.systems.rendering.ViewportSystem
import ru.icarumbas.bagel.systems.velocity.FlyingSystem
import ru.icarumbas.bagel.systems.velocity.JumpingSystem
import ru.icarumbas.bagel.systems.velocity.RunningSystem
import ru.icarumbas.bagel.systems.velocity.TeleportSystem
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.SerializedMapObject


class GameScreen(newWorld: Boolean, val game: Bagel): ScreenAdapter() {

    private val world = World(Vector2(0f, -9.8f), true)
    private val hud: Hud
    private val debugRenderer: DebugRenderer
    private val engine = Engine()
    private val mapRenderer: MapRenderer
    private val worldCleaner: WorldCleaner
    private val entityCreator: EntityCreator
    private val lootCreator: LootCreator
    private val playerEntity: Entity
    private val rm: RoomManager
    private val orthoRenderer: OrthogonalTiledMapRenderer
    private val uiInputListener: UInputListener

    private val serializedObjects = ArrayList<SerializedMapObject>()
    private val rooms = ArrayList<Room>()


    init {

        val b2DWorldCreator = B2DWorldCreator(world)
        val animationCreator = AnimationCreator()
        val worldCreator = WorldCreator(game.assetManager)
        entityCreator = EntityCreator(b2DWorldCreator, game.assetManager, engine, animationCreator, b2DWorldCreator.createPlayerBody())

        playerEntity = entityCreator.createPlayerEntity(
                animationCreator,
                game.assetManager["Packs/GuyKnight.pack", TextureAtlas::class.java])
        rm = RoomManager(rooms, game.assetManager, entityCreator, engine, serializedObjects, game.worldIO, playerEntity)

        hud = Hud(playerEntity, rm, game.assetManager)
        uiInputListener = UInputListener(hud.stage, hud)

        if (newWorld) {
            rm.createNewWorld(worldCreator, game.assetManager)
            hud.minimap.createRooms(rm.mesh)

        } else {
            rm.continueWorld()
            hud.minimap.loadRooms(game.worldIO.loadVisibleRooms(), rm.mesh)
            body[playerEntity].body.setTransform(
                    game.worldIO.prefs.getFloat("playerX"),
                    game.worldIO.prefs.getFloat("playerY"),
                    0f)
        }

        val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))
        orthoRenderer = OrthogonalTiledMapRenderer(game.assetManager.get(rm.path()), 0.01f)

        mapRenderer = MapRenderer(orthoRenderer, rm, game.assetManager, viewport)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport)

        val coins = ArrayList<Body>()
        val entityDeleteList = ArrayList<Entity>()

        worldCleaner = WorldCleaner(entityDeleteList, engine, world, serializedObjects, playerEntity, game)
        lootCreator = LootCreator(
                entityCreator,
                b2DWorldCreator,
                game.assetManager["Packs/weapons.pack", TextureAtlas::class.java],
                playerEntity)

        val contactListener = BodyContactListener(hud, playerEntity, engine)
        world.setContactListener(contactListener)


        with (engine) {

            // Other
            addSystem(RoomChangingSystem(rm))
            addSystem(HealthSystem(rm, world, coins, entityDeleteList))
            addSystem(StateSystem(rm))
            addSystem(AISystem(rm))
            addSystem(OpeningSystem(uiInputListener, rm, entityDeleteList, lootCreator))
//            addSystem(LootSystem(hud, rm, playerEntity, entityDeleteList))

            // Velocity
            addSystem(RunningSystem(hud, rm))
            addSystem(JumpingSystem(hud, rm))
            addSystem(TeleportSystem(playerEntity, rm))
            addSystem(FlyingSystem(playerEntity, rm))

            // Physic
            addSystem(AwakeSystem(rm))
            addSystem(WeaponSystem(uiInputListener, rm))

            // Rendering
            addSystem(ViewportSystem(viewport, rm))
            addSystem(AnimationSystem(rm))
            addSystem(TranslateSystem(rm))
//            addSystem(ShaderSystem(rm))
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

        val visibleRooms = ArrayList<Int>()
        hud.minimap.minimapFrame.children.filter { it.isVisible }.forEach {
            visibleRooms.add(hud.minimap.minimapFrame.children.indexOf(it))
        }
        game.worldIO.saveWorld(serializedObjects, rooms, rm.mesh, visibleRooms)
        game.worldIO.saveCurrentState(playerEntity, rm.currentMapId)
    }

    override fun resize(width: Int, height: Int) {
        hud.stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }

}
