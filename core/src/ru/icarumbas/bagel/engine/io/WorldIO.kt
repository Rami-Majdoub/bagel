package ru.icarumbas.bagel.engine.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import ru.icarumbas.bagel.engine.world.Room


class WorldIO {

    private val json = Json()
    private val prefs = Gdx.app.getPreferences("Bagel prefs")


    private fun saveToFile(path: String, obj: Any){
        Gdx.files.local(path).writeString(json.prettyPrint(obj), false)
    }

    private fun <T> loadFromFile(path: String, clazz: Class<T>): T {
        return json.fromJson(clazz, Gdx.files.local(path))
    }

    fun saveInfo(info: IOInfo) {
        when (info) {
            is WorldInfo -> {
                saveToFile("rooms.json", info.rooms)
                saveToFile("mesh.json", info.mesh)
            }

            is PlayerInfo -> {
                with (prefs) {
                    putFloat("playerX", info.position.first)
                    putFloat("playerY", info.position.second)
                    putInteger("currentMapId", info.currentMap)
                    flush()
                }
            }

            is RoomInfo -> {
                with (prefs) {
                    putBoolean("canContinue", info.canContinue)
                    flush()
                }
            }

            is MinimapInfo -> {
                saveToFile("openedRooms", info.openedRooms)
            }

            is EntitiesInfo -> {
                saveToFile("items.json", info.mapObjects)
            }
        }
    }

    fun loadWorldInfo() =
            WorldInfo(
                rooms = loadFromFile("rooms.json", ArrayList<Room>()::class.java),
                mesh = loadFromFile("mesh.json", Array<IntArray>::class.java)
            )

    fun loadPlayerInfo() =
            PlayerInfo(
                    Pair(prefs.getFloat("playerX"), prefs.getFloat("playerY")),
                    prefs.getInteger("currentMapId")
            )

    fun loadRoomInfo() =
            RoomInfo(
                    prefs.getBoolean("canContinue")
            )

    fun loadMinimapInfo() =
            MinimapInfo(
                    openedRooms = loadFromFile("openedRooms", ArrayList<Int>()::class.java)
            )

    fun loadEntitiesInfo() =
            EntitiesInfo(
                    mapObjects = loadFromFile("items.json", ArrayList<SerializedMapObject>()::class.java)
            )
}



