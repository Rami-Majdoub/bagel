package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.BOX_BIT
import ru.icarumbas.PIX_PER_M


class Box : MapObject, Sprite{
    private var destroyed = false
    lateinit var body: Body

    constructor(world: World, rectangle: Rectangle, textureAtlas: TextureAtlas) {
        defineBody(rectangle, world)

        val random = MathUtils.random(1)
        if (random == 0)
        set(textureAtlas.createSprite("box"))
        else
        set(textureAtlas.createSprite("barrel"))

        setSize(64.div(PIX_PER_M), 64.div(PIX_PER_M))
        setPosition(body.position.x.minus(width.div(2)), body.position.y.minus(width.div(2)))


    }

    override fun draw(batch: Batch) {
        if (!destroyed) super.draw(batch)
    }

    override fun update(dt: Float) {

    }

    override fun defineBody(rect: Rectangle, world: World) {

        val fixtureDef = FixtureDef()
        val def = BodyDef()
        val shape = PolygonShape()

        def.position.x = (rect.x.plus(rect.width.div(2))).div(PIX_PER_M)
        def.position.y = (rect.y.plus(rect.height.div(2))).div(PIX_PER_M)
        def.type = BodyDef.BodyType.StaticBody

        shape.setAsBox(rect.width.div(2f).div(PIX_PER_M), rect.height.div(2f).div(PIX_PER_M))
        fixtureDef.shape = shape
        fixtureDef.friction = 1f
        fixtureDef.filter.categoryBits = BOX_BIT

        body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.isActive = false
    }

}