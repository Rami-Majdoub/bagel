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

    var currentMap = 0
    var random = 0
    var zeroRoomChance = 0
    var universalRoomChance = 0
    var mapWidth = 7.68f
    var mapHeight = 5.12f
    private var mapLinks = ArrayList<Array<Int>>()
    private val maps = ArrayList<TiledMap>()
    private val jsonMaps = ArrayList<String>()
    val bodyArrays = ArrayList<ArrayList<Body>>()
    val platformArrays = ArrayList<ArrayList<Body>>()
    private val tmxLoader = TmxMapLoader()
    private var map = tmxLoader.load("Maps/up/up1.tmx")
    private var mapRenderer = OrthogonalTiledMapRenderer(map, 0.01f)
    private var mesh = Array(50) { IntArray(50) }
    private val json = Json()
    private val mapsFile = Gdx.files.local("mapsFile.Json")
    private val mapLinksFile = Gdx.files.local("mapLinksFile.Json")
    private var randomMap: Int = 0
    var meshCheckSides = IntArray(8)
    val stringSides = arrayOf("Left", "Up", "Down", "Right")

    init {
        if (newWorld) createNewWorld() else continueWorld()
        animationCreator.createTileAnimation(0, maps)

    }

    private fun createNewWorld() {
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
        arr.addAll(0, 0, 0, 0, 0, 0, 0, 0, 25, 25, 25, 25)
        mapLinks.add(arr)

        for (y in 0..49) {
            for (x in 0..49) {
                mesh[y][x] = 0
            }
        }

        mesh[25][25] = 1

        for (i in 0..50) {
            if (i == maps.size) break
            generateRoom("Maps/right/right", "Right", 2, 0, i, 1, 0, 10, 9)
            generateRoom("Maps/left/left", "Left", 0, 2, i, -1, 0, 8, 9)
            generateRoom("Maps/up/up", "Up", 1, 3, i, 0, -1, 8, 11)
            generateRoom("Maps/down/down", "Down", 3, 1, i, 0, 1, 8, 9)

            generateRoom("Maps/right/right", "Right", 6, 4, i, 1, 0, 10, 11)
            generateRoom("Maps/left/left", "Left", 4, 6, i, -1, 0, 8, 11)
            generateRoom("Maps/up/up", "Up", 5, 7, i, 0, -1, 10, 11)
            generateRoom("Maps/down/down", "Down", 7, 5, i, 0, 1, 10, 9)
        }
        currentMap = 0

        json.setUsePrototypes(false)
        mapsFile.writeString(json.toJson(jsonMaps), false)
        mapLinksFile.writeString(json.toJson(mapLinks), false)
    }

    private fun continueWorld() {
        mapLinks = json.fromJson(ArrayList<Array<Int>>().javaClass, mapLinksFile)

        json.fromJson(ArrayList<String>().javaClass, mapsFile).forEach { maps.add(tmxLoader.load(it)) }

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

        if (player.playerBody!!.position.y > mapHeight) changeRoom(player, 1, 3, mapWidth / 2, 0f)

        if (player.playerBody!!.position.y < 0) changeRoom(player, 3, 1, mapWidth / 2, mapHeight)
    }

    private fun rand(values: Int): Int {
        random = MathUtils.random(1000)
        if (random < zeroRoomChance) {
            return 0
        }
        if (random < universalRoomChance){
            universalRoomChance = 0
            return 1
        }
        else
            return MathUtils.random(2, values)
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

    private fun checkMeshSides(side: String, meshX: Int, meshY: Int, mapRoomWidth: Int, mapRoomHeight: Int): Boolean{

    meshCheckSides = when (side) {
        "Left" -> intArrayOf(-1, 0, -1, -mapRoomHeight + 1, -mapRoomWidth, 0, -mapRoomWidth, -mapRoomHeight + 1)
        "Up" -> intArrayOf(0, -1, mapRoomWidth - 1, -1, 0, -mapRoomHeight, mapRoomWidth - 1, -mapRoomHeight)
        "Right" -> intArrayOf(1, 0, mapRoomWidth, -mapRoomHeight + 1, 1, -mapRoomHeight + 1, mapRoomWidth, -mapRoomHeight + 1)
        "Down" -> intArrayOf(0, 1, mapRoomWidth - 1, 1, 0, mapRoomHeight, mapRoomWidth - 1, mapRoomHeight)
        else -> IntArray(0)
    }

        return (mesh[meshY + meshCheckSides[1]][meshX + meshCheckSides[0]] == 0) &&
                (mesh[meshY + meshCheckSides[3]][meshX + meshCheckSides[2]] == 0) &&
                (mesh[meshY + meshCheckSides[5]][meshX + meshCheckSides[4]] == 0) &&
                (mesh[meshY + meshCheckSides[7]][meshX + meshCheckSides[6]] == 0)
}

    private fun chooseMap(path: String, side: String, count: Int, previousMapLink: Int, meshX: Int, meshY: Int): TiledMap {
        randomMap = rand(6)
        map = tmxLoader.load(path + randomMap + ".tmx")

        val mapRoomWidth = (map.properties["Width"].toString().toFloat() / 7.68f).toInt()
        val mapRoomHeight = (map.properties["Height"].toString().toFloat() / 5.12f).toInt()

        if (checkMeshSides(side, meshX, meshY, mapRoomWidth, mapRoomHeight)){
        val checkSides = when (side) {

            "Left" -> intArrayOf(-mapRoomWidth-1, -mapRoomWidth-1, -mapRoomWidth, -1, -mapRoomWidth, -1, 0, 0,
                                 0, -mapRoomHeight+1, -mapRoomHeight, -mapRoomHeight, mapRoomHeight, mapRoomHeight, 0, -mapRoomHeight+1)

            "Up" -> intArrayOf(-1, -1, 0, mapRoomWidth-1, 0, mapRoomWidth-1, mapRoomWidth, mapRoomWidth,
                                -1, -mapRoomHeight, -mapRoomHeight-1, -mapRoomHeight-1, 0, 0, -1, -mapRoomHeight)

            "Right" -> intArrayOf(0, 0, 1, mapRoomWidth, mapRoomWidth, 1, mapRoomWidth+1, mapRoomWidth+1,
                                0, -mapRoomHeight+1, -mapRoomHeight, -mapRoomHeight, 1, 1, -mapRoomHeight+1, 0)

            "Down" -> intArrayOf(-1, -1, 0, mapRoomWidth-1, 0, mapRoomWidth-1, mapRoomWidth, mapRoomWidth,
                                1, mapRoomHeight, 0, 0, mapRoomHeight+1, mapRoomHeight+1, 1, mapRoomHeight)
            else -> IntArray(0)
        }

            var place = 0
            for (link in mapLinks) {
                for (i in 0..7) {
                    if (meshX.plus(checkSides[i]) == link[8] && meshY.plus(checkSides[i + 8]) == link[9]) {
                        if ((maps[mapLinks.indexOf(link)].properties.get(stringSides[3 - place]) == "No" && map.properties.get(stringSides[place]) == "Yes") ||
                                (maps[mapLinks.indexOf(link)].properties.get(stringSides[3 - place]) == "Yes" && map.properties.get(stringSides[place]) == "No")) {
                            chooseMap(path, side, count, previousMapLink, meshX, meshY)
                        }
                    }
                if (i % 2 > 0) place++
                }
                place = 0
            }
            return map
        } else chooseMap(path, side, count, previousMapLink, meshX, meshY)

        return TiledMap()
    }
    
    private fun generateRoom(path: String, side: String, previousMapLink: Int, currentMapLink: Int, count: Int, closestX: Int, closestY: Int, placeX: Int, placeY: Int) {

        if (maps[count].properties.get(side) == "Yes") {
            val meshX = mapLinks[count][placeX]
            val meshY = mapLinks[count][placeY]

            if (mesh[meshY + closestY][meshX + closestX] == 0) {
                if (zeroRoomChance < 500) zeroRoomChance += 10
                universalRoomChance += 50
                currentMap++
                map = chooseMap(path, side, count, previousMapLink, meshX, meshY)
                maps.add(map)
                jsonMaps.add(path + randomMap + ".tmx")
                bodyArrays.add(ArrayList<Body>())
                worldCreator.loadBodies(maps[currentMap].layers.get("ground"), world, bodyArrays[currentMap], gameScreen.GROUND_BIT)
                platformArrays.add(ArrayList<Body>())
                if (maps[currentMap].layers.get("platform") != null)
                    worldCreator.loadBodies(maps[currentMap].layers.get("platform"), world, platformArrays[currentMap], gameScreen.PLATFORM_BIT)
                val sides = Array<Int>()
                sides.addAll(0, 0, 0, 0,  0, 0, 0, 0,  meshX + closestX, meshY + closestY, meshX + meshCheckSides[6], meshY + meshCheckSides[7])
                sides[currentMapLink] = count
                mapLinks[count][previousMapLink] = currentMap
                mapLinks.add(sides)
                for (i in 0..7 step 2) mesh[meshY + meshCheckSides[i+1]][meshX + meshCheckSides[i]] = 1
            }
            else {
                for (map in maps) {
                    if (meshX + closestX == mapLinks[maps.indexOf(map)][8] && meshY + closestY == mapLinks[maps.indexOf(map)][9]) {
                        mapLinks[count][previousMapLink] = maps.indexOf(map)
                        mapLinks[maps.indexOf(map)][currentMapLink] = count
                        break
                    }
                }
            }
        }
    }

}
