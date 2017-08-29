package ru.icarumbas.bagel

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.PIX_PER_M


class Room {

    var path = ""
    var id = -1
    var width = 0f
    var height = 0f
    var passes = Array(8){-1}
    lateinit var meshCoords: IntArray

    constructor()

    constructor(assetManager: AssetManager, path: String, id: Int){
        this.path = path
        this.id = id
        width = assetManager.get(path, TiledMap::class.java).properties["Width"].toString().toFloat().div(PIX_PER_M)
        height = assetManager.get(path, TiledMap::class.java).properties["Height"].toString().toFloat().div(PIX_PER_M)
    }

}
