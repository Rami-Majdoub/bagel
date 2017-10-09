package ru.icarumbas.bagel.creators

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

class AnimationCreator{

    // Single sprites
    fun create(path: String,
               count: Int,
               animSpeed: Float,
               atlas: TextureAtlas,
               animPlaymode: Animation.PlayMode = Animation.PlayMode.LOOP): Animation<TextureRegion> {


        val frames = Array<TextureRegion>(count)

        (1..count).forEach {
            frames.add(atlas.findRegion("$path ($it)"))
        }

        val animation = Animation(animSpeed, frames)
        animation.playMode = animPlaymode
        frames.clear()
        return animation
    }

    /*// Sprite sheet
    fun create(path: String, count: Int, animSpeed: Float, animPlaymode: Animation.PlayMode, atlas: TextureAtlas,
                              width: Int, height: Int): Animation<*> {
        val frames = Array<Sprite>(count)
        (0..count).forEach {
            val sprite = Sprite(atlas.findRegion(path), it * width, 0, width, height)
            sprite.setSize(width.div(PIX_PER_M), height.div(PIX_PER_M))
            frames.add(sprite)
        }
        val animation = Animation(animSpeed, frames)
        animation.playMode = animPlaymode
        frames.clear()
        return animation
    }*/
}
