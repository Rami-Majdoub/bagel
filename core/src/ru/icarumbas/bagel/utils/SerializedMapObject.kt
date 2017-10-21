package ru.icarumbas.bagel.utils

import com.badlogic.gdx.math.Rectangle

class SerializedMapObject {

    var roomId = -1
    var rect = Rectangle()
    var objectPath = ""
    var rand = 1
    var appeared = false

    constructor()

    constructor(roomId: Int, rect: Rectangle, objectPath: String, rand: Int = 1) {
        this.roomId = roomId
        this.rect = rect
        this.objectPath = objectPath
        this.rand = rand
    }
}
