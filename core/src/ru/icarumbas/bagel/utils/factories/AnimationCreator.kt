package ru.icarumbas.bagel.utils.factories

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.toGdxArray

object AnimationCreator {

    // Single sprites
    fun create(path: String,
               count: Int,
               animSpeed: Float,
               atlas: TextureAtlas,
               animPlaymode: Animation.PlayMode = Animation.PlayMode.LOOP): Animation<out TextureRegion> {


        return Animation(animSpeed, Array<TextureRegion>(count) { atlas.findRegion("$path ($it)") }.toGdxArray(), animPlaymode)

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
