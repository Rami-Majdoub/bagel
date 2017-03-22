package ru.icarumbas.bagel.Tools.WorldCreate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Tools.B2dWorldCreator.B2DWorldCreator

class WorldCreator(private val worldCreator: B2DWorldCreator, private val world: World, newWorld: Boolean, private val animationCreator: AnimationCreator, val gameScreen: GameScreen) {

    var currentMap: Int = 0
    var random: Int = 0
    var zeroRoomChance: Int = 0
    var mapWidth = 7.68f
    var mapHeight = 5.12f
    var mapRoomWidth = 1
    var mapRoomHeight = 1
    private var mapLinks = ArrayList<Array<Int>>()
    private val maps = ArrayList<TiledMap>()
    private val jsonMaps = ArrayList<String>()
    val bodyArrays = ArrayList<ArrayList<Body>>()
    val platformArrays = ArrayList<ArrayList<Body>>()
    private var map = TiledMap()
    private var mapRenderer = OrthogonalTiledMapRenderer(map, 0.01f)
    private var mesh = Array(50) { IntArray(50) }
    private val tmxLoader = TmxMapLoader()
    private val json = Json()
    private val mapsFile = Gdx.files.local("mapsFile.Json")
    private val mapLinksFile = Gdx.files.local("mapLinksFile.Json")
    private var randomMap: Int = 0
    val checkSides = ArrayList<IntArray>()
    val stringSides = arrayOf("Left", "Up", "Right", "Down", "Right", "Down", "Left", "Up")

    init {
        if (newWorld) createNewWorld() else continueWorld()
        animationCreator.createTileAnimation(0, maps)

    }

    private fun createNewWorld() {

        map = tmxLoader.load("Maps/up/up1.tmx")
        maps.add(map)
        jsonMaps.add("Maps/up/up1.tmx")

        bodyArrays.add(ArrayList<Body>())
        worldCreator.loadBodies(map.layers.get("ground"), world, bodyArrays.first(), gameScreen.GROUND_BIT)

        for (body in bodyArrays[0]) body.isActive = true

        platformArrays.add(ArrayList<Body>())
        if (map.layers.get("platform") != null)
            worldCreator.loadBodies(map.layers.get("platform"), world, platformArrays.first(), gameScreen.PLATFORM_BIT)

        for (body in platformArrays[0]) body.isActive = true

        val arr = Array<Int>()
        arr.addAll(0, 0, 0, 0, 25, 25)
        mapLinks.add(arr)

        for (y in 0..49) {
            for (x in 0..49) {
                mesh[y][x] = 0
            }
        }

        mesh[25][25] = 1
        checkSides.add(intArrayOf(-2, -1, -1, -1, 0, -1, 0, 1))
        checkSides.add(intArrayOf(-1, 0, 1, 0, -1, -2, -1, -1))
        checkSides.add(intArrayOf(1, 1, 2, 1, 0, -1, 0, 1))
        checkSides.add(intArrayOf(-1, 0, 1, 0, 1, 1, 1, 2))

        for (i in 0..50) {
            if (i == maps.size) break
            generateRooms("Maps/right/right", "Right", 0, 1, 2, 0, i)
            generateRooms("Maps/left/left", "Left", 0, -1, 0, 2, i)
            generateRooms("Maps/up/up", "Up", -1, 0, 1, 3, i)
            generateRooms("Maps/down/down", "Down", 1, 0, 3, 1, i)
        }
        currentMap = 0

        json.setUsePrototypes(false)
        mapsFile.writeString(json.toJson(jsonMaps), false)
        mapLinksFile.writeString(json.toJson(mapLinks), false)
    }

    private fun continueWorld() {
        mapLinks = json.fromJson(ArrayList<Array<Int>>().javaClass, mapLinksFile)

        json.fromJson(ArrayList<String>().javaClass, mapsFile).forEach { path: String -> maps.add(tmxLoader.load(path)) }

        for (map in maps) {
            bodyArrays.add(ArrayList<Body>())
            worldCreator.loadBodies(map.layers.get("ground"), world, bodyArrays[maps.indexOf(map)], gameScreen.GROUND_BIT)

            platformArrays.add(ArrayList<Body>())
            if (map.layers.get("platform") != null)
                worldCreator.loadBodies(map.layers.get("platform"), world, platformArrays[maps.indexOf(map)], gameScreen.PLATFORM_BIT)
        }

        mapRenderer = OrthogonalTiledMapRenderer(maps[0], 0.01f)
        for (body in bodyArrays.first()) body.isActive = true
        for (body in platformArrays.first()) body.isActive = true


    }

