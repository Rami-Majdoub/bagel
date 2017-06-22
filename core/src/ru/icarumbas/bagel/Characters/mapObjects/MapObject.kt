package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Screens.GameScreen


abstract class MapObject {

    var body: Body? = null
    var sprite: Sprite? = null
    var posX = 0f
    var posY = 0f
    open val width = 64f.div(PIX_PER_M)
    open val height = 64f.div(PIX_PER_M)
    open val bit: Short = 1
    open val bodyType = BodyDef.BodyType.StaticBody
    var destroyed = false

    abstract val path: String

    @Suppress("Used for JSON Serialization")
    constructor()

    constructor(rectangle: Rectangle){
        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)
    }

    fun defineBody(world: World){
        val fixtureDef = FixtureDef()
        val def = BodyDef()
        val shape = PolygonShape()

        def.type = bodyType
        def.position.x = posX.plus(width.div(2))
        def.position.y = posY.plus(height.div(2))

        shape.setAsBox(width.div(2), height.div(2))
        fixtureDef.shape = shape
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = bit

        body = world.createBody(def)
        body!!.createFixture(fixtureDef)
        body!!.isActive = false
        body!!.isFixedRotation = true
    }

    fun loadSprite(textureAtlas: TextureAtlas) {
        sprite = textureAtlas.createSprite(path)
        sprite!!.setSize(width, height)
    }

    open fun draw(batch: Batch, delta: Float, gameScreen: GameScreen){
        if (!destroyed) {
            sprite?.setPosition(body!!.position.x.minus(width.div(2)), body!!.position.y.minus(height.div(2)))
            sprite?.draw(batch)
        }
    }

}