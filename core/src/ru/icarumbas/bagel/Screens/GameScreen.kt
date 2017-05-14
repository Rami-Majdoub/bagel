package ru.icarumbas.bagel.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
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
import ru.icarumbas.bagel.Utils.B2dWorldCreator.B2DWorldCreator
import ru.icarumbas.bagel.Utils.B2dWorldCreator.WorldContactListener
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator
import ru.icarumbas.bagel.Utils.WorldCreate.Room
import ru.icarumbas.bagel.Utils.WorldCreate.WorldCreator
import ru.icarumbas.bagel.Utils.WorldCreate.WorldIO


class GameScreen(newWorld: Boolean): ScreenAdapter() {
    val mapRenderer: OrthogonalTiledMapRenderer
    val assetManager = AssetManager()
    private val debugRenderer = Box2DDebugRenderer()
    private val camera = OrthographicCamera(REG_ROOM_WIDTH, REG_ROOM_HEIGHT)
    private val viewport = FitViewport(camera.viewportWidth, camera.viewportHeight, camera)
    val animationCreator: AnimationCreator
    val worldCreator: WorldCreator
    private val worldContactListener = WorldContactListener(this)
    val miniMap = MiniMap()
    var currentMap = 0
    var rooms = ArrayList<Room>()
    val worldIO = WorldIO()
    val textureAtlas = TextureAtlas(Gdx.files.internal("Packs/RoomObjects.txt"))
    val b2DWorldCreator = B2DWorldCreator()
    val world = World(Vector2(0f, -9.8f), true)
    val player = Player(this)
    val hud = Hud(player)
    val groundBodies = HashMap<String, ArrayList<Body>>()


    init {
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
        (0..TILED_MAPS_TOTAL).forEach {
            assetManager.load("Maps/Map$it.tmx", TiledMap::class.java)
        }
        assetManager.finishLoading()

        worldCreator = WorldCreator(assetManager)
        animationCreator = AnimationCreator(assetManager)

        if (newWorld) createNewWorld() else continueWorld()

        (0..TILED_MAPS_TOTAL).forEach {
            val bodies = ArrayList<Body>()
            b2DWorldCreator.loadGround(assetManager.get("Maps/Map$it.tmx", TiledMap::class.java).layers.get("platform"), world, bodies, PLATFORM_BIT)
            b2DWorldCreator.loadGround(assetManager.get("Maps/Map$it.tmx", TiledMap::class.java).layers.get("ground"), world, bodies, GROUND_BIT)
            groundBodies.put("Maps/Map$it.tmx", bodies)

        }

        rooms.forEach { it.mapObjects.forEach { it.loadSprite(textureAtlas)
                                               it.defineBody(world) } }

        mapRenderer = OrthogonalTiledMapRenderer(assetManager.get(rooms[currentMap].path), 0.01f)
        groundBodies[rooms[currentMap].path]!!.forEach { it.isActive = true }

        Gdx.input.inputProcessor = hud.stage

        world.setContactListener(worldContactListener)
        world.setContactFilter(worldContactListener)


    }

    override fun render(delta: Float) {
        debugRenderer.render(world, camera.combined)
        world.step(1 / 60f, 8, 3)
        mapRenderer.setView(camera)
        mapRenderer.render()
        player.update(delta)

        mapRenderer.batch.begin()
        player.draw(mapRenderer.batch)
        rooms[currentMap].draw(mapRenderer.batch)
        mapRenderer.batch.end()
        animationCreator.updateAnimations()
        hud.update(currentMap)
        moveCamera()
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
        rooms[currentMap].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[currentMap].loadMap("Maps/Map0.tmx", assetManager)

        worldCreator.createWorld(100, rooms)

        System.out.println("Size of rooms: ${rooms.size}")
        currentMap = 0

        rooms.forEach { it.loadMapObjects(b2DWorldCreator, assetManager) }

        worldIO.writeRoomsToJson("roomsFile.Json", rooms, false)
        worldIO.preferences.putInteger("CurrentMap", currentMap)
        worldIO.preferences.flush()

        animationCreator.createTileAnimation(currentMap, rooms)

    }

    fun continueWorld() {
        rooms = worldIO.loadRoomsFromJson("roomsFile.Json")
        currentMap = worldIO.getCurrentMapNumber("CurrentMap")
        worldIO.loadLastPlayerState(player)

        animationCreator.createTileAnimation(currentMap, rooms)
    }

    private fun changeRoom(player: Player, link: Int, side: String, plX: Int, plY: Int) {
        player.setPlayerPosition(side, player, plX, plY, currentMap)
        groundBodies[rooms[currentMap].path]!!.forEach { it.isActive = false }

        rooms[currentMap].mapObjects.forEach { it.sprite = null
                                               it.body.isActive = false }

        currentMap = rooms[currentMap].roomLinks[link]!!

        rooms[currentMap].mapObjects.forEach { it.loadSprite(textureAtlas)
                                               it.body.isActive = true }

        mapRenderer.map = assetManager.get(rooms[currentMap].path, TiledMap::class.java)
        groundBodies[rooms[currentMap].path]!!.forEach { it.isActive = true }

        animationCreator.createTileAnimation(currentMap, rooms)

        worldIO.preferences.putInteger("CurrentMap", currentMap)
        worldIO.preferences.flush()



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
