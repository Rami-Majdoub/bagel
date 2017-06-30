package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator


class RedBug : FlyingEnemy {


    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun loadSprite(textureAtlas: TextureAtlas, animationCreator: AnimationCreator) {
        stateAnimation = animationCreator.createSpriteAnimation("redBug", 2, .1f, Animation.PlayMode.LOOP, textureAtlas)
        sprite = Sprite(stateAnimation!!.keyFrames.first() as TextureRegion)
        sprite!!.setSize(width, height)
    }


}