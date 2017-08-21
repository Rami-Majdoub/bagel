package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.PIX_PER_M


class Room{

    var path: String = ""
    var id = -1

    val roomLinks = arrayOfNulls<Int>(8)
    lateinit var meshVertices: IntArray

    var mapWidth = 0f
    var mapHeight = 0f

    fun loadMap(path: String, assetManager: AssetManager, id: Int){
        this.path = path
        this.id = id
        mapWidth = assetManager.get(path, TiledMap::class.java).properties["Width"].toString().toFloat().div(PIX_PER_M)
        mapHeight = assetManager.get(path, TiledMap::class.java).properties["Height"].toString().toFloat().div(PIX_PER_M)
    }

    fun loadEntities(b2DWorldCreator: B2DWorldCreator, engine: Engine){

        b2DWorldCreator.loadMapObject(path, "boxes", engine, id)
        b2DWorldCreator.loadMapObject(path, "chandeliers", engine, id)
        b2DWorldCreator.loadMapObject(path, "chests", engine, id)
        b2DWorldCreator.loadMapObject(path, "statues", engine, id)
        b2DWorldCreator.loadMapObject(path, "spikeTraps", engine, id)
        b2DWorldCreator.loadMapObject(path, "spikes", engine, id)
        b2DWorldCreator.loadMapObject(path, "portalDoor", engine, id)
        b2DWorldCreator.loadMapObject(path, "chairs", engine, id)
        b2DWorldCreator.loadMapObject(path, "tables", engine, id)
    }

}