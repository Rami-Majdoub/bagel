package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Characters.mapObjects.MapObject
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Utils.B2dWorld.B2DWorldCreator


class Room {

    var path: String = ""

    val roomLinks = arrayOfNulls<Int>(8)
    lateinit var meshVertices: IntArray

    var mapObjects = ArrayList<MapObject>()

    var mapWidth = 0f
    var mapHeight = 0f

    fun loadMap(path: String, assetManager: AssetManager){
        this.path = path
        mapWidth = assetManager.get(path, TiledMap::class.java).properties["Width"].toString().toFloat()
        mapHeight = assetManager.get(path, TiledMap::class.java).properties["Height"].toString().toFloat()
    }

    fun loadMapObjects(b2DWorldCreator: B2DWorldCreator, assetManager: AssetManager){
        b2DWorldCreator.loadBoxes(assetManager.get(path, TiledMap::class.java).layers["boxes"], mapObjects)
        b2DWorldCreator.loadChandeliers(assetManager.get(path, TiledMap::class.java).layers["chandeliers"], mapObjects)
        b2DWorldCreator.loadChests(assetManager.get(path, TiledMap::class.java).layers["chests"], mapObjects)
        b2DWorldCreator.loadStatues(assetManager.get(path, TiledMap::class.java).layers["statue"], mapObjects)
        b2DWorldCreator.loadSpikes(assetManager.get(path, TiledMap::class.java).layers["spikes"], mapObjects)
    }

    fun draw(batch: Batch, delta: Float, hud: Hud, player: Player) {
        mapObjects.forEach { it.draw(batch, delta, hud, player) }
    }

}