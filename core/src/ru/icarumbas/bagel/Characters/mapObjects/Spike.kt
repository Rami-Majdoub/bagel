package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.SPIKE_BIT
import ru.icarumbas.bagel.Screens.GameScreen


open class Spike: MapObject{

    override val bit: Short = SPIKE_BIT
    override val path = "spike"
    override val height = 109f.div(PIX_PER_M)

    var isTouched = false

    @Suppress("Used for JSON Serialization")
    constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    open fun checkHit(gameScreen: GameScreen){
        if (isTouched) {
            gameScreen.player.hit(15, Vector2(0f, .05f))
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)
        checkHit(gameScreen)
    }
}