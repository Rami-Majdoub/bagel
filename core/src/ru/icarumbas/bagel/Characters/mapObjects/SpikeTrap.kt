package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.SPIKE_TRAP_BIT
import ru.icarumbas.bagel.Screens.GameScreen


class SpikeTrap : MapObject {

    override val bit: Short = SPIKE_TRAP_BIT
    override var destroyed = false
    lateinit override var body: Body
    override var sprite: Sprite? = null
    var posX = 0f
    var posY = 0f

    private var timer = 0f

    var isTouched = false

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) {
        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)
    }

    private fun checkHit(gameScreen: GameScreen){
        if (timer > .75 && isTouched) {
            gameScreen.player.hit(10, Vector2(0f, .1f))
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if ((isTouched || (timer > 0 && timer < 2.5f)) && !destroyed) {

            timer += delta
            if (timer > .75f) {
                sprite!!.draw(batch)
                checkHit(gameScreen)
            }

        }
        if (!isTouched && timer >= 2.5f) {
            timer = 0f
        }

    }

    override fun loadSprite(textureAtlas: TextureAtlas) {
        sprite = textureAtlas.createSprite("spikeTrap")
        sprite!!.setSize(64.div(PIX_PER_M), 64.div(PIX_PER_M))
        sprite!!.setPosition(posX, posY)
    }
}