package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.Room
import ru.icarumbas.bagel.Utils.B2dWorldCreator.B2DWorldCreator

class WorldCreator {

    var random = 0
    var zeroRoomChance = 0
    val tmxLoader = TmxMapLoader()
    var mesh = Array(50) { IntArray(50) }
    private var randomMap: Int = 0
    val stringSides = arrayOf("Left", "Up", "Down", "Right")
    val b2DWorldCreator = B2DWorldCreator()
    var newRoom = Room()
    var roomCounter = 0
    var meshCheckSides = IntArray(8)

    private fun rand(values: Int): Int {
        random = MathUtils.random(1000)
        if (random < zeroRoomChance) {
            return 0
        } else
            return MathUtils.random(1, values)
    }

    private fun checkMeshSides(side: String, meshX: Int, meshY: Int, mapRoomWidth: Int, mapRoomHeight: Int): Boolean {
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

    private fun chooseMap(path: String, side: String, count: Int, meshX: Int, meshY: Int, rooms: ArrayList<Room>): Room {


        // Load room randomly
        randomMap = rand(8)
        newRoom = Room()
        newRoom.loadTileMap(this, path + randomMap + ".tmx")

        // Set new width and height
        val mapRoomWidth = (newRoom.mapWidth / 7.68f).toInt()
        val mapRoomHeight = (newRoom.mapHeight / 5.12f).toInt()

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
                        if ((it.map!!.properties.get(stringSides[3 - place]) == "No" && newRoom.map!!.properties.get(stringSides[place]) == "Yes") ||
                                (it.map!!.properties.get(stringSides[3 - place]) == "Yes" && newRoom.map!!.properties.get(stringSides[place]) == "No")) {
                            chooseMap(path, side, count, meshX, meshY, rooms)
                        }
                    }
                    if (i % 2 > 0) place++
                }
                place = 0
            }

        }
        // Else recursion -__- Try to load another room
        else chooseMap(path, side, count, meshX, meshY, rooms)

        return newRoom
    }

    fun generateRoom(path: String, side: String, previousMapLink: Int, currentMapLink: Int, count: Int, closestX: Int, closestY: Int,
                     placeX: Int, placeY: Int, linkX: Int, linkY: Int, rooms: ArrayList<Room>) {

        // Check that room have gate to create specific map
        if (rooms[count].map!!.properties.get(side) == "Yes") {
            // X and y positions on mesh. For big maps part of it
            val meshX = rooms[count].meshVertices[placeX]
            val meshY = rooms[count].meshVertices[placeY]

            // Check that mesh don't have another room with it's coordinates
            if (mesh[meshY + closestY][meshX + closestX] == 0) {
                if (zeroRoomChance < 500) zeroRoomChance += 10
                roomCounter++
                rooms.add(chooseMap(path, side, count, meshX, meshY, rooms))
                rooms[roomCounter].meshVertices = intArrayOf(meshX + meshCheckSides[0], meshY + meshCheckSides[1], meshX + meshCheckSides[6], meshY + meshCheckSides[7])
                rooms[roomCounter].roomLinks[currentMapLink] = count
                rooms[count].roomLinks[previousMapLink] = roomCounter

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
