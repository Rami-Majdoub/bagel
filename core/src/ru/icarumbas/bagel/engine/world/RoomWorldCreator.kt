package ru.icarumbas.bagel.engine.world

import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.engine.resources.ResourceManager


class RoomWorldCreator(

        private val worldSize: Int,
        private val assets: ResourceManager

) {

    private lateinit var newRoom: Room

    private var meshCheckSides = IntArray(8)

    private val stringSides = arrayOf("Left", "Up", "Down", "Right")

    private var roomsTotal = 0
    private var mainRoomId = 0

    private var totalNotClosedGates = 0
    private var endingRoomChance = 0
    private val increaseEndingRoomChanceThreshold = 25

    private val rooms = ArrayList<Room>()

    val mesh: Array<IntArray> = Array(worldSize) { IntArray(worldSize) }.apply {
        map { column ->
            column.fill(0)
        }

        get(worldSize / 2)[worldSize / 2] = 1
    }

    private data class SideArguments(

            val sideName: String,
            val previousPassLink: Int,
            val stepX: Int,
            val stepY: Int,
            val countMeshX: Int,
            val countMeshY: Int,
            val meshClosestX: Int,
            val meshClosestY: Int,
            val meshClosestX2: Int,
            val meshClosestY2: Int
    )

    fun createWorld(): ArrayList<Room>{

        fun isRoomBigOnHeight(mainRoomId: Int) = rooms[mainRoomId].height != REG_ROOM_HEIGHT

        fun isRoomBigOnWidth(mainRoomId: Int) = rooms[mainRoomId].width != REG_ROOM_WIDTH

        fun getRootRoom() =
            Room("Maps/Map9.tmx", 0, assets).apply {
                meshCoords = intArrayOf(worldSize / 2, worldSize / 2, worldSize / 2, worldSize / 2)
            }

        rooms.add(getRootRoom())

        totalNotClosedGates = getRoomGatesCount(rooms.first())

        while (totalNotClosedGates != 0) {

            stringSides.forEach {
                createRoom(getSideArguments(it))
            }

            if (isRoomBigOnHeight(mainRoomId)) {
                createRoom(getSideArguments("Right", isBig = true))
                createRoom(getSideArguments("Left", isBig = true))
            }
            if (isRoomBigOnWidth(mainRoomId)) {
                createRoom(getSideArguments("Up", isBig = true))
                createRoom(getSideArguments("Down", isBig = true))
            }

            mainRoomId++

            if (isTimeToIncreaseRoomWithoutExitChance()) endingRoomChance = 75
        }

        return rooms.apply {
            trimToSize()
        }
    }

    private fun getSideArguments(side: String, isBig: Boolean = false): SideArguments {
        return when (side) {
            "Right" -> {
                if (isBig) SideArguments("Right", 6, 1, 0, 2, 3, 0, 3, 0, 1)
                else SideArguments("Right", 2, 1, 0, 2, 1, 0, 1, 0, 3)
            }

            "Left" -> {
                if (isBig) SideArguments("Left", 4, -1, 0, 0, 3, 2, 3, 2, 1)
                else SideArguments("Left", 0, -1, 0, 0, 1, 2, 1, 2, 3)
            }

            "Up" -> {
                if (isBig) SideArguments("Up", 5, 0, -1, 2, 3, 2, 1, 0, 1)
                else SideArguments("Up", 1, 0, -1, 0, 3, 0, 1, 2, 1)
            }

            "Down" -> {
                if (isBig) SideArguments("Down", 7, 0, 1, 2, 1, 2, 3, 0, 3)
                else SideArguments("Down", 3, 0, 1, 0, 1, 0, 3, 2, 3)
            }

            else -> throw IllegalArgumentException("cant getSideArguments for $side")
        }
    }

    private fun isTimeToIncreaseRoomWithoutExitChance(): Boolean {
        return totalNotClosedGates > increaseEndingRoomChanceThreshold
    }

    private fun createRoom(sideArgs: SideArguments) {

        fun isRoomHasGateForSide() = assets.getTiledMap(rooms[mainRoomId].path).properties.get(sideArgs.sideName) == "Yes"

        if (isRoomHasGateForSide()) {

            // X and y positions on mesh. For big maps part of it
            val meshX = rooms[mainRoomId].meshCoords[sideArgs.countMeshX]
            val meshY = rooms[mainRoomId].meshCoords[sideArgs.countMeshY]

            fun addNewRoom(){
                rooms.add(chooseMap(sideArgs.sideName, meshX, meshY).apply {
                    meshCoords = intArrayOf(
                            meshX + meshCheckSides[0],
                            meshY + meshCheckSides[1],
                            meshX + meshCheckSides[6],
                            meshY + meshCheckSides[7])
                })
            }

            fun addGateToMainRoom(){
                rooms[mainRoomId].passes[sideArgs.previousPassLink] = roomsTotal
            }


            fun fillMeshOnNewRoomCoordinates(){
                for (i in 0..7 step 2) {
                    mesh[meshY + meshCheckSides[i + 1]][meshX + meshCheckSides[i]] = 1
                }
            }

            fun addJointToCollidingRoom() {

                fun isRoomOnCoordinates(room: Room, x: Int, y: Int) =
                        (x == room.meshCoords[sideArgs.meshClosestX] && y == room.meshCoords[sideArgs.meshClosestY]) ||
                                (x == room.meshCoords[sideArgs.meshClosestX2] && y == room.meshCoords[sideArgs.meshClosestY2])

                fun addLinkToMainRoom(room: Room) {
                    rooms[mainRoomId].passes[sideArgs.previousPassLink] = rooms.indexOf(room)
                }

                for (room in rooms) {
                    if (isRoomOnCoordinates(room,meshX + sideArgs.stepX, meshY + sideArgs.stepY)) {
                        addLinkToMainRoom(room)
                        totalNotClosedGates -= 1
                        return
                    }
                }
            }

            fun isMeshHasAtLeastOneSpace() = mesh[meshY + sideArgs.stepY][meshX + sideArgs.stepX] == 0

            if (isMeshHasAtLeastOneSpace()) {
                roomsTotal++
                addNewRoom()
                totalNotClosedGates += getRoomGatesCount(rooms.last()) - 1
//                updateCounters()
                addGateToMainRoom()
                fillMeshOnNewRoomCoordinates()

            } else {
                addJointToCollidingRoom()
            }
        }
    }

    private fun chooseMap(side: String, meshX: Int, meshY: Int): Room {

        newRoom = Room("Maps/Map${getNextRoomId(side)}.tmx", roomsTotal, assets)

        val meshRoomWidth = (newRoom.width / REG_ROOM_WIDTH).toInt()
        val meshRoomHeight = (newRoom.height / REG_ROOM_HEIGHT).toInt()

        meshCheckSides = getFitCheckValues(side, meshRoomWidth, meshRoomHeight)

        if (isMeshHasSpace(meshCheckSides, meshX, meshY)) {
            if (!isRoomJointsUnite(newRoom, getSideCheckValues(side, meshRoomWidth, meshRoomHeight), meshX, meshY)) {
                chooseMap(side, meshX, meshY)
            }
        } else {
            chooseMap(side, meshX, meshY)
        }

        return newRoom
    }

    private fun isRoomJointsUnite(newRoom: Room, checkSides: IntArray, meshX: Int, meshY: Int): Boolean {

        fun isRoomOnCoordinateExists(i: Int, room: Room) =
                ((meshX.plus(checkSides[i]) == room.meshCoords[0] || meshX.plus(checkSides[i]) == room.meshCoords[2]) &&
                        (meshY.plus(checkSides[i + 8]) == room.meshCoords[1] || meshY.plus(checkSides[i + 8]) == room.meshCoords[3]))


        fun isSidesConflict(index: Int, room: Room) =
            (assets.getTiledMap(room.path).properties[stringSides[3 - index]] == "Yes"
                    && assets.getTiledMap(newRoom.path).properties[stringSides[index]] != "Yes") ||
            (assets.getTiledMap(room.path).properties[stringSides[3 - index]] != "Yes" &&
                    assets.getTiledMap(newRoom.path).properties[stringSides[index]] == "Yes")


        var index = 0

        rooms.forEach {
            for (i in 0..7) {
                if (isRoomOnCoordinateExists(i, it)) {
                    if (isSidesConflict(index, it)) return false
                }
                if (i % 2 > 0) index++
            }
            index = 0
        }


        return true
    }

    private fun getSideCheckValues(side: String, meshRoomWidth: Int, meshRoomHeight: Int): IntArray {
        return when (side) {

            "Left" -> intArrayOf(-meshRoomWidth - 1, -meshRoomWidth - 1, -meshRoomWidth, -1, -meshRoomWidth, -1, 0, 0, // Xss
                    0, -meshRoomHeight + 1, -meshRoomHeight, -meshRoomHeight, 1, 1, 0, -meshRoomHeight + 1) // Yss

            "Up" -> intArrayOf(-1, -1, 0, meshRoomWidth - 1, 0, meshRoomWidth - 1, meshRoomWidth, meshRoomWidth,
                    -1, -meshRoomHeight, -meshRoomHeight - 1, -meshRoomHeight - 1, 0, 0, -1, -meshRoomHeight)

            "Right" -> intArrayOf(0, 0, 1, meshRoomWidth, meshRoomWidth, 1, meshRoomWidth + 1, meshRoomWidth + 1,
                    0, -meshRoomHeight + 1, -meshRoomHeight, -meshRoomHeight, 1, 1, -meshRoomHeight + 1, 0)

            "Down" -> intArrayOf(-1, -1, 0, meshRoomWidth - 1, 0, meshRoomWidth - 1, meshRoomWidth, meshRoomWidth,
                    1, meshRoomHeight, 0, 0, meshRoomHeight + 1, meshRoomHeight + 1, 1, meshRoomHeight)

            else -> throw IllegalArgumentException("Unknown side: $side")
        }
    }

    private fun getFitCheckValues(side: String, meshRoomWidth: Int, meshRoomHeight: Int): IntArray {
        return when (side) {

            "Left" -> intArrayOf(-meshRoomWidth, 0, -meshRoomWidth, -meshRoomHeight + 1, -1, 0, -1, -meshRoomHeight + 1)

            "Up" -> intArrayOf(0, -1, meshRoomWidth - 1, -1, 0, -meshRoomHeight, meshRoomWidth - 1, -meshRoomHeight)

            "Right" -> intArrayOf(1, 0, meshRoomWidth, 0, 1, -meshRoomHeight + 1, meshRoomWidth, -meshRoomHeight + 1)

            "Down" -> intArrayOf(0, meshRoomHeight, 0, 1, meshRoomWidth - 1, meshRoomHeight, meshRoomWidth - 1, 1)

            else -> throw IllegalArgumentException("Unknown side: $side")
        }
    }

    private fun getRoomGatesCount(room: Room): Int{
        var gatesCount = 0

        assets.getTiledMap(room.path).properties.keys.forEach {
            if (it[0].isUpperCase() && it != "Height" && it != "Width") {
                gatesCount += when {
                    (room.width != REG_ROOM_WIDTH && (it == "Up" || it == "Down")) ||
                        (room.height != REG_ROOM_HEIGHT && (it == "Right" || it == "Left")) -> 2
                    else -> 1
                }
            }
        }

        return gatesCount
    }

    private fun isMeshHasSpace(fitCheckValues: IntArray, meshX: Int, meshY: Int): Boolean {

        return (mesh[meshY + fitCheckValues[1]][meshX + fitCheckValues[0]] == 0) &&
                (mesh[meshY + fitCheckValues[3]][meshX + fitCheckValues[2]] == 0) &&
                (mesh[meshY + fitCheckValues[5]][meshX + fitCheckValues[4]] == 0) &&
                (mesh[meshY + fitCheckValues[7]][meshX + fitCheckValues[6]] == 0)
    }

    private fun getNextRoomId(side: String): Int {

        fun isEndingRoomChanced() = MathUtils.random(100) < endingRoomChance

        fun isRoomPathEqualsNearest(mainRoomId: Int, randomRoomId: Int) = rooms[mainRoomId].path == "Maps/Map$randomRoomId.tmx"

        fun getRandomRoomIdForSide() =
                if (isEndingRoomChanced())
                    when (side) {
                        "Left" -> 13
                        "Right" -> 16
                        "Up" -> 15
                        else -> 14
                    }
                else
                    MathUtils.random(0, MAPS_TOTAL - 1)

        val randomRoomId = getRandomRoomIdForSide()

        return if (isRoomPathEqualsNearest(mainRoomId, randomRoomId))
            getNextRoomId(side)
        else
            randomRoomId
    }
}
