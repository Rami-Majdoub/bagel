package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import ru.icarumbas.PIX_PER_M


class Statue: MapObject {

    override lateinit var path: String
    override val height = 128f.div(PIX_PER_M)

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){

        when (MathUtils.random(1)) {
            0 -> path = "goldenStatue"
            1 -> path = "silverStatue"
        }
    }
}