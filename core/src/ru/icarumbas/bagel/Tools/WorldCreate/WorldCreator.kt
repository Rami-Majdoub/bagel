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

class WorldCreator(private val worldCreator: B2DWorldCreator, private val world: World, private val animationCreator: AnimationCreator, val gameScreen: GameScreen) {

    var currentMap = 0
    var random = 0
    var zeroRoomChance = 0
    var mapWidth = 7.68f
    var mapHeight = 5.12f
    private var mapLinks = ArrayList<Array<Int>>()
    val maps = ArrayList<TiledMap>()
    private val jsonMaps = ArrayList<String>()
    val bodyArrays = ArrayList<ArrayList<Body>>()
    val platformArrays = ArrayList<ArrayList<Body>>()
    private val tmxLoader = TmxMapLoader()
    private var map = tmxLoader.load("Maps/up/up1.tmx")
    var mapRenderer = OrthogonalTiledMapRenderer(map, 0.01f)
    private var mesh = Array(50) { IntArray(50) }
    private val json = Json()
    private val mapsFile = Gdx.files.local("mapsFile.Json")
    private val mapLinksFile = Gdx.files.local("mapLinksFile.Json")
    private var randomMap: Int = 0
    var meshCheckSides = IntArray(8)
    val stringSides = arrayOf("Left", "Up", "Down", "Right")

    fun createNewWorld() {
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

        for (i in 0..100) {
            if (i == maps.size) break
            generateRoom("Maps/up/up", "Up", 1, 3, i, 0, -1, 8, 11, 8, 9)
            generateRoom("Maps/down/down", "Down", 3, 1, i, 0, 1, 8, 9, 8, 11)
            generateRoom("Maps/right/right", "Right", 2, 0, i, 1, 0, 10, 9, 8, 9)
            generateRoom("Maps/left/left", "Left", 0, 2, i, -1, 0, 8, 9, 10, 9)

            if (maps[i].properties["Height"] != 5.12f) {
                generateRoom("Maps/right/right", "Right", 6, 0, i, 1, 0, 10, 11, 8, 11)
                generateRoom("Maps/left/left", "Left", 4, 2, i, -1, 0, 8, 11, 10, 11)
            }
            if (maps[i].properties["Width"] != 7.68f){
                generateRoom("Maps/up/up", "Up", 5, 3, i, 0, -1, 10, 11, 10, 9)
                generateRoom("Maps/down/down", "Down", 7, 1, i, 0, 1, 10, 9, 10, 11)
            }

        }
        currentMap = 0

        json.setUsePrototypes(false)
        mapsFile.writeString(json.prettyPrint(jsonMaps), false)
        mapLinksFile.writeString(json.prettyPrint(mapLinks), false)

        for (y in 0..49) {
            for (x in 0..49) {
                if (mesh[y][x] == 1) print("${mesh[y][x]} ") else print(" ")
            }
            println()
        }
    }

    fun continueWorld() {
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

    fun checkRoomChange(player: Player){
        val posX = player.playerBody!!.position.x
        val posY = player.playerBody!!.position.y

        if (posX > mapWidth && posY < 5.12f) changeRoom(player, 2, "Right", 10, 9) else

        if (posX < 0 && posY < 5.12f) changeRoom(player, 0, "Left", 8, 9) else

        if (posY > mapHeight && posX < 7.68f) changeRoom(player, 1, "Up", 8, 11) else

        if (posY < 0 && posX < 7.68f) changeRoom(player, 3, "Down", 8, 9) else

        if (posX < 0 && posY > 5.12f) changeRoom(player, 4, "Left", 8, 11) else

        if (posY < 0 && posX > 7.68f) changeRoom(player, 7, "Down", 10, 9) else

        if (posX > mapWidth && posY > 5.12f) changeRoom(player, 6, "Right", 10, 11) else

        if (posY > mapHeight && posX > 7.68f) changeRoom(player, 5, "Up", 10, 11)
    }

    private fun rand(values: Int): Int {
        random = MathUtils.random(1000)
        if (random < zeroRoomChance) {
            return 0
        }
        else
            return MathUtils.random(1, values)
    }

    private fun setPlayerPosition(side: String, player: Player, plX: Int, plY: Int, previousMapLink: Int) {

        if (side == "Up" || side == "Down"){
            val X10 = mapLinks[currentMap][10]
            val prevX = mapLinks[previousMapLink][plX]

            if (side == "Up"){
                if (prevX == X10) player.playerBody!!.setTransform(mapWidth - 3.84f, 0f, 0f)
                else player.playerBody!!.setTransform(3.84f, 0f, 0f)
            }
            if (side == "Down"){
                if (prevX == X10) player.playerBody!!.setTransform(mapWidth - 3.84f, mapHeight, 0f)
                else player.playerBody!!.setTransform(3.84f, mapHeight, 0f)
            }
        }

        if (side == "Left" || side == "Right"){
            val Y11 = mapLinks[currentMap][11]
            val prevY = mapLinks[previousMapLink][plY]

            if (side == "Left"){
                if (prevY == Y11) player.playerBody!!.setTransform(mapWidth, mapHeight - 2.56f, 0f)
                else player.playerBody!!.setTransform(mapWidth, 2.56f, 0f)
            }
            if (side == "Right"){
                if (prevY == Y11) player.playerBody!!.setTransform(0f, mapHeight - 2.56f, 0f)
                else player.playerBody!!.setTransform(0f, 2.56f, 0f)
            }
        }


    }

    private fun changeRoom(player: Player, link: Int, side: String, plX: Int, plY: Int) {

        for (body in bodyArrays[currentMap]) {
            body.isActive = false
        }
        for (body in platformArrays[currentMap]) {
            body.isActive = false
        }

        val previousMapLink = currentMap
        currentMap = mapLinks[currentMap][link]
        mapRenderer.map = maps[currentMap]

        mapHeight = maps[currentMap].properties.get("Height").toString().toFloat()
        mapWidth = maps[currentMap].properties.get("Width").toString().toFloat()

        setPlayerPosition(side, player, plX, plY, previousMapLink)

        animationCreator.createTileAnimation(currentMap, maps)

        for (body in bodyArrays[currentMap]) {
            body.isActive = true
        }
        for (body in platformArrays[currentMap]) {
            body.isActive = true
        }

    }

    private fun checkMeshSides(side: String, meshX: Int, meshY: Int, mapRoomWidth: Int, mapRoomHeight: Int): Boolean{

    meshCheckSides = when (side) {
        "Left" -> intArrayOf(-mapRoomWidth, 0, -mapRoomWidth, -mapRoomHeight + 1, -1, 0, -1, -mapRoomHeight + 1)
        "Up" -> intArrayOf(0, -1, mapRoomWidth - 1, -1, 0, -mapRoomHeight, mapRoomWidth - 1, -mapRoomHeight)
        "Right" -> intArrayOf(1, 0, mapRoomWidth, 0, 1, -mapRoomHeight + 1, mapRoomWidth, - mapRoomHeight + 1)
        "Down" -> intArrayOf(0, mapRoomHeight, 0, 1, mapRoomWidth - 1, mapRoomHeight, mapRoomWidth - 1, 1)
        else -> IntArray(0)
    }

        return  (mesh[meshY + meshCheckSides[1]][meshX + meshCheckSides[0]] == 0) &&
                (mesh[meshY + meshCheckSides[3]][meshX + meshCheckSides[2]] == 0) &&
                (mesh[meshY + meshCheckSides[5]][meshX + meshCheckSides[4]] == 0) &&
                (mesh[meshY + meshCheckSides[7]][meshX + meshCheckSides[6]] == 0)
}

    private fun chooseMap(path: String, side: String, count: Int, previousMapLink: Int, meshX: Int, meshY: Int): TiledMap {
        randomMap = rand(10)
        map = tmxLoader.load(path + randomMap + ".tmx")

        val mapRoomWidth = (map.properties["Width"].toString().toFloat() / 7.68f).toInt()
        val mapRoomHeight = (map.properties["Height"].toString().toFloat() / 5.12f).toInt()

        if (checkMeshSides(side, meshX, meshY, mapRoomWidth, mapRoomHeight)){
        val checkSides = when (side) {

            "Left" -> intArrayOf(-mapRoomWidth-1, -mapRoomWidth-1, -mapRoomWidth, -1, -mapRoomWidth, -1, 0, 0,
                                 0, -mapRoomHeight+1, -mapRoomHeight, -mapRoomHeight, 1, 1, 0, -mapRoomHeight+1)

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
                    if ((meshX.plus(checkSides[i]) == link[8] || meshX.plus(checkSides[i]) == link[10]) &&
                            (meshY.plus(checkSides[i + 8]) == link[9] || meshY.plus(checkSides[i + 8]) == link[11])) {
                        if ((maps[mapLinks.indexOf(link)].properties.get(stringSides[3 - place]) == "No" && map.properties.get(stringSides[place]) == "Yes") ||
                                (maps[mapLinks.indexOf(link)].properties.get(stringSides[3 - place]) == "Yes" && map.properties.get(stringSides[place]) == "No")) {
                            chooseMap(path, side, count, previousMapLink, meshX, meshY)
                        }
                    }
                if (i % 2 > 0) place++
                }
                place = 0
            }

        } else chooseMap(path, side, count, previousMapLink, meshX, meshY)

        return map
    }
    
    private fun generateRoom(path: String, side: String, previousMapLink: Int, currentMapLink: Int, count: Int,
                             closestX: Int, closestY: Int, placeX: Int, placeY: Int, linkX: Int, linkY: Int) {

        if (maps[count].properties.get(side) == "Yes") {
            val meshX = mapLinks[count][placeX]
            val meshY = mapLinks[count][placeY]

            if (mesh[meshY + closestY][meshX + closestX] == 0) {
                if (zeroRoomChance < 500) zeroRoomChance += 10
                currentMap++
                maps.add(chooseMap(path, side, count, previousMapLink, meshX, meshY))
                jsonMaps.add(path + randomMap + ".tmx")
                bodyArrays.add(ArrayList<Body>())
                worldCreator.loadBodies(maps[currentMap].layers.get("ground"), world, bodyArrays[currentMap], gameScreen.GROUND_BIT)
                platformArrays.add(ArrayList<Body>())
                if (maps[currentMap].layers.get("platform") != null)
                    worldCreator.loadBodies(maps[currentMap].layers.get("platform"), world, platformArrays[currentMap], gameScreen.PLATFORM_BIT)
                val sides = Array<Int>()
                sides.addAll(0, 0, 0, 0,  0, 0, 0, 0,  meshX + meshCheckSides[0], meshY + meshCheckSides[1], meshX + meshCheckSides[6], meshY + meshCheckSides[7])
                sides[currentMapLink] = count
                mapLinks[count][previousMapLink] = currentMap
                mapLinks.add(sides)
                for (i in 0..7 step 2) mesh[meshY + meshCheckSides[i+1]][meshX + meshCheckSides[i]] = 1
            }
            else {
                for (i in mapLinks) {
                    if (meshX + closestX == i[linkX] && meshY + closestY == i[linkY]) {

                            mapLinks[count][previousMapLink] = mapLinks.indexOf(i)
                            i[currentMapLink] = count

                        break
                    }
                }
            }
        }
    }

}
