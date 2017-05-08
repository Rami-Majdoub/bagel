package ru.icarumbas.bagel.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.Bagel
import ru.icarumbas.DEFAULT
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Room
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Screens.Scenes.MiniMap
import ru.icarumbas.bagel.Utils.B2dWorldCreator.WorldContactListener
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import ru.icarumbas.bagel.Utils.WorldCreate.WorldCreator
import ru.icarumbas.bagel.WorldIO
import kotlin.concurrent.thread

class GameScreen(newWorld: Boolean): ScreenAdapter() {

    private val debugRenderer = Box2DDebugRenderer()
    private val camera = OrthographicCamera(7.68f, 5.12f)
    private val viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
    val animationCreator = AnimationCreator()
    val world = World(Vector2(0f, -9.8f), true)
    val player = Player(this)
    val hud = Hud(player)
    val worldCreator = WorldCreator()
    private val worldContactListener = WorldContactListener(this)
    val miniMap = MiniMap()
    var mapRenderer = OrthogonalTiledMapRenderer(TiledMap(), 0.01f)
    var currentMap = 0
    var rooms = ArrayList<Room>()
    val worldIO = WorldIO()

    init {
        if (newWorld) createNewWorld() else continueWorld()

        Gdx.input.inputProcessor = hud.stage

        world.setContactListener(worldContactListener)
        world.setContactFilter(worldContactListener)
    }

    override fun render(delta: Float) {
        debugRenderer.render(world, camera.combined)
        world.step(1 / 60f, 8, 3)
        player.update(delta)
        moveCamera()
        mapRenderer.setView(camera)
        mapRenderer.render()
        mapRenderer.batch.begin()
        player.draw(mapRenderer.batch)
        mapRenderer.batch.end()
        animationCreator.updateAnimations()
        worldContactListener.update()
        hud.update()
        hud.stage.draw()
        hud.l.setText("$currentMap")
        miniMap.render()
        checkRoomChange(player)
    }

    override fun pause() {
        worldIO.preferences.putFloat("PlayerPositionX", player.playerBody.position.x)
        worldIO.preferences.putFloat("PlayerPositionY", player.playerBody.position.y)
        worldIO.preferences.flush()
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }

    private fun moveCamera() {

        camera.position.x = player.playerBody.position.x
        camera.position.y = player.playerBody.position.y

        if (camera.position.y - viewport.worldHeight / 2f < 0)
            camera.position.y = viewport.worldHeight / 2f

        if (camera.position.x - viewport.worldWidth / 2f < 0)
            camera.position.x = viewport.worldWidth / 2f

        if (camera.position.x + viewport.worldWidth / 2f > rooms[currentMap].mapWidth)
            camera.position.x = rooms[currentMap].mapWidth - viewport.worldWidth / 2f

        if (camera.position.y + viewport.worldHeight / 2f > rooms[currentMap].mapHeight)
            camera.position.y = rooms[currentMap].mapHeight - viewport.worldHeight / 2f

        camera.update()
    }

    fun createNewWorld() {
        rooms.add(Room())
        rooms[0].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[0].loadTileMap(worldCreator, "Maps/up/up1.tmx")

        worldCreator.createWorld(100, rooms)

        System.out.println("Size of rooms: ${rooms.size}")

        currentMap = 0

        rooms.forEach { it.map!!.dispose()
            it.map = null}

        worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)
        worldIO.preferences.putInteger("CurrentMap", 0)
        worldIO.preferences.flush()

        (0..4).forEach {
            rooms[it].loadTileMap(worldCreator)
            rooms[it].loadBodies(worldCreator, this)
        }

        rooms[0].setAllBodiesActivity(true)
        mapRenderer.map = rooms[0].map

        animationCreator.createTileAnimation(0, rooms)


    }

    fun continueWorld() {
        rooms = worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = worldIO.preferences.getInteger("CurrentMap")
        worldIO.loadLastState(mapRenderer, player, rooms, this, currentMap)
    }

    private fun changeRoom(player: Player, link: Int, side: String, plX: Int, plY: Int) {
        val previousMapLink = currentMap

        currentMap = rooms[currentMap].roomLinks[link]

        mapRenderer.map = rooms[currentMap].map
        rooms[currentMap].setAllBodiesActivity(true)

        player.setPlayerPosition(side, player, plX, plY, previousMapLink)

        worldIO.preferences.putInteger("CurrentMap", currentMap)
        worldIO.preferences.flush()

        thread(start = true){
            Gdx.app.postRunnable {

                rooms[previousMapLink].setAllBodiesActivity(false)
                rooms[previousMapLink].roomLinks.forEach {
                    if (it != DEFAULT && it != currentMap){
                        rooms[it].map!!.dispose()
                        rooms[it].groundBodies.forEach { body -> body.fixtureList.forEach{ body.destroyFixture(it) } }
                        rooms[it].platformBodies.forEach { body -> body.fixtureList.forEach{ body.destroyFixture(it) } }
                    }
                }

                rooms[currentMap].roomLinks.forEach {
                    if (it != DEFAULT) {
                        rooms[it].loadTileMap(worldCreator)
                        rooms[it].loadBodies(worldCreator, this)
                    }
                }

                // Create animation for current room
                animationCreator.createTileAnimation(currentMap, rooms)
            }
        }
    }

    fun checkRoomChange(player: Player) {
        val posX = player.playerBody.position.x
        val posY = player.playerBody.position.y

        if (posX > rooms[currentMap].mapWidth && posY < REG_ROOM_HEIGHT) changeRoom(player, 2, "Right", 2, 1) else // 2

            if (posX < 0 && posY < REG_ROOM_HEIGHT) changeRoom(player, 0, "Left", 0, 1) else // 1

                if (posY > rooms[currentMap].mapHeight && posX < REG_ROOM_WIDTH) changeRoom(player, 1, "Up", 0, 3) else // 4

                    if (posY < 0 && posX < REG_ROOM_WIDTH) changeRoom(player, 3, "Down", 0, 1) else // 1

                        if (posX < 0 && posY > REG_ROOM_HEIGHT) changeRoom(player, 4, "Left", 0, 3) else // 4

                            if (posY < 0 && posX > REG_ROOM_WIDTH) changeRoom(player, 7, "Down", 2, 1) else // 2

                                if (posX > rooms[currentMap].mapWidth && posY > REG_ROOM_HEIGHT) changeRoom(player, 6, "Right", 2, 3) else // 3

                                    if (posY > rooms[currentMap].mapHeight && posX > REG_ROOM_WIDTH) changeRoom(player, 5, "Up", 2, 3) // 3
    }

}
