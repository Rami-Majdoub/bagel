package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World


interface MapObject{

    fun defineBody(rect: Rectangle, world: World)

    fun update(dt: Float)

}