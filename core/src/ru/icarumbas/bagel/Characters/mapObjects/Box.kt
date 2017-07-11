package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ru.icarumbas.BREAKABLE_BIT
import ru.icarumbas.bagel.Characters.Coin
import ru.icarumbas.bagel.Screens.GameScreen


class Box : BreakableMapObject {

    override val bodyType = BodyDef.BodyType.DynamicBody
    override lateinit var path: String
    override val bit = BREAKABLE_BIT

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){
        when (MathUtils.random(1)) {
            0 -> path = "box"
            1 -> path = "barrel"
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)
        if (!destroyed) onHit(gameScreen)
        if (coins.isNotEmpty()) coin!!.updateCoins(coins, batch)

    }

    override fun onHit(gameScreen: GameScreen) {
        if (canBeBroken && gameScreen.player.attacking) {

            when (MathUtils.random(1)) {
                0 -> gameScreen.game.assetManager["Sounds/crateBreak0.wav", Sound::class.java].play()
                1 -> gameScreen.game.assetManager["Sounds/crateBreak1.wav", Sound::class.java].play()
            }

            coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            coin!!.createCoins(body!!, gameScreen.world, coins, when (MathUtils.random(4)) {
                0 -> 1 // 1 coin
                else -> 0
            })

            super.onHit(gameScreen)

        }
    }


}