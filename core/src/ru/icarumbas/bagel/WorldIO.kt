package ru.icarumbas.bagel

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Json
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen


class WorldIO (val game: Bagel){

    private val json = Json()
    val preferences = Gdx.app.getPreferences("Bagel preferences")!!

    init {
        json.setUsePrototypes(false)
    }

    fun writeRoomsToJson(path: String, arr: ArrayList<Room>, attach: Boolean) = Gdx.files.local(path).writeString(json.prettyPrint(arr), attach)

    fun loadRoomsFromJson(path: String) = json.fromJson(ArrayList<Room>().javaClass, Gdx.files.local(path))!!

    fun loadLastState(mapRenderer: OrthogonalTiledMapRenderer, player: Player, rooms: ArrayList<Room>, gameScreen: GameScreen, currentMap: Int){

        player.playerBody.setTransform(preferences.getFloat("PlayerPositionX"), preferences.getFloat("PlayerPositionY"), 0f)

        rooms[currentMap].loadTileMap(gameScreen.worldCreator)
        rooms[currentMap].loadBodies(gameScreen.worldCreator, gameScreen, game)
        rooms[currentMap].setAllBodiesActivity(true)
        mapRenderer.map = rooms[currentMap].map

        rooms[gameScreen.currentMap].roomLinks.forEach {
            if (it != game.DEFAULT) {
                rooms[it].loadTileMap(gameScreen.worldCreator)
                rooms[it].loadBodies(gameScreen.worldCreator, gameScreen, game)
            }
        }

        gameScreen.animationCreator.createTileAnimation(currentMap, rooms)

    }
}

