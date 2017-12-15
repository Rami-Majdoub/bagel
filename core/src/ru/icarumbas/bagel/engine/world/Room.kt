package ru.icarumbas.bagel.engine.world

import ru.icarumbas.bagel.engine.resources.ResourceManager


class Room {

    var path = ""
    var id = -1
    var width = 0f
    var height = 0f
    var passes = Array(8){ -1 }
    lateinit var meshCoords: IntArray


    // Serialization
    constructor()

    constructor(path: String, id: Int, assets: ResourceManager){
        this.path = path
        this.id = id
        width = assets.getTiledMap(path).properties["Width"].toString().toFloat().div(PIX_PER_M)
        height = assets.getTiledMap(path).properties["Height"].toString().toFloat().div(PIX_PER_M)
    }
}
