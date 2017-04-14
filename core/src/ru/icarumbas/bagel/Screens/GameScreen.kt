package ru.icarumbas.bagel.Screens

import com.badlogic.gdx.Gdx
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
import ru.icarumbas.bagel.Scenes.MiniMap
import ru.icarumbas.bagel.Tools.B2dWorldCreator.WorldContactListener
import ru.icarumbas.bagel.Tools.WorldCreate.AnimationCreator
import ru.icarumbas.bagel.Tools.WorldCreate.WorldCreator

class GameScreen(game: Bagel, newWorld: Boolean) : ScreenAdapter() {

    val GROUND_BIT: Short = 2
    val PLATFORM_BIT: Short = 4
    val PLAYER_BIT: Short = 8

    private val debugRenderer = Box2DDebugRenderer()
    private val camera = OrthographicCamera(7.68f, 5.12f)
    val hud = Hud()
    private val viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
    val animationCreator = AnimationCreator()
    val world = World(Vector2(0f, -9.8f), true)
    val player = Player(this)
    val worldCreator = WorldCreator(this)
    private val worldContactListener = WorldContactListener(this)
    val miniMap = MiniMap()

    init {
        if (newWorld) worldCreator.createNewWorld() else worldCreator.continueWorld()
        animationCreator.createTileAnimation(0, worldCreator.maps)

        Gdx.input.inputProcessor = hud.stage

        world.setContactListener(worldContactListener)
        world.setContactFilter(worldContactListener)
    }

    override fun render(delta: Float) {
        world.step(1 / 60f, 8, 3)
        player.update(delta)
        moveCamera()
        worldCreator.mapRenderer.setView(camera)
        worldCreator.mapRenderer.render()
        worldCreator.mapRenderer.batch.begin()
        worldCreator.checkRoomChange(player)
        player.draw(worldCreator.mapRenderer.batch)
        worldCreator.mapRenderer.batch.end()
        animationCreator.updateAnimations()
        worldContactListener.update()
        hud.update(player)
        hud.stage.draw()
        hud.l.setText("${worldCreator.currentMap}")
        miniMap.render()
        debugRenderer.render(world, camera.combined)
    }

    private fun moveCamera() {

        camera.position.x = player.playerBody.position.x
        camera.position.y = player.playerBody.position.y

        if (camera.position.y - viewport.worldHeight / 2f < 0)
            camera.position.y = viewport.worldHeight / 2f

        if (camera.position.x - viewport.worldWidth / 2f < 0)
            camera.position.x = viewport.worldWidth / 2f

        if (camera.position.x + viewport.worldWidth / 2f > worldCreator.mapWidth)
            camera.position.x = worldCreator.mapWidth - viewport.worldWidth / 2f

        if (camera.position.y + viewport.worldHeight / 2f > worldCreator.mapHeight)
            camera.position.y = worldCreator.mapHeight - viewport.worldHeight / 2f

        camera.update()
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }
}
