package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ru.icarumbas.bagel.engine.world.REG_ROOM_WIDTH
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.view.renderer.DebugRenderer
import ru.icarumbas.bagel.view.renderer.MapRenderer
import ru.icarumbas.bagel.view.ui.Hud


class GameScreen : KtxScreen {


    private val debugRenderer: DebugRenderer
    private val mapRenderer: MapRenderer


    private val hud: Hud

    private val rm: RoomWorld









    // Box2d world
    private val world = World(Vector2(0f, -9.8f), true)
    private val worldCleaner: WorldCleaner

    // ECS
    private val engine = Engine()
    private val entityCreator: EntityCreator
    private val serializedObjects = ArrayList<SerializedMapObject>()


    // UI
    private val uiInputListener: UInputListener

    // World
    private val rooms = ArrayList<Room>()
    private val playerEntity: Entity


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

        // if newWorld
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

        mapRenderer = MapRenderer(orthoRenderer, rm, game.assetManager, viewport.camera)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport.camera)

        val coins = ArrayList<Body>()
        val entityDeleteList = ArrayList<Entity>()

        worldCleaner = WorldCleaner(entityDeleteList, engine, world, serializedObjects, playerEntity, game)

        val contactListener = BodyContactListener(hud.touchpad, playerEntity, engine)
        world.setContactListener(contactListener)


        Gdx.input.inputProcessor = hud.stage

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