package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle


class Box : MapObject{

    override lateinit var path: String

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){

        when (MathUtils.random(1)) {
            0 -> path = "box"
            1 -> path = "barrel"
        }
    }
}