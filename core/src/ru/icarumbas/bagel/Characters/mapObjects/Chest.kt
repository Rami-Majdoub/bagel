package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import ru.icarumbas.bagel.Screens.GameScreen
import kotlin.experimental.or


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
        if (isOpened) {
            open(gameScreen.world)
            isOpened = false
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
        val coin = Coin()
        coin.createCoins(this, world, count = when(path){
            "goldenChest" -> 30
            "silverChest" -> 20
            "bronzeChest" -> 10
            else -> 10
        })
    }

    private class Coin {

        val coinSprite = Sprite(Texture("Coin.png"))
        val coinBdef = BodyDef()
        val coinShape = PolygonShape()
        val coinFixDef = FixtureDef()


        fun createCoins(chest: Chest, world: World, count: Int) {

            coinSprite.setSize(16.div(PIX_PER_M), 16.div(PIX_PER_M))

            coinBdef.type = BodyDef.BodyType.DynamicBody
            coinBdef.position.set(chest.body.position.x, chest.body.position.y)

            coinShape.setAsBox(coinSprite.width.div(2), coinSprite.height.div(2))

            coinFixDef.shape = coinShape
            coinFixDef.restitution = .25f
            coinFixDef.filter.categoryBits = COIN_BIT
            coinFixDef.filter.maskBits = PLATFORM_BIT or PLAYER_BIT or GROUND_BIT

            (0..count-1).forEach {
                chest.coins.add(world.createBody(coinBdef))
                chest.coins[chest.coins.size-1].createFixture(coinFixDef)
                chest.coins[chest.coins.size-1].userData = coinSprite
                chest.coins[chest.coins.size-1].applyLinearImpulse (
                        Vector2(MathUtils.random(-3f, 3f),
                        MathUtils.random(10f)),
                        Vector2(0f, 0f), true
                )
            }

        }
    }

}