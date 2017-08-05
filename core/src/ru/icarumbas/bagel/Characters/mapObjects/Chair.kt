package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.Rectangle


class Chair : Box {



    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){
        path = "chair"
    }
}