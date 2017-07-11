package ru.icarumbas.bagel.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.*
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Screens.Scenes.MiniMap
import ru.icarumbas.bagel.Utils.B2dWorld.B2DWorldCreator
import ru.icarumbas.bagel.Utils.B2dWorld.WorldContactListener
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import ru.icarumbas.bagel.Utils.WorldCreate.Room
import ru.icarumbas.bagel.Utils.WorldCreate.WorldCreator


class GameScreen(newWorld: Boolean, val game: Bagel): ScreenAdapter() {

    val mapRenderer: OrthogonalTiledMapRenderer
    private val debugRenderer = Box2DDebugRenderer()
    private val viewport = FitViewport(REG_ROOM_WIDTH, REG_ROOM_HEIGHT, OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT))
    val animationCreator: AnimationCreator = AnimationCreator(game.assetManager)
    val worldCreator: WorldCreator = WorldCreator(game.assetManager)
    val miniMap = MiniMap()
    var currentMap = 0
    var rooms = ArrayList<Room>()
    val b2DWorldCreator = B2DWorldCreator()
    val world = World(Vector2(0f, -9.8f), true)
    val player = Player(this, animationCreator)
    val hud = Hud(this)
    val worldContactListener = WorldContactListener(this)
    val groundBodies = HashMap<String, ArrayList<Body>>()
    var isB2DWorldRendering = false


    enum class State {
        Standing,
        Running,
        Jumping,
        Attacking,
        Dead,
        Appearing,
        NULL
    }

    init {

//        var client = Client()
//        val serv = Server()

        if (newWorld) createNewWorld() else continueWorld()

        (0..TILED_MAPS_TOTAL).forEach {
            val bodies = ArrayList<Body>()

            if (game.assetManager.get("Maps/Map$it.tmx", TiledMap::class.java).layers.get("platform") != null) {
                b2DWorldCreator.loadGround(game.assetManager.get("Maps/Map$it.tmx",
                        TiledMap::class.java).layers.get("platform"), world, bodies, PLATFORM_BIT)
            }

            if (game.assetManager.get("Maps/Map$it.tmx", TiledMap::class.java).layers.get("ground") != null) {
                b2DWorldCreator.loadGround(game.assetManager.get("Maps/Map$it.tmx",
                        TiledMap::class.java).layers.get("ground"), world, bodies, GROUND_BIT)
            }

            groundBodies.put("Maps/Map$it.tmx", bodies)

        }

        rooms.forEach {
            it.enemies.forEach {
                if (!it.killed) it.defineBody(world)
            }
            it.mapObjects.forEach {
                if (!it.destroyed) it.defineBody(world)
            }

            if (rooms.indexOf(it) == currentMap) {
                it.enemies.forEach {
                    it.loadAnimation(game.assetManager.get("Packs/Enemies.txt", TextureAtlas::class.java), animationCreator)
                    it.body!!.isActive = true
                }
                it.mapObjects.forEach {
                    it.loadSprite(game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
                    it.body?.isActive = true
                }
            }
        }

        mapRenderer = OrthogonalTiledMapRenderer(game.assetManager.get(rooms[currentMap].path), 0.01f)
        groundBodies[rooms[currentMap].path]!!.forEach { it.isActive = true }

        Gdx.input.inputProcessor = hud.stage

        world.setContactListener(worldContactListener)
        println("Size of rooms: ${rooms.size}")
        println("World bodies count: ${world.bodyCount}")
    }

    override fun render(delta: Float) {
        mapRenderer.setView(viewport.camera as OrthographicCamera)
        mapRenderer.render()

        player.update(delta, hud)
        hud.update(this)

        mapRenderer.batch.begin()
        rooms[currentMap].draw(mapRenderer.batch, delta, this)
        player.draw(mapRenderer.batch)
        mapRenderer.batch.end()

        animationCreator.updateAnimations()
        moveCamera()
        miniMap.render()
        checkRoomChange(player)
        applyWorldRender()

        world.step(1 / 60f, 8, 3)
        worldContactListener.deleteBodies()

        if (isB2DWorldRendering) debugRenderer.render(world, viewport.camera.combined)

    }

    override fun pause() {
        rooms.forEach {
            it.removeUnserealizableObjects()
            it.clearEntities(worldContactListener)
        }
            game.worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)

            with(game.worldIO.preferences) {
                putFloat("PlayerPositionX", player.playerBody.position.x)
                putFloat("PlayerPositionY", player.playerBody.position.y)
                putInteger("Money", player.money)
                putInteger("CurrentMap", currentMap)
                flush()
            }

    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
        mapRenderer.dispose()
        game.assetManager.dispose()
    }

    override fun resize(width: Int, height: Int) {
        hud.stage.viewport.update(width, height, true)
    }

    fun applyWorldRender(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (!isB2DWorldRendering) isB2DWorldRendering = true else
            if (isB2DWorldRendering) isB2DWorldRendering = false
        }
    }

    private fun moveCamera() {

        viewport.camera.position.x = player.playerBody.position.x
        viewport.camera.position.y = player.playerBody.position.y

        if (viewport.camera.position.y - viewport.worldHeight / 2f < 0)
            viewport.camera.position.y = viewport.worldHeight / 2f

        if (viewport.camera.position.x - viewport.worldWidth / 2f < 0)
            viewport.camera.position.x = viewport.worldWidth / 2f

        if (viewport.camera.position.x + viewport.worldWidth / 2f > rooms[currentMap].mapWidth)
            viewport.camera.position.x = rooms[currentMap].mapWidth - viewport.worldWidth / 2f

        if (viewport.camera.position.y + viewport.worldHeight / 2f > rooms[currentMap].mapHeight)
            viewport.camera.position.y = rooms[currentMap].mapHeight - viewport.worldHeight / 2f

        viewport.camera.update()
    }

    fun createNewWorld() {
        rooms.add(Room())
        rooms[currentMap].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[currentMap].loadMap("Maps/Map0.tmx", game.assetManager)

        worldCreator.createWorld(100, rooms)

        currentMap = 0

        rooms.forEach { it.loadEntities(b2DWorldCreator, game.assetManager) }

        animationCreator.createTileAnimation(currentMap, rooms)

        with (game.worldIO.preferences) {
            putInteger("CurrentMap", currentMap)
            putBoolean("CanContinueWorld", true)
            flush()
        }

        game.worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)
    }

    fun continueWorld() {
        rooms = game.worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = game.worldIO.preferences.getInteger("CurrentMap")
        player.money = game.worldIO.preferences.getInteger("Money")
        game.worldIO.loadLastPlayerState(player)

        animationCreator.createTileAnimation(currentMap, rooms)
    }

    fun updateRoomObjects(previousRoom: Int, newRoom: Int){

        // Clear ground
        groundBodies[rooms[previousRoom].path]!!.forEach { it.isActive = false }

        rooms[previousRoom].clearEntities(worldContactListener)

        // New room
        rooms[newRoom].awakeEntities(animationCreator, game)

        // Change map
        mapRenderer.map = game.assetManager.get(rooms[newRoom].path, TiledMap::class.java)

        // Load ground
        groundBodies[rooms[newRoom].path]!!.forEach { it.isActive = true }

        // Animation
        animationCreator.createTileAnimation(newRoom, rooms)

        currentMap = newRoom
    }

    private fun moveToAnotherRoom(player: Player, link: Int, side: String, plX: Int, plY: Int) {
        // Previous room
        player.setRoomPosition(side, plX, plY, currentMap)

        // New Room
        updateRoomObjects(currentMap, rooms[currentMap].roomLinks[link]!!)
    }

    fun checkRoomChange(player: Player) {
        val posX = player.playerBody.position.x
        val posY = player.playerBody.position.y

        if (posX > rooms[currentMap].mapWidth && posY < REG_ROOM_HEIGHT)
            moveToAnotherRoom(player, 2, "Right", 2, 1) else

        if (posX < 0 && posY < REG_ROOM_HEIGHT)
            moveToAnotherRoom(player, 0, "Left", 0, 1) else

        if (posY > rooms[currentMap].mapHeight && posX < REG_ROOM_WIDTH)
            moveToAnotherRoom(player, 1, "Up", 0, 3) else

        if (posY < 0 && posX < REG_ROOM_WIDTH)
            moveToAnotherRoom(player, 3, "Down", 0, 1) else

        if (posX < 0 && posY > REG_ROOM_HEIGHT)
            moveToAnotherRoom(player, 4, "Left", 0, 3) else

        if (posY < 0 && posX > REG_ROOM_WIDTH)
            moveToAnotherRoom(player, 7, "Down", 2, 1) else

        if (posX > rooms[currentMap].mapWidth && posY > REG_ROOM_HEIGHT)
            moveToAnotherRoom(player, 6, "Right", 2, 3) else

        if (posY > rooms[currentMap].mapHeight && posX > REG_ROOM_WIDTH)
            moveToAnotherRoom(player, 5, "Up", 2, 3)
    }

}
