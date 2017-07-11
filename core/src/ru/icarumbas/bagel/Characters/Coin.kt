package ru.icarumbas.bagel.Characters

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import kotlin.experimental.or

class Coin {

    lateinit var coinSprite: Sprite
    val coinShape = PolygonShape()

    val coinFixDef = FixtureDef()
    val coinBdef = BodyDef()

    constructor()

    constructor(textureAtlas: TextureAtlas) {
        coinSprite = textureAtlas.createSprite("Coin")!!
        coinSprite.setSize(16.div(PIX_PER_M), 16.div(PIX_PER_M))
        coinBdef.type = BodyDef.BodyType.DynamicBody
        coinFixDef.shape = coinShape
        coinFixDef.restitution = .25f
        coinFixDef.filter.categoryBits = COIN_BIT
        coinFixDef.filter.maskBits = PLATFORM_BIT or PLAYER_BIT or GROUND_BIT

    }

    fun createCoins(mainBody: Body, world: World, arr: ArrayList<Body>, count: Int) {

        if (count != 0) {
            coinBdef.position.set(mainBody.position.x, mainBody.position.y)
            coinShape.setAsBox(coinSprite.width.div(2), coinSprite.height.div(2))

            (0..count - 1).forEach {
                arr.add(world.createBody(coinBdef))
                arr[arr.size - 1].createFixture(coinFixDef)
                arr[arr.size - 1].userData = coinSprite
                arr[arr.size - 1].applyLinearImpulse(
                        Vector2(MathUtils.random(-3f, 3f),
                                MathUtils.random(10f)),
                        arr[arr.size - 1].localPoint2, true
                )
            }

        }

    }

    fun updateCoins(coins: ArrayList<Body>, batch: Batch) {
        coins.forEach {
            (it.userData as Sprite).setPosition(
                    it.position.x - (it.userData as Sprite).width.div(2),
                    it.position.y - (it.userData as Sprite).height.div(2)
            )
            (it.userData as Sprite).draw(batch)
        }
    }
}
