package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.BOX_BIT
import ru.icarumbas.PIX_PER_M


class Box : MapObject{
    private var destroyed = false
    lateinit override var body: Body
    override var sprite: Sprite? = null
    var path = ""
    var posX = 0f
    var posY = 0f

    constructor()

    constructor(rectangle: Rectangle) {

        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)

        val random = MathUtils.random(1)
        if (random == 0) {
            path = "box"
        }
        else {
            path = "barrel"
        }

    }

    override fun loadSprite(textureAtlas: TextureAtlas) {
        sprite = textureAtlas.createSprite(path)
        sprite!!.setSize(64.div(PIX_PER_M), 64.div(PIX_PER_M))
        sprite!!.setPosition(posX, posY)
    }

    override fun draw(batch: Batch) {
        if (!destroyed) sprite!!.draw(batch)
    }

    override fun defineBody(world: World) {

        val fixtureDef = FixtureDef()
        val def = BodyDef()
        val shape = PolygonShape()

        def.position.x = sprite!!.x.plus(sprite!!.width.div(2))
        def.position.y = sprite!!.y.plus(sprite!!.height.div(2))
        def.type = BodyDef.BodyType.StaticBody

        shape.setAsBox(sprite!!.width.div(2), sprite!!.height.div(2))
        fixtureDef.shape = shape
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = BOX_BIT

        body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.isActive = false
    }

}