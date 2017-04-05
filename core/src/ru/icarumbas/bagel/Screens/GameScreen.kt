package ru.icarumbas.bagel.Screens

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Scenes.Hud
import ru.icarumbas.bagel.Tools.WorldCreate.AnimationCreator
import ru.icarumbas.bagel.Tools.WorldCreate.WorldCreator
import ru.icarumbas.bagel.Tools.B2dWorldCreator.WorldContactListener
import ru.icarumbas.bagel.Tools.B2dWorldCreator.B2DWorldCreator

class GameScreen (game: Bagel, newWorld: Boolean) : ScreenAdapter() {

    val GROUND_BIT: Short = 2
    val PLATFORM_BIT: Short = 4
    val PLAYER_BIT: Short = 8

    private val debugRenderer = Box2DDebugRenderer()
    private val logger = FPSLogger()
    private val camera = OrthographicCamera(7.68f, 5.12f)
    private val hud = Hud()
    private val viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
    private val animationCreator = AnimationCreator()
    private val worldCreator = B2DWorldCreator()
    private val world = World(Vector2(0f, -9.8f), true)
    private val player = Player(world, hud, this)
    private val mapGenerator = WorldCreator(worldCreator, world, animationCreator, this)
    private val worldContactListener = WorldContactListener(mapGenerator, hud, player, this)

    init {
        if (newWorld) mapGenerator.createNewWorld() else mapGenerator.continueWorld()
        animationCreator.createTileAnimation(0, mapGenerator.maps)

        world.setContactListener(worldContactListener)
        world.setContactFilter(worldContactListener)
    }

    override fun render(delta: Float) {
        world.step(1 / 60f, 8, 3)
        player.update(delta)
        logger.log()
        moveCamera()
        debugRenderer.render(world, camera.combined)
        mapGenerator.mapRenderer.setView(camera)
        mapGenerator.mapRenderer.render()
        mapGenerator.mapRenderer.batch.begin()
        mapGenerator.checkRoomChange(player)
        player.draw(mapGenerator.mapRenderer.batch)
        mapGenerator.mapRenderer.batch.end()
        animationCreator.updateAnimations()
        worldContactListener.update()
        hud.update(player)
        hud.stage.draw()
        hud.l.setText("${mapGenerator.currentMap}")
    }

    private fun moveCamera() {

        camera.position.x = player.playerBody!!.position.x
        camera.position.y = player.playerBody!!.position.y

        if (camera.position.y - viewport.worldHeight / 2f < 0)
            camera.position.y = viewport.worldHeight / 2f

        if (camera.position.x - viewport.worldWidth / 2f < 0)
            camera.position.x = viewport.worldWidth / 2f

        if (camera.position.x + viewport.worldWidth / 2f > mapGenerator.mapWidth)
            camera.position.x = mapGenerator.mapWidth - viewport.worldWidth / 2f

        if (camera.position.y + viewport.worldHeight / 2f > mapGenerator.mapHeight)
            camera.position.y = mapGenerator.mapHeight - viewport.worldHeight / 2f

        camera.update()
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }
}
