package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.Characters.Coin
import ru.icarumbas.bagel.Screens.GameScreen


open class BreakableMapObject : MapObject {

    var canBeBroken = false
    val coins = ArrayList<Body>()
    var coin : Coin? = null

    constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)
        onHit(gameScreen)
        if (coins.isNotEmpty()) coin!!.updateCoins(coins, batch)
    }

    open fun onHit(gameScreen: GameScreen) {
        gameScreen.worldContactListener.deleteList.add(body!!)
        destroyed = true
        sprite = null
        body = null
    }
}