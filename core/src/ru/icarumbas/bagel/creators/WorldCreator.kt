package ru.icarumbas.bagel.creators

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.TILED_MAPS_TOTAL
import ru.icarumbas.bagel.Room
import ru.icarumbas.bagel.RoomManager

class WorldCreator (private val assetManager: AssetManager){

    private var zeroRoomChance = 0
    lateinit var mesh: Array<IntArray>
    private val stringSides = arrayOf("Left", "Up", "Down", "Right")
    private lateinit var newRoom: Room
    private var roomsTotal = 0
    private var meshCheckSides = IntArray(8)
    private var angularRoomChance = 0
    private var totalNotClosedGates = 0

    fun createWorld(worldSize: Int, rm: RoomManager){
        mesh = Array(worldSize) { IntArray(worldSize) }

        mesh.forEach { it.fill(0) }

        // Set center of the mesh
        mesh[worldSize / 2][worldSize / 2] = 1

        totalNotClosedGates = countRoomGates(rm.rooms.first().path, rm.rooms.first())
        println(countRoomGates(rm.rooms.first().path, rm.rooms.first()))

        var i = 0

        // Try to create map for each map[i] side
        while (totalNotClosedGates != 0) {
            if (i == rm.size()) break

            generateRoom("Right", 2, i, 1, 0, 2, 1, 0, 1, 0, 3, rm)
            generateRoom("Left", 0, i, -1, 0, 0, 1, 2, 1, 2, 3, rm)
            generateRoom("Up", 1, i, 0, -1, 0, 3, 0, 1, 2, 1, rm)
            generateRoom( "Down", 3, i, 0, 1, 0, 1, 0, 3, 2, 3, rm)


            if (rm.height(i) != REG_ROOM_HEIGHT) {
                generateRoom("Right", 6, i, 1, 0, 2, 3, 0, 3, 0, 1, rm)
                generateRoom( "Left", 4, i, -1, 0, 0, 3, 2, 3, 2, 1, rm)
            }
            if (rm.width(i) != REG_ROOM_WIDTH) {
                generateRoom("Up", 5, i, 0, -1, 2, 3, 2, 1, 0, 1, rm)
                generateRoom( "Down", 7, i, 0, 1, 2, 1, 2, 3, 0, 3, rm)
            }

            i++

            if (totalNotClosedGates > 25) angularRoomChance = 75

        }


    }

    private fun countRoomGates(roomPath: String, room: Room): Int{
        var gates = 0

        assetManager[roomPath, TiledMap::class.java].properties.keys.forEach {
            if (it[0].isUpperCase() && it != "Height" && it != "Width") {
                gates += when {
                    room.width != REG_ROOM_WIDTH && (it == "Up" || it == "Down") -> 2
                    room.height != REG_ROOM_HEIGHT && (it == "Right" || it == "Left") -> 2
                    else -> 1
                }
            }
        }

        return gates
    }

    private fun rand(values: Int, rm: RoomManager, count: Int, side: String): Int {

        val r = if (MathUtils.random(100) > angularRoomChance) {
            MathUtils.random(0, values)
        } else {
            when (side) {
                "Left" -> 13
                "Right" -> 16
                "Up" -> 15
                else -> 14
            }
        }

        return if (rm.rooms[count].path != "Maps/Map$r.tmx")
            r
        else {
            rand(values, rm, count, side)
        }
    }

   /* private fun drawMesh(count: Int){
        println("Count: $count, Size: $roomsTotal")

        mesh.forEach {
            x -> x.forEach { y ->
            if (y == 1) print("* ") else print("  ")
        }
            println()
        }

        println(totalNotClosedGates)


    }*/

