package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Screens.GameScreen


class SpikeTrap : Spike {

    override val path = "spikeTrap"
    override val height = 64f.div(PIX_PER_M)
    private var timer = 0f
    private var soundPlayed = false

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun checkHit(gameScreen: GameScreen){
        if (timer > .75 && isTouched) {
            gameScreen.player.hit(10, Vector2(0f, .1f))
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if ((isTouched || (timer > 0 && timer < 2.5f))) {

            timer += delta

            if (timer > .75f) {
                if (!soundPlayed){
                    gameScreen.game.assetManager["Sounds/spikes.wav", Sound::class.java].play()
                    soundPlayed = true
                }
                super.draw(batch, delta, gameScreen)
                checkHit(gameScreen)
            }

        }
        if (!isTouched && timer >= 2.5f) {
            soundPlayed = false
            timer = 0f
        }

    }
}