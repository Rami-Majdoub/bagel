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
import ru.icarumbas.bagel.Characters.mapObjects.Breakable
import ru.icarumbas.bagel.Characters.mapObjects.Chest
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
    private val camera = OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT)
    private val viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
    val animationCreator: AnimationCreator = AnimationCreator(game.assetManager)
    val worldCreator: WorldCreator = WorldCreator(game.assetManager)
    val miniMap = MiniMap()
    var currentMap = 0
    var rooms = ArrayList<Room>()
    val b2DWorldCreator = B2DWorldCreator()
    val world = World(Vector2(0f, -9.8f), true)
    val player = Player(this, animationCreator)
    val hud = Hud(player)
    val worldContactListener: WorldContactListener
    val groundBodies = HashMap<String, ArrayList<Body>>()
    var isWorldRendering = false



    init {

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

        rooms.forEach { it.mapObjects.forEach {
            it.defineBody(world)
        } }

        rooms[currentMap].mapObjects.forEach { it.loadSprite(game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java)) }

        rooms.forEach { it.enemies.forEach {
            it.defineBody(world)
        } }

        rooms[currentMap].enemies.forEach { it.loadSprite(game.assetManager.get("Packs/Enemies.txt", TextureAtlas::class.java), animationCreator) }


        mapRenderer = OrthogonalTiledMapRenderer(game.assetManager.get(rooms[currentMap].path), 0.01f)
        groundBodies[rooms[currentMap].path]!!.forEach { it.isActive = true }
        rooms[currentMap].mapObjects.forEach { it.body?.isActive = true }


        Gdx.input.inputProcessor = hud.stage

        worldContactListener = WorldContactListener(this)
        world.setContactListener(worldContactListener)
        println("Size of rooms: ${rooms.size}")
    }

    enum class State {
        Standing,
        Running,
        Jumping,
        Attacking,
        Dead,
        Appearing,
        NULL
    }

    override fun render(delta: Float) {
        mapRenderer.setView(camera)
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

        if (isWorldRendering) debugRenderer.render(world, camera.combined)

    }

    override fun pause() {
        game.worldIO.preferences.putFloat("PlayerPositionX", player.playerBody.position.x)
        game.worldIO.preferences.putFloat("PlayerPositionY", player.playerBody.position.y)
        game.worldIO.preferences.putInteger("Money", player.money)
        game.worldIO.preferences.putInteger("CurrentMap", currentMap)

//        worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)
        game.worldIO.preferences.flush()
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
            if (!isWorldRendering) isWorldRendering = true else
            if (isWorldRendering) isWorldRendering = false
        }
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
        rooms[currentMap].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[currentMap].loadMap("Maps/Map0.tmx", game.assetManager)

        worldCreator.createWorld(100, rooms)

        currentMap = 0

        rooms.forEach { it.loadMapObjects(b2DWorldCreator, game.assetManager) }

        game.worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)
        game.worldIO.preferences.putInteger("CurrentMap", currentMap)
        game.worldIO.preferences.flush()

        animationCreator.createTileAnimation(currentMap, rooms)

        game.worldIO.preferences.putBoolean("CanContinueWorld", true)
        game.worldIO.preferences.flush()

    }

    fun continueWorld() {
        rooms = game.worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = game.worldIO.preferences.getInteger("CurrentMap")
        player.money = game.worldIO.preferences.getInteger("Money")
        game.worldIO.loadLastPlayerState(player)

        animationCreator.createTileAnimation(currentMap, rooms)
    }

    fun updateRoomObjects(previousRoom: Int, newRoom: Int){
        // Previous room


        // Clear ground
        groundBodies[rooms[previousRoom].path]!!.forEach { it.isActive = false }

        // Clear chest coins
        rooms[previousRoom].mapObjects.forEach {
            if (it is Chest) {
                it.coins.forEach { body ->
                    worldContactListener.deleteList.add(body)
                }
                it.coins.clear()
            } else
            if (it is Breakable) {
                it.coins.forEach { body ->
                    worldContactListener.deleteList.add(body)
                }
                it.coins.clear()
            }

            it.sprite = null
            it.body?.isActive = false
        }

        // Deleting used MapObjects
        val it = rooms[previousRoom].mapObjects.iterator()
        while (it.hasNext()) {
            if (it.next().destroyed) it.remove()
        }

        rooms[previousRoom].enemies.forEach {
            it.coins.forEach { worldContactListener.deleteList.add(it) }
            it.coins.clear()
            it.sprite = null
            it.body?.isActive = false
        }

        // Deleting killed Enemies
        val itEn = rooms[previousRoom].enemies.iterator()
        while (itEn.hasNext()) {
            if (itEn.next().killed) itEn.remove()
        }

        // New room


        // Load mapObjects
        rooms[newRoom].mapObjects.forEach {
            it.loadSprite(game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            it.body!!.isActive = true
        }

        rooms[newRoom].enemies.forEach {
            it.loadSprite(game.assetManager.get("Packs/Enemies.txt", TextureAtlas::class.java), animationCreator)
            it.body!!.isActive = true
        }

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
