package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.SPIKES_BIT
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Screens.Scenes.Hud


class Spikes: MapObject {
    override val bit: Short = SPIKES_BIT
    override var destroyed = false
    lateinit override var body: Body
    override var sprite: Sprite? = null
    var posX = 0f
    var posY = 0f

    private var timer = 0f
    private var hitTimer = 0f
    private var firstHit = false

    var isTouched = false

    constructor()

    constructor(rectangle: Rectangle) {

        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)


    }

    private fun checkHit(delta: Float, gameScreen: GameScreen){
        if (timer > .75 && isTouched) {

            hitTimer += delta

            if ((hitTimer > .5 || !firstHit) && gameScreen.player.getState(gameScreen.hud) != GameScreen.State.Dead) {
                gameScreen.player.color = Color.RED
                gameScreen.player.HP -= 5
                hitTimer = 0f
            }
        }

        if (hitTimer > .1 || !isTouched) gameScreen.player.color = gameScreen.player.defaultColor

    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if ((isTouched || (timer > 0 && timer < 2.5f)) && !destroyed) {

            timer += delta
            if (timer > .75f) {
                sprite!!.draw(batch)
                checkHit(delta, gameScreen)
                if (!firstHit && isTouched) gameScreen.player.playerBody.applyLinearImpulse(Vector2(0f, .05f), gameScreen.player.playerBody.worldCenter, true)
                firstHit = true
            }

        }
        if (!isTouched && timer >= 2.5f) {
            timer = 0f
            firstHit = false
        }

    }

    override fun loadSprite(textureAtlas: TextureAtlas) {
        sprite = textureAtlas.createSprite("spikes")
        sprite!!.setSize(64.div(PIX_PER_M), 64.div(PIX_PER_M))
        sprite!!.setPosition(posX, posY)
    }
}