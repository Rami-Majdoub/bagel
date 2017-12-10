package ru.icarumbas.bagel.engine.io

import com.badlogic.gdx.math.Rectangle

data class SerializedMapObject(

        var roomId: Int = -1,
        var rect: Rectangle = Rectangle(),
        var objectPath: String = "",
        var rand: Int = 1,
        var appeared: Boolean = false
)
