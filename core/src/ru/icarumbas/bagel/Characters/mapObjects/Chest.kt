package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.CHEST_BIT
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Screens.GameScreen


class Chest: MapObject{

    override val bit: Short = CHEST_BIT
    override var destroyed = false
    lateinit override var body: Body
    override var sprite: Sprite? = null
    var path = ""
    var posX = 0f
    var posY = 0f

    val coins = ArrayList<Body>()
    var isOpened = false

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) {

        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)

        when (MathUtils.random(2)) {
            0 -> path = "goldenChest"
            1 -> path = "silverChest"
            2 -> path = "bronzeChest"

        }
    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        if (isOpened && gameScreen.player.attacking) {
            open(gameScreen.world)
        }
        coins.forEach {
            (it.userData as Sprite).setPosition(
                    it.position.x - (it.userData as Sprite).width.div(2),
                    it.position.y - (it.userData as Sprite).height.div(2)
            )
            (it.userData as Sprite).draw(batch)
        }

        super.draw(batch, delta, gameScreen)
    }

    override fun loadSprite(textureAtlas: TextureAtlas) {
        sprite = textureAtlas.createSprite(path)
        sprite!!.setSize(64.div(PIX_PER_M), 64.div(PIX_PER_M))
        sprite!!.setPosition(posX, posY)
    }

    fun open(world: World){
        world.destroyBody(body)

        destroyed = true
        isOpened = false

        val coin = Coin()
        coin.createCoins(body, world, coins, count = when(path){
            "goldenChest" -> 30
            "silverChest" -> 20
            "bronzeChest" -> 10
            else -> 10
        })
    }


}