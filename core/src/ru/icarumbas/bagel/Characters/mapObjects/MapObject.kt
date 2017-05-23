package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.Scenes.Hud


abstract class MapObject{

    abstract var destroyed: Boolean
    abstract var body: Body
    abstract var sprite: Sprite?
    abstract val bit: Short

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
        fixtureDef.filter.categoryBits = bit

        body = world.createBody(def)
        body.createFixture(fixtureDef)
        body.isActive = false
    }

    abstract fun loadSprite(textureAtlas: TextureAtlas)

    open fun draw(batch: Batch, delta: Float, hud: Hud, player: Player){
        if (!destroyed) sprite!!.draw(batch)

    }

}