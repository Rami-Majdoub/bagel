package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.Rectangle
import ru.icarumbas.PIX_PER_M


class Table : Box {

    override val width = 128f.div(PIX_PER_M)

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){
        path = "table"
    }
}