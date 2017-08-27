package ru.icarumbas.bagel

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.TILED_MAPS_TOTAL

class WorldCreator (val assetManager: AssetManager){

    var zeroRoomChance = 0
    lateinit var mesh: Array<IntArray>
    val stringSides = arrayOf("Left", "Up", "Down", "Right")
    var newRoom = Room()
    var roomCounter = 0
    var meshCheckSides = IntArray(8)

    fun createWorld(worldSize: Int, rooms: ArrayList<Room>){
        mesh = Array(worldSize / 2 + 1) { IntArray(worldSize / 2 + 1) }

        mesh.forEach { it.fill(0) }

        // Set center of the mesh
        mesh[worldSize.div(4)][worldSize.div(4)] = 1


        // Try to create map for each map[i] side
        for (i in 0..worldSize) {
            if (i == rooms.size) break

            generateRoom("Maps/Map", "Right", 2, i, 1, 0, 2, 1, 0, 1, 0, 3, rooms)
            generateRoom("Maps/Map", "Left", 0, i, -1, 0, 0, 1, 2, 1, 2, 3, rooms)
            generateRoom("Maps/Map", "Up", 1, i, 0, -1, 0, 3, 0, 1, 2, 1, rooms)
            generateRoom("Maps/Map", "Down", 3, i, 0, 1, 0, 1, 0, 3, 2, 3, rooms)


            if (rooms[i].mapHeight != REG_ROOM_HEIGHT) {
                generateRoom("Maps/Map", "Right", 6, i, 1, 0, 2, 3, 0, 3, 0, 1, rooms)
                generateRoom("Maps/Map", "Left", 4, i, -1, 0, 0, 3, 2, 3, 2, 1, rooms)
            }
            if (rooms[i].mapWidth != REG_ROOM_WIDTH) {
                generateRoom("Maps/Map", "Up", 5, i, 0, -1, 2, 3, 2, 1, 0, 1, rooms)
                generateRoom("Maps/Map", "Down", 7, i, 0, 1, 2, 1, 2, 3, 0, 3, rooms)
            }

        }
    }

    private fun rand(values: Int): Int {
        val random = MathUtils.random(1000)
        if (random < zeroRoomChance) {
            return MathUtils.random(0, 3)
        } else
            return MathUtils.random(4, values)
    }

    private fun checkFit(side: String, meshX: Int, meshY: Int, mapRoomWidth: Int, mapRoomHeight: Int): Boolean {
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

    private fun chooseMap(path: String = "", side: String, count: Int, meshX: Int, meshY: Int, rooms: ArrayList<Room>): Room {

        newRoom = Room()
        newRoom.loadMap("$path${rand(TILED_MAPS_TOTAL)}.tmx", assetManager, roomCounter)

        val mapRoomWidth = (newRoom.mapWidth / REG_ROOM_WIDTH).toInt()
        val mapRoomHeight = (newRoom.mapHeight / REG_ROOM_HEIGHT).toInt()

        // Check that new room fits
        if (checkFit(side, meshX, meshY, mapRoomWidth, mapRoomHeight)) {


            val checkSides = when (side) {

                "Left" -> intArrayOf(-mapRoomWidth - 1, -mapRoomWidth - 1, -mapRoomWidth, -1, -mapRoomWidth, -1, 0, 0, // Xss
                        0, -mapRoomHeight + 1, -mapRoomHeight, -mapRoomHeight, 1, 1, 0, -mapRoomHeight + 1) // Yss

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

                        if ((assetManager.get(it.path, TiledMap::class.java).properties.get(stringSides[3 - place]) != "Yes"
                          && assetManager.get(newRoom.path, TiledMap::class.java).properties.get(stringSides[place]) == "Yes") ||
                            (assetManager.get(it.path, TiledMap::class.java).properties.get(stringSides[3 - place]) == "Yes"
                          && assetManager.get(newRoom.path, TiledMap::class.java).properties.get(stringSides[place]) != "Yes")) {
                            chooseMap(path, side, count, meshX, meshY, rooms)
                        }

                    }
                    if (i % 2 > 0) place++
                }
                place = 0
            }

        }
        else chooseMap(path, side, count, meshX, meshY, rooms)

        return newRoom
    }

    fun drawMesh(count: Int, meshX: Int, meshY: Int){
        println("Count: $count, Size: $roomCounter")

        mesh.forEach {
            x -> x.forEach { y ->
                if (y == 1) print("* ") else print("  ")
            }
            println()
        }

    }

    //TODO("Rename all this fucking values. ")
    fun generateRoom(path: String,
                     side: String,
                     previousMapLink: Int,
                     count: Int,
                     closestX: Int,
                     closestY: Int,
                     placeX: Int,
                     placeY: Int,
                     linkX: Int,
                     linkY: Int,
                     linkXB: Int,
                     linkYB: Int,
                     rooms: ArrayList<Room>)
    {

        // Check that room have gate to create specific map
        if (assetManager.get(rooms[count].path, TiledMap::class.java).properties.get(side) == "Yes") {
            // X and y positions on mesh. For big maps part of it
            val meshX = rooms[count].meshVertices[placeX]
            val meshY = rooms[count].meshVertices[placeY]


            // Check that mesh don't have another room with it's coordinates
            if (mesh[meshY + closestY][meshX + closestX] == 0) {
                if (zeroRoomChance < 750) zeroRoomChance += 15
                roomCounter++
                rooms.add(chooseMap(path, side, count, meshX, meshY, rooms))
                rooms[roomCounter].meshVertices = intArrayOf(meshX + meshCheckSides[0], meshY + meshCheckSides[1], meshX + meshCheckSides[6], meshY + meshCheckSides[7])
                rooms[count].roomLinks[previousMapLink] = roomCounter

                // Fill mesh on new room's coordinates
                for (i in 0..7 step 2) mesh[meshY + meshCheckSides[i + 1]][meshX + meshCheckSides[i]] = 1

                drawMesh(count, meshX, meshY)

            }

            // Else add link
            else {
                // Search in mapLinks room with collided coordinates
                for (it in rooms) {
                    if (meshX + closestX == it.meshVertices[linkX] && meshY + closestY == it.meshVertices[linkY] ||
                        meshX + closestX == it.meshVertices[linkXB] && meshY + closestY == it.meshVertices[linkYB]) {
                        // Add link to main room
                        rooms[count].roomLinks[previousMapLink] = rooms.indexOf(it)
                        return
                    }
                }
            }

        }
    }

}