    private fun checkFit(side: String, meshX: Int, meshY: Int, mapRoomWidth: Int, mapRoomHeight: Int): Boolean {
        meshCheckSides = when (side) {

            "Left" -> intArrayOf(-mapRoomWidth, 0, -mapRoomWidth, -mapRoomHeight + 1, -1, 0, -1, -mapRoomHeight + 1)
            "Up" -> intArrayOf(0, -1, mapRoomWidth -1, -1, 0, -mapRoomHeight, mapRoomWidth -1, -mapRoomHeight)
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

    private fun chooseMap(side: String, count: Int, meshX: Int, meshY: Int, rm: RoomManager): Room {

        newRoom = rm.createRoom(assetManager, "Maps/Map${rand(TILED_MAPS_TOTAL - 1, rm, count, side)}.tmx", roomsTotal)

        val mapRoomWidth = (newRoom.width / REG_ROOM_WIDTH).toInt()
        val mapRoomHeight = (newRoom.height / REG_ROOM_HEIGHT).toInt()

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
            rm.rooms.forEach {
                for (i in 0..7) {
                    if ((meshX.plus(checkSides[i]) == it.meshCoords[0] || meshX.plus(checkSides[i]) == it.meshCoords[2]) &&
                            (meshY.plus(checkSides[i + 8]) == it.meshCoords[1] || meshY.plus(checkSides[i + 8]) == it.meshCoords[3])) {

                        if ((assetManager.get(it.path, TiledMap::class.java).properties.get(stringSides[3 - place]) != "Yes"
                          && assetManager.get(newRoom.path, TiledMap::class.java).properties.get(stringSides[place]) == "Yes") ||
                            (assetManager.get(it.path, TiledMap::class.java).properties.get(stringSides[3 - place]) == "Yes"
                          && assetManager.get(newRoom.path, TiledMap::class.java).properties.get(stringSides[place]) != "Yes")) {
                            chooseMap(side, count, meshX, meshY, rm)
                        }

                    }
                    if (i % 2 > 0) place++
                }
                place = 0
            }

        } else {
            chooseMap(side, count, meshX, meshY, rm)
        }

        return newRoom
    }

    private fun generateRoom(
                             sideName: String,
                             previousPassLink: Int,
                             count: Int,
                             stepX: Int,
                             stepY: Int,
                             countMeshX: Int,
                             countMeshY: Int,
                             meshClosestX: Int,
                             meshClosestY: Int,
                             meshClosestX2: Int,
                             meshClosestY2: Int,
                             rm: RoomManager)
    {

        // Check that room have gate to create specific map
        if (assetManager.get(rm.path(count), TiledMap::class.java).properties.get(sideName) == "Yes") {
            // X and y positions on mesh. For big maps part of it
            val meshX = rm.rooms[count].meshCoords[countMeshX]
            val meshY = rm.rooms[count].meshCoords[countMeshY]


            // Check that mesh don't have another room with it's coordinates
            if (mesh[meshY + stepY][meshX + stepX] == 0) {
                if (zeroRoomChance < 750) zeroRoomChance += 15
                roomsTotal++
                rm.rooms.add(chooseMap(sideName, count, meshX, meshY, rm))
                totalNotClosedGates += countRoomGates(rm.rooms.last().path, rm.rooms.last())

                rm.rooms[roomsTotal].meshCoords = intArrayOf(meshX + meshCheckSides[0], meshY + meshCheckSides[1], meshX + meshCheckSides[6], meshY + meshCheckSides[7])

                rm.rooms[count].passes[previousPassLink] = roomsTotal
                totalNotClosedGates -= 1


                // Fill mesh on new room's coordinates
                for (i in 0..7 step 2) mesh[meshY + meshCheckSides[i + 1]][meshX + meshCheckSides[i]] = 1

//                drawMesh(count)
            }

            // Else add link
            else {
                // Search in mapLinks room with collided coordinates
                for (it in rm.rooms) {
                    if (meshX + stepX == it.meshCoords[meshClosestX] && meshY + stepY == it.meshCoords[meshClosestY] ||
                        meshX + stepX == it.meshCoords[meshClosestX2] && meshY + stepY == it.meshCoords[meshClosestY2]) {
                        // Add link to main room
                        rm.rooms[count].passes[previousPassLink] = rm.rooms.indexOf(it)
                        totalNotClosedGates -= 1
                        return
                    }
                }
            }

        }
    }


}
