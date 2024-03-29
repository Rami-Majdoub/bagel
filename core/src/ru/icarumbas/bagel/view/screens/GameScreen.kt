
package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.box2d.createWorld
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.engine.controller.HudInputListener
import ru.icarumbas.bagel.engine.controller.PlayerMoveController
import ru.icarumbas.bagel.engine.controller.WASDPlayerController
import ru.icarumbas.bagel.engine.entities.BodyContactListener
import ru.icarumbas.bagel.engine.entities.EntitiesWorld
import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.engine.world.REG_ROOM_HEIGHT
import ru.icarumbas.bagel.engine.world.REG_ROOM_WIDTH
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.view.renderer.DebugRenderer
import ru.icarumbas.bagel.view.renderer.MapRenderer
import ru.icarumbas.bagel.view.ui.Hud


class GameScreen(

        val assets: ResourceManager,
        val worldIO: WorldIO,
        game: Bagel,
        isNewGame: Boolean

) : ScreenAdapter() {

    // Box2d world
    private val world = createWorld(Vector2(0f, -10f))

    private val debugRenderer: DebugRenderer
    private val mapRenderer: MapRenderer

    private val hud: Hud

    // Room world
    private val roomWorld: RoomWorld

    // Entity world
    private val entityWorld: EntitiesWorld

    private val playerController: PlayerMoveController
    private val uiController: HudInputListener

    init {


        val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))

        mapRenderer = MapRenderer(viewport.camera)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport.camera)

        roomWorld = RoomWorld(assets, mapRenderer)

        entityWorld = EntitiesWorld(
                roomWorld,
                worldIO,
                game,
                world,
                assets
        )

        hud = Hud(assets, roomWorld, entityWorld.playerEntity)

        if (isNewGame) {
            createNewWorld()
        } else {
            continueWorld()
        }

        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            playerController = WASDPlayerController()
            uiController = HudInputListener(null, null, hud.minimap)
        } else {
            playerController = hud.createOnScreenPlayerMoveController()
            uiController = hud.createUIController()
        }

        hud.stage.addListener(uiController)
        Gdx.input.inputProcessor = hud.stage

        entityWorld.defineEngine(
                playerController,
                uiController,
                viewport,
                mapRenderer.renderer.batch
        )

        BodyContactListener(
                playerController,
                entityWorld.engine,
                entityWorld.playerEntity
        ).let {
            world.setContactListener(it)
        }


        println("Rooms size is ${roomWorld.rooms.size}")
        println("Entities size is ${entityWorld.engine.entities.size()}")
    }

    private fun continueWorld(){

        val playerInfo = worldIO.loadPlayerInfo()

        with (roomWorld) {
            loadWorld(worldIO)
            currentMapId = playerInfo.currentMap
        }

        with (entityWorld) {
            loadEntities()
            createStaticMapEntities()
        }

        hud.minimap.load(worldIO, assets)
    }

    private fun createNewWorld(){
        with (roomWorld) {
            createNewWorld()
            currentMapId = 0
        }

        with (entityWorld) {
            createIdMapEntities()
            createStaticMapEntities()
        }

        hud.minimap.createRooms(roomWorld.mesh, assets)
    }

    private fun update(delta: Float){
        world.step(1/45f, 6, 2)
        hud.update(delta)
        entityWorld.update(delta)
    }

    override fun render(delta: Float) {
        mapRenderer.render()
        update(delta)
        debugRenderer.render()
        hud.draw(delta)
    }

    override fun pause() {
        entityWorld.saveEntities()
        roomWorld.saveWorld(worldIO)
        hud.minimap.save(worldIO)
    }

    override fun resize(width: Int, height: Int) {
        hud.stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
        hud.dispose()
    }

}