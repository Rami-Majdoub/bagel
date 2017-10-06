package ru.icarumbas.bagel

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import ru.icarumbas.bagel.utils.SerializedMapObject


class WorldIO {


    private val json = Json()
//    val pref = Gdx.app.getPreferences("game preferences")!!

    init {
        json.setUsePrototypes(false)
    }

    fun loadWorld(serializedObjects: ArrayList<SerializedMapObject>, rooms: ArrayList<Room>){
        serializedObjects.addAll(json.fromJson(ArrayList<SerializedMapObject>().javaClass, Gdx.files.local("items.json")))
        rooms.addAll(json.fromJson(ArrayList<Room>().javaClass, Gdx.files.local("rooms.json")))
    }

    fun saveWorld(serializedObjects: ArrayList<SerializedMapObject>, rooms: ArrayList<Room>){
        Gdx.files.local("items.json").writeString(json.toJson(serializedObjects), false)
        Gdx.files.local("rooms.json").writeString(json.toJson(rooms), false)
    }
}

