package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.Screens.GameScreen


interface Breakable {

    var canBeBroken: Boolean
    val coins: ArrayList<Body>

    fun onHit(gameScreen: GameScreen)
}