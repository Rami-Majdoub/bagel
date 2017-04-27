package ru.icarumbas.bagel.Tools.WorldCreate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Json
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Room
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Tools.B2dWorldCreator.B2DWorldCreator
import kotlin.concurrent.thread

class WorldCreator(val gameScreen: GameScreen) {

    var currentMap = 0
    var random = 0
    var zeroRoomChance = 0
    var rooms = ArrayList<Room>()
    val tmxLoader = TmxMapLoader()
    var mapRenderer = OrthogonalTiledMapRenderer(TiledMap(), 0.01f)
    private var mesh = Array(50) { IntArray(50) }
    private val json = Json()
    private var randomMap: Int = 0
    var meshCheckSides = IntArray(8)
    val stringSides = arrayOf("Left", "Up", "Down", "Right")
    val b2DWorldCreator = B2DWorldCreator()
    var newRoom: Room? = null
    private val roomsFile = Gdx.files.local("roomsFile.Json")

    fun createNewWorld() {
        rooms.add(Room())
        rooms[0].meshVertices = intArrayOf(25, 25, 25, 25)
        rooms[0].loadTileMap(this, "Maps/up/up1.tmx")

        // Fill the mesh with nulls
        for (y in 0..49) {
            for (x in 0..49) {
                mesh[y][x] = 0
            }
        }

        // Set center of the mesh
        mesh[25][25] = 1


        // Try to create map for each map[i] side
        for (i in 0..100) {
            if (i == rooms.size) break
            generateRoom("Maps/up/up", "Up", 1, 3, i, 0, -1, 0, 3, 0, 1)
            generateRoom("Maps/down/down", "Down", 3, 1, i, 0, 1, 0, 1, 0, 3)
            generateRoom("Maps/right/right", "Right", 2, 0, i, 1, 0, 2, 1, 0, 1)
            generateRoom("Maps/left/left", "Left", 0, 2, i, -1, 0, 0, 1, 2, 1)

            if (rooms[i].map!!.properties["Height"] != 5.12f) {
                generateRoom("Maps/right/right", "Right", 6, 0, i, 1, 0, 2, 3, 0, 3)
                generateRoom("Maps/left/left", "Left", 4, 2, i, -1, 0, 0, 3, 2, 3)
            }
            if (rooms[i].map!!.properties["Width"] != 7.68f) {
                generateRoom("Maps/up/up", "Up", 5, 3, i, 0, -1, 2, 3, 2, 1)
                generateRoom("Maps/down/down", "Down", 7, 1, i, 0, 1, 2, 1, 2, 3)
            }

        }
        currentMap = 0

        rooms.forEach { it.map!!.dispose()
                        it.map = null}

        // To write repetitive maps
        json.setUsePrototypes(false)


        roomsFile.writeString(json.prettyPrint(rooms), false)

        loadForFirstRoom()

    }

    fun loadForFirstRoom(){
        (0..4).forEach {
            rooms[it].loadTileMap(this)
            rooms[it].loadBodies(this, gameScreen)
             }

        rooms[0].setAllBodiesActivity(true)
        mapRenderer.map = rooms[0].map
    }

    fun continueWorld() {
        rooms = json.fromJson(ArrayList<Room>().javaClass, roomsFile)
        loadForFirstRoom()
    }

    fun checkRoomChange(player: Player) {
        val posX = player.playerBody.position.x
        val posY = player.playerBody.position.y

        /* Check from which position of map has player came to other map
         *                                43
         *                                12
         * This is positions for 2x2 map ^ ^ ^
        */
        if (posX > rooms[currentMap].mapWidth && posY < 5.12f) changeRoom(player, 2, "Right", 2, 1) else // 2

            if (posX < 0 && posY < 5.12f) changeRoom(player, 0, "Left", 0, 1) else // 1

                if (posY > rooms[currentMap].mapHeight && posX < 7.68f) changeRoom(player, 1, "Up", 0, 3) else // 4

                    if (posY < 0 && posX < 7.68f) changeRoom(player, 3, "Down", 0, 1) else // 1

                        if (posX < 0 && posY > 5.12f) changeRoom(player, 4, "Left", 0, 3) else // 4

                            if (posY < 0 && posX > 7.68f) changeRoom(player, 7, "Down", 2, 1) else // 2

                                if (posX > rooms[currentMap].mapWidth && posY > 5.12f) changeRoom(player, 6, "Right", 2, 3) else // 3

                                    if (posY > rooms[currentMap].mapHeight && posX > 7.68f) changeRoom(player, 5, "Up", 2, 3) // 3
    }

    private fun rand(values: Int): Int {
        random = MathUtils.random(1000)
        if (random < zeroRoomChance) {
            return 0
        } else
            return MathUtils.random(1, values)
    }

    private fun changeRoom(player: Player, link: Int, side: String, plX: Int, plY: Int) {
        val previousMapLink = currentMap

        currentMap = rooms[currentMap].roomLinks[link]

        mapRenderer.map = rooms[currentMap].map
        rooms[currentMap].setAllBodiesActivity(true)

        gameScreen.player.setPlayerPosition(side, player, plX, plY, previousMapLink)

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
                        rooms[it].loadTileMap(this)
                        rooms[it].loadBodies(this, gameScreen)
                    }
                }

                // Create animation for current room
                gameScreen.animationCreator.createTileAnimation(currentMap, rooms)
            }
        }
    }

    private fun checkMeshSides(side: String, meshX: Int, meshY: Int, mapRoomWidth: Int, mapRoomHeight: Int): Boolean {

        /* Check positions on mesh to check if room fits
                                                                    13
                                                                    02
         * Here's order to check pairs(x, y) of parts of 2x2 map  ^ ^ ^
         */

        // (x, y)
        meshCheckSides = when (side) {
            "Left" -> intArrayOf(-mapRoomWidth, 0, -mapRoomWidth, -mapRoomHeight + 1, -1, 0, -1, -mapRoomHeight + 1)
            "Up" -> intArrayOf(0, -1, mapRoomWidth - 1, -1, 0, -mapRoomHeight, mapRoomWidth - 1, -mapRoomHeight)
            "Right" -> intArrayOf(1, 0, mapRoomWidth, 0, 1, -mapRoomHeight + 1, mapRoomWidth, -mapRoomHeight + 1)
            "Down" -> intArrayOf(0, mapRoomHeight, 0, 1, mapRoomWidth - 1, mapRoomHeight, mapRoomWidth - 1, 1)
            else -> IntArray(0)
        }

        // Returns true if room fits
        return (mesh[meshY + meshCheckSides[1]][meshX + meshCheckSides[0]] == 0) &&
                (mesh[meshY + meshCheckSides[3]][meshX + meshCheckSides[2]] == 0) &&
                (mesh[meshY + meshCheckSides[5]][meshX + meshCheckSides[4]] == 0) &&
                (mesh[meshY + meshCheckSides[7]][meshX + meshCheckSides[6]] == 0)
    }

    private fun chooseMap(path: String, side: String, count: Int, meshX: Int, meshY: Int): Room {
        // Load room randomly
        randomMap = rand(8)
        newRoom = Room()
        newRoom!!.loadTileMap(this, path + randomMap + ".tmx")

        // Set new width and height
        val mapRoomWidth = (newRoom!!.mapWidth / 7.68f).toInt()
        val mapRoomHeight = (newRoom!!.mapHeight / 5.12f).toInt()

        // Check that new room fits
        if (checkMeshSides(side, meshX, meshY, mapRoomWidth, mapRoomHeight)) {

            /* (x0, x1, x2, x3
                y0, y1, y2, y3) */
            val checkSides = when (side) {

                "Left" -> intArrayOf(-mapRoomWidth - 1, -mapRoomWidth - 1, -mapRoomWidth, -1, -mapRoomWidth, -1, 0, 0, // Xs
                        0, -mapRoomHeight + 1, -mapRoomHeight, -mapRoomHeight, 1, 1, 0, -mapRoomHeight + 1) // Ys

                "Up" -> intArrayOf(-1, -1, 0, mapRoomWidth - 1, 0, mapRoomWidth - 1, mapRoomWidth, mapRoomWidth,
                        -1, -mapRoomHeight, -mapRoomHeight - 1, -mapRoomHeight - 1, 0, 0, -1, -mapRoomHeight)

                "Right" -> intArrayOf(0, 0, 1, mapRoomWidth, mapRoomWidth, 1, mapRoomWidth + 1, mapRoomWidth + 1,
                        0, -mapRoomHeight + 1, -mapRoomHeight, -mapRoomHeight, 1, 1, -mapRoomHeight + 1, 0)

                "Down" -> intArrayOf(-1, -1, 0, mapRoomWidth - 1, 0, mapRoomWidth - 1, mapRoomWidth, mapRoomWidth,
                        1, mapRoomHeight, 0, 0, mapRoomHeight + 1, mapRoomHeight + 1, 1, mapRoomHeight)
                else -> IntArray(0)
            }

            // Check for collisions with other rooms on mesh. If gates match using checkSides
            var place = 0
            rooms.forEach {
                for (i in 0..7) {
                    if ((meshX.plus(checkSides[i]) == it.meshVertices[0] || meshX.plus(checkSides[i]) == it.meshVertices[2]) &&
                            (meshY.plus(checkSides[i + 8]) == it.meshVertices[1] || meshY.plus(checkSides[i + 8]) == it.meshVertices[3])) {
                        if ((it.map!!.properties.get(stringSides[3 - place]) == "No" && newRoom!!.map!!.properties.get(stringSides[place]) == "Yes") ||
                                (it.map!!.properties.get(stringSides[3 - place]) == "Yes" && newRoom!!.map!!.properties.get(stringSides[place]) == "No")) {
                            chooseMap(path, side, count, meshX, meshY)
                        }
                    }
                    if (i % 2 > 0) place++
                }
                place = 0
            }

        }
        // Else recursion -__- Try to load another room
        else chooseMap(path, side, count,  meshX, meshY)

        return newRoom!!
    }

    private fun generateRoom(path: String, side: String, previousMapLink: Int, currentMapLink: Int, count: Int,
                             closestX: Int, closestY: Int, placeX: Int, placeY: Int, linkX: Int, linkY: Int) {

        // Check that room have gate to create specific map
        if (rooms[count].map!!.properties.get(side) == "Yes") {
            // X and y positions on mesh. For big maps part of it
            val meshX = rooms[count].meshVertices[placeX]
            val meshY = rooms[count].meshVertices[placeY]

            // Check that mesh don't have another room with it's coordinates
            if (mesh[meshY + closestY][meshX + closestX] == 0) {
                if (zeroRoomChance < 500) zeroRoomChance += 10
                currentMap++
                rooms.add(chooseMap(path, side, count, meshX, meshY))
                rooms[currentMap].meshVertices = intArrayOf(meshX + meshCheckSides[0], meshY + meshCheckSides[1], meshX + meshCheckSides[6], meshY + meshCheckSides[7])
                rooms[currentMap].roomLinks[currentMapLink] = count
                rooms[count].roomLinks[previousMapLink] = currentMap

                // Fill mesh on new room's coordinates
                for (i in 0..7 step 2) mesh[meshY + meshCheckSides[i + 1]][meshX + meshCheckSides[i]] = 1
            }

            // Else add link to collided and main rooms
            else {
                // Search in mapLinks room with collided coordinates
                for (it in rooms) {
                    if (meshX + closestX == it.meshVertices[linkX] && meshY + closestY == it.meshVertices[linkY]) {
                        // Add link to main room
                        rooms[count].roomLinks[previousMapLink] = rooms.indexOf(it)

                        // Add link to new room
                        it.roomLinks[currentMapLink] = count
                        break
                    }
                }
            }
        }
    }

}