    fun render(camera: OrthographicCamera, player: Player) {
        mapRenderer.setView(camera)
        mapRenderer.render()

        mapRenderer.batch.begin()
        player.draw(mapRenderer.batch)
        mapRenderer.batch.end()

        if (player.playerBody!!.position.x > mapWidth) changeRoom(player, 2, 0, .5f, 2.5f)

        if (player.playerBody!!.position.x < 0) changeRoom(player, 0, 2, mapWidth - .3f, 2.5f)

        if (player.playerBody!!.position.y > mapHeight) changeRoom(player, 1, 3, mapHeight / 2, 0f)

        if (player.playerBody!!.position.y < 0) changeRoom(player, 3, 1, mapWidth / 2, mapHeight)
    }

    private fun rand(values: Int): Int {
        random = MathUtils.random(1000)
        if (random < zeroRoomChance) {
            return 0
        } else
            return MathUtils.random(1, values)
    }

    private fun changeRoom(player: Player, link: Int, previousLink: Int, positionX: Float, positionY: Float) {
        currentMap = mapLinks[currentMap][link]
        mapRenderer.map = maps[currentMap]

        player.playerBody!!.setTransform(positionX, positionY, 0f)
        animationCreator.createTileAnimation(currentMap, maps)

        for (body in bodyArrays[currentMap]) {
            body.isActive = true
        }
        for (body in platformArrays[currentMap]) {
            body.isActive = true
        }
        for (body in bodyArrays[mapLinks[currentMap][previousLink]]) {
            body.isActive = false
        }
        for (body in platformArrays[mapLinks[currentMap][previousLink]]) {
            body.isActive = false
        }

        mapHeight = maps[currentMap].properties.get("Height").toString().toFloat()
        mapWidth = maps[currentMap].properties.get("Width").toString().toFloat()

    }

    private fun generateMap(path: String, count: Int, side: Int): TiledMap {
        randomMap = rand(6)
        map = tmxLoader.load(path + randomMap + ".tmx")

        mapRoomWidth = (map.properties["Width"].toString().toFloat() / 7.68f).toInt()
        mapRoomHeight = (map.properties["Height"].toString().toFloat() / 5.12f).toInt()

        for (link in mapLinks) {
            for (i in 0..3) {
                if (mapLinks[count][4].plus(checkSides[side][i]) == link[4] && mapLinks[count][5].plus(checkSides[side][i + 4]) == link[5]) {
                    if ((maps[mapLinks.indexOf(link)].properties.get(stringSides[i + 4]) == "No" && map.properties.get(stringSides[i]) == "Yes") ||
                            (maps[mapLinks.indexOf(link)].properties.get(stringSides[i + 4]) == "Yes" && map.properties.get(stringSides[i]) == "No")) {
                        generateMap(path, count, side)
                    }
                }
            }
        }
        return map
    }

    private fun generateRooms(path: String, side: String, meshPosY: Int, meshPosX: Int, previousMapLink: Int, currentMapLink: Int, count: Int) {

        if (maps[count].properties.get(side) == "Yes") {
            if (mesh[mapLinks[count][5] + meshPosY][mapLinks[count][4] + meshPosX] == 0) {
                currentMap++
                map = generateMap(path, count, previousMapLink)
                maps.add(map)
                jsonMaps.add(path + randomMap + ".tmx")
                bodyArrays.add(ArrayList<Body>())
                worldCreator.loadBodies(maps[currentMap].layers.get("ground"), world, bodyArrays[currentMap], gameScreen.GROUND_BIT)
                platformArrays.add(ArrayList<Body>())
                if (maps[currentMap].layers.get("platform") != null)
                    worldCreator.loadBodies(maps[currentMap].layers.get("platform"), world, platformArrays[currentMap], gameScreen.PLATFORM_BIT)
                val sides = Array<Int>()
                sides.addAll(0, 0, 0, 0, mapLinks[count][4] + meshPosX, mapLinks[count][5] + meshPosY)
                sides[currentMapLink] = count
                mapLinks[count][previousMapLink] = currentMap
                mapLinks.add(sides)
                mesh[sides[5]][sides[4]] = 1
                if (zeroRoomChance < 500) zeroRoomChance += 10

            } else {
                for (map in maps) {
                    if (mapLinks[count][4] + meshPosX == mapLinks[maps.indexOf(map)][4] && mapLinks[count][5] + meshPosY == mapLinks[maps.indexOf(map)][5]) {
                        mapLinks[count][previousMapLink] = maps.indexOf(map)
                        mapLinks[maps.indexOf(map)][currentMapLink] = count
                        break
                    }
                }
            }
        }
    }

}
