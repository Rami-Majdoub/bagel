package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.CHEST_BIT
import ru.icarumbas.bagel.Characters.Coin
import ru.icarumbas.bagel.Screens.GameScreen


class Chest: MapObject{

    override val bit: Short = CHEST_BIT
    override lateinit var path: String

    lateinit var coin: Coin
    val coins = ArrayList<Body>()
    var isOpened = false

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle): super(rectangle){

        when (MathUtils.random(2)) {
            0 -> path = "goldenChest"
            1 -> path = "silverChest"
            2 -> path = "bronzeChest"
        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)

        if (isOpened && gameScreen.hud.openButtonPressed) open(gameScreen)
        if (coins.isNotEmpty()) coin.updateCoins(coins, batch)
    }

    fun open(gameScreen: GameScreen){
        isOpened = false
        destroyed = true

        gameScreen.game.assetManager["Sounds/openchest.wav", Sound::class.java].play()

        coin = Coin(gameScreen.game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
        coin.createCoins(body!!, gameScreen.world, coins, count = when(path){
            "goldenChest" -> 30
            "silverChest" -> 20
            "bronzeChest" -> 10
            else -> throw Exception("Unknown path")
        })

        gameScreen.worldContactListener.deleteList.add(body!!)
        body = null
        sprite = null

    }


}