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
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Room
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Screens.Scenes.MiniMap
import ru.icarumbas.bagel.Utils.B2dWorldCreator.WorldContactListener
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import ru.icarumbas.bagel.Utils.WorldCreate.WorldCreator
import ru.icarumbas.bagel.WorldIO
import kotlin.concurrent.thread

class GameScreen(game: Bagel, newWorld: Boolean): ScreenAdapter() {

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
        world.step(1 / 60f, 8, 3)
        player.update(delta)
        moveCamera()
        mapRenderer.setView(camera)
        mapRenderer.render()
        mapRenderer.batch.begin()
        checkRoomChange(player)
        player.draw(mapRenderer.batch)
        mapRenderer.batch.end()
        animationCreator.updateAnimations()
        worldContactListener.update()
        hud.update(player)
        hud.stage.draw()
        hud.l.setText("$currentMap")
        miniMap.render()
        debugRenderer.render(world, camera.combined)
        checkRoomChange(player)
    }

    override fun pause() {
        worldIO.preferences.putFloat("PlayerPositionX", player.playerBody.position.x)
        worldIO.preferences.putFloat("PlayerPositionY", player.playerBody.position.y)
        worldIO.preferences.flush()
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

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
    }

    fun createNewWorld() {
        rooms.add(Room())
        rooms[0].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[0].loadTileMap(worldCreator, "Maps/up/up1.tmx")

        // Fill the mesh with nulls
        for (y in 0..49) {
            for (x in 0..49) {
                worldCreator.mesh[y][x] = 0
            }
        }

        // Set center of the mesh
        worldCreator.mesh[25][25] = 1


        // Try to create map for each map[i] side
        for (i in 0..100) {
            if (i == rooms.size) break
            worldCreator.generateRoom("Maps/up/up", "Up", 1, 3, i, 0, -1, 0, 3, 0, 1, rooms)
            worldCreator.generateRoom("Maps/down/down", "Down", 3, 1, i, 0, 1, 0, 1, 0, 3, rooms)
            worldCreator.generateRoom("Maps/right/right", "Right", 2, 0, i, 1, 0, 2, 1, 0, 1, rooms)
            worldCreator.generateRoom("Maps/left/left", "Left", 0, 2, i, -1, 0, 0, 1, 2, 1, rooms)

            if (rooms[i].map!!.properties["Height"] != 5.12f) {
                worldCreator.generateRoom("Maps/right/right", "Right", 6, 0, i, 1, 0, 2, 3, 0, 3, rooms)
                worldCreator.generateRoom("Maps/left/left", "Left", 4, 2, i, -1, 0, 0, 3, 2, 3, rooms)
            }
            if (rooms[i].map!!.properties["Width"] != 7.68f) {
                worldCreator.generateRoom("Maps/up/up", "Up", 5, 3, i, 0, -1, 2, 3, 2, 1, rooms)
                worldCreator.generateRoom("Maps/down/down", "Down", 7, 1, i, 0, 1, 2, 1, 2, 3, rooms)
            }

        }
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
                    if (rooms[previousMapLink].roomLinks.indexOf(it) != link && it != 999){
                        rooms[it].map!!.dispose()
                        rooms[it].groundBodies.forEach { body -> body.fixtureList.forEach{ body.destroyFixture(it) } }
                        rooms[it].platformBodies.forEach { body -> body.fixtureList.forEach{ body.destroyFixture(it) } }
                    }
                }

                rooms[currentMap].roomLinks.forEach {
                    if (it != 999) {
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

        if (posX > rooms[currentMap].mapWidth && posY < 5.12f) changeRoom(player, 2, "Right", 2, 1) else // 2

            if (posX < 0 && posY < 5.12f) changeRoom(player, 0, "Left", 0, 1) else // 1

                if (posY > rooms[currentMap].mapHeight && posX < 7.68f) changeRoom(player, 1, "Up", 0, 3) else // 4

                    if (posY < 0 && posX < 7.68f) changeRoom(player, 3, "Down", 0, 1) else // 1

                        if (posX < 0 && posY > 5.12f) changeRoom(player, 4, "Left", 0, 3) else // 4

                            if (posY < 0 && posX > 7.68f) changeRoom(player, 7, "Down", 2, 1) else // 2

                                if (posX > rooms[currentMap].mapWidth && posY > 5.12f) changeRoom(player, 6, "Right", 2, 3) else // 3

                                    if (posY > rooms[currentMap].mapHeight && posX > 7.68f) changeRoom(player, 5, "Up", 2, 3) // 3
    }

}
