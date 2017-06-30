package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import ru.icarumbas.bagel.Characters.Player


class WorldIO {

    val json = Json()
    val preferences = Gdx.app.getPreferences("Bagel preferences")!!

    init {
        json.setUsePrototypes(false)
    }

    fun writeRoomsToJson (path: String, arr: ArrayList<Room>, attach: Boolean) = Gdx.files.local(path).writeString(json.prettyPrint(arr), attach)

    fun loadRoomsFromJson(path: String) = json.fromJson(ArrayList<Room>().javaClass, Gdx.files.local(path))!!

    fun loadLastPlayerState(player: Player){
        player.playerBody.setTransform(preferences.getFloat("PlayerPositionX"), preferences.getFloat("PlayerPositionY"), 0f)
    }
}

