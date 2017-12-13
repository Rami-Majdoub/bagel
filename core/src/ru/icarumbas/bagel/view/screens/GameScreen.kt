package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.box2d.createWorld
import ru.icarumbas.Bagel
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.bagel.engine.controller.PlayerController
import ru.icarumbas.bagel.engine.entities.BodyContactListener
import ru.icarumbas.bagel.engine.entities.EntitiesWorld
import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.engine.world.REG_ROOM_WIDTH
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.view.renderer.DebugRenderer
import ru.icarumbas.bagel.view.renderer.MapRenderer
import ru.icarumbas.bagel.view.ui.Hud


class GameScreen(

        val assets: ResourceManager,
        val game: Bagel,
        isNewGame: Boolean

) : ScreenAdapter() {

    // Box2d world
    private val world = createWorld(Vector2(0f, -9.8f))

    private val worldIO = WorldIO()

    private val debugRenderer: DebugRenderer
    private val mapRenderer: MapRenderer

    private val hud: Hud

    // Room world
    private val roomWorld: RoomWorld

    // Entity world
    private val entityWorld: EntitiesWorld
    // UI
    private val playerController : PlayerController


    init {


        val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))

        mapRenderer = MapRenderer(viewport.camera)
        debugRenderer = DebugRenderer(Box2DDebugRenderer(), world, viewport.camera)

        roomWorld = RoomWorld(assets, mapRenderer)

        entityWorld = EntitiesWorld(
                roomWorld,
                world,
                assets
        )

        hud = Hud(assets, roomWorld, entityWorld.playerEntity)


        val contactListener = BodyContactListener(hud.touchpad, playerEntity, engine)
        world.setContactListener(contactListener)


        Gdx.input.inputProcessor = hud.stage

    }

    fun continueWorld(){
        entityWorld.loadEntities(worldIO)
        roomWorld.loadWorld(worldIO)
        hud.minimap.load(worldIO)
    }

    fun createNewWorld(){

    }

    override fun render(delta: Float) {
        world.step(1/45f, 6, 2)
        mapRenderer.render()
        debugRenderer.render()
        hud.draw()

    }

    override fun pause() {
    }

    override fun resize(width: Int, height: Int) {
        hud.stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }

}