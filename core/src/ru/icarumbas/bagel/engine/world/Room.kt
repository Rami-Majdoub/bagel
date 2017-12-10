package ru.icarumbas.bagel.engine.world

import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.engine.world.WorldConstants.PIX_PER_M


class Room {

    var path = ""
    var id = -1
    var width = 0f
    var height = 0f
    var passes = Array(8){ -1 }
    lateinit var meshCoords: IntArray


    // Serialization
    constructor()

    constructor(path: String, id: Int){
        this.path = path
        this.id = id
        width = ResourceManager.getTiledMap(path).properties["Width"].toString().toFloat().div(PIX_PER_M)
        height = ResourceManager.getTiledMap(path).properties["Height"].toString().toFloat().div(PIX_PER_M)
    }
}
