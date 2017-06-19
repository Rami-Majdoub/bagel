package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.CHEST_BIT
import ru.icarumbas.bagel.Characters.Coin
import ru.icarumbas.bagel.Screens.GameScreen


class Chest: MapObject{

    override val bit: Short = CHEST_BIT
    override lateinit var path: String

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

        if (isOpened) open(gameScreen.world)

        coins.forEach {
            (it.userData as Sprite).setPosition(
                    it.position.x - (it.userData as Sprite).width.div(2),
                    it.position.y - (it.userData as Sprite).height.div(2)
            )
            (it.userData as Sprite).draw(batch)
        }

    }

    fun open(world: World){
        isOpened = false
        destroyed = true

        val coin = Coin()
        coin.createCoins(body!!, world, coins, count = when(path){
            "goldenChest" -> 30
            "silverChest" -> 20
            "bronzeChest" -> 10
            else -> 10
        })

        world.destroyBody(body)
        body = null
        sprite = null

    }


}