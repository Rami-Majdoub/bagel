package ru.icarumbas.bagel

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import ru.icarumbas.bagel.utils.Mappers.Mappers.body


class WorldIO {


    private val json = Json()
    val prefs = Gdx.app.getPreferences("game preferences")!!

    init {
        json.setUsePrototypes(false)
    }

    fun loadWorld(serializedObjects: ArrayList<SerializedMapObject>, rooms: ArrayList<Room>){
        serializedObjects.addAll(json.fromJson(ArrayList<SerializedMapObject>()::class.java, Gdx.files.local("items.json")))
        rooms.addAll(json.fromJson(ArrayList<Room>()::class.java, Gdx.files.local("rooms.json")))
    }

    fun loadMesh(): Array<IntArray> {
        return json.fromJson(Array<IntArray>::class.java, Gdx.files.local("mesh.json"))
    }

    fun loadVisibleRooms(): ArrayList<Int>{
        return json.fromJson(ArrayList<Int>()::class.java, Gdx.files.local("visibleRooms.json"))
    }

    fun saveWorld(serializedObjects: ArrayList<SerializedMapObject>,
                  rooms: ArrayList<Room>,
                  mesh: Array<IntArray>,
                  visibleRooms: ArrayList<Int>){

        Gdx.files.local("items.json").writeString(json.toJson(serializedObjects), false)
        Gdx.files.local("rooms.json").writeString(json.toJson(rooms), false)
        Gdx.files.local("mesh.json").writeString(json.toJson(mesh), false)
        Gdx.files.local("visibleRooms.json").writeString(json.toJson(visibleRooms), false)

    }

    fun saveCurrentState(playerEntity: Entity, currentMap: Int){
        prefs.putFloat("playerX", body[playerEntity].body.position.x)
        prefs.putFloat("playerY", body[playerEntity].body.position.y)
        prefs.putInteger("currentMap", currentMap)
        prefs.flush()
    }
}

