package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import ru.icarumbas.PIX_PER_M


class Chandelier: MapObject{

    override lateinit var path: String
    override val width = 127f.div(PIX_PER_M)
    override val height = 192f.div(PIX_PER_M)


    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){

        when (MathUtils.random(1)) {
            0 -> path = "goldenChandelier"
            1 -> path = "silverChandelier"
        }
    }
}