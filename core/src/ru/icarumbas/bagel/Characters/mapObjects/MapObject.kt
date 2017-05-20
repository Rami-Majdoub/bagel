package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.BOX_BIT


abstract class MapObject{

    abstract var destroyed: Boolean
    abstract var body: Body
    abstract var sprite: Sprite?

    fun defineBody(world: World){
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

    abstract fun loadSprite(textureAtlas: TextureAtlas)

    fun draw(batch: Batch){
        if (!destroyed) sprite!!.draw(batch)

    }

}