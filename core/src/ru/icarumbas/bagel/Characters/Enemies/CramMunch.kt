package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.ENEMY_BIT
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator


class CramMunch: Enemy {
    override var sprite: Sprite? = null
    override val bit: Short = ENEMY_BIT
    override var destroyed = false
    override lateinit var body: Body
    override var posX = 0f
    override var posY = 0f

    override var stateTimer = 0f
    override lateinit var stateAnimation: Animation<*>
    override lateinit var runAnimation: Animation<*>


    constructor()

    constructor(rectangle: Rectangle) {
        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)
    }

    override fun loadSprite(textureAtlas: TextureAtlas, animationCreator: AnimationCreator) {
        sprite = Sprite(textureAtlas.findRegion("idle"), 0, 0, 147, 114)
        stateAnimation = animationCreator.createSpriteAnimation("idle", 6, .25f, Animation.PlayMode.LOOP, textureAtlas, 147, 114)
    }

}