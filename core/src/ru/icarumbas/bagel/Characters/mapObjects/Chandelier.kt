package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ru.icarumbas.BREAKABLE_BIT
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Coin
import ru.icarumbas.bagel.Screens.GameScreen


class Chandelier: MapObject, Breakable {

    lateinit var coin: Coin
    override val coins = ArrayList<Body>()
    override var canBeBroken = false

    override lateinit var path: String
    override val bit = BREAKABLE_BIT
    override val width = 127f.div(PIX_PER_M)
    override val height = 192f.div(PIX_PER_M)


    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle){

        when (MathUtils.random(1)) {
            0 -> path = "goldenChandelier"
            1 -> path = "silverChandelier"
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)

        if (!destroyed) onHit(gameScreen)
        if (coins.isNotEmpty()) coin.updateCoins(coins, batch)
    }


    override fun onHit(gameScreen: GameScreen) {
        if (canBeBroken && body!!.type != BodyDef.BodyType.DynamicBody && gameScreen.player.attacking) {

            body!!.type = BodyDef.BodyType.DynamicBody
            sprite!!.setAlpha(.25f)
            gameScreen.game.assetManager["Sounds/shatterMetal.wav", Sound::class.java].play()

            coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            coin.createCoins(body!!, gameScreen.world, coins, when (path) {
                "goldenChandelier" -> MathUtils.random(0, 4)
                "silverChandelier" -> MathUtils.random(0, 2)
                else -> throw Exception("Unknown path")
            })

        }
        if (body!!.linearVelocity.y < -3f) {
            gameScreen.worldContactListener.deleteList.add(body!!)
            destroyed = true
            body = null
            sprite = null
        }
    }
}