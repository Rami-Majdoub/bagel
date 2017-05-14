package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.bagel.Characters.mapObjects.MapObject
import ru.icarumbas.bagel.Utils.B2dWorldCreator.B2DWorldCreator


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
        if (assetManager.get(path, TiledMap::class.java).layers["boxes"] != null)
        b2DWorldCreator.loadBoxes(assetManager.get(path, TiledMap::class.java).layers["boxes"], mapObjects)

    }

    fun draw(batch: Batch) {
        mapObjects.forEach { it.draw(batch) }
    }

}