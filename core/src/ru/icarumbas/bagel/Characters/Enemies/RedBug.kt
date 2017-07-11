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

    override fun loadAnimation(textureAtlas: TextureAtlas, animationCreator: AnimationCreator) {
        runAnimation = Animation(.1f, textureAtlas.findRegions("redBug"), Animation.PlayMode.LOOP)
        sprite = Sprite()

    }


}