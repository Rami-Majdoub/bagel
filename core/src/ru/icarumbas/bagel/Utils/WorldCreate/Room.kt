package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.bagel.Characters.Enemies.Enemy
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Characters.mapObjects.MapObject
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Utils.B2dWorld.B2DWorldCreator


class Room {

    var path: String = ""

    val roomLinks = arrayOfNulls<Int>(8)
    lateinit var meshVertices: IntArray

    var mapObjects = ArrayList<MapObject>()
    var enemies = ArrayList<Enemy>()

    var mapWidth = 0f
    var mapHeight = 0f

    fun loadMap(path: String, assetManager: AssetManager){
        this.path = path
        mapWidth = assetManager.get(path, TiledMap::class.java).properties["Width"].toString().toFloat()
        mapHeight = assetManager.get(path, TiledMap::class.java).properties["Height"].toString().toFloat()
    }

    fun loadMapObjects(b2DWorldCreator: B2DWorldCreator, assetManager: AssetManager){

        b2DWorldCreator.loadMapObject(path, "boxes", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "chandeliers", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "chests", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "statues", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "spikeTraps", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "spikes", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "portalDoor", assetManager, mapObjects)

        //b2DWorldCreator.loadCramMunch(assetManager.get(path, TiledMap::class.java).layers["enemies"], enemies)

    }

    fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        mapObjects.forEach { it.draw(batch, delta, gameScreen) }
        enemies.forEach { it.draw(batch, delta, gameScreen) }
    }

}