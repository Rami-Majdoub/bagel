package ru.icarumbas.bagel.engine.entities.factories

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.toGdxArray

class AnimationFactory {

    // Single sprites
    fun create(path: String,
               count: Int,
               animSpeed: Float,
               atlas: TextureAtlas,
               animPlaymode: Animation.PlayMode = Animation.PlayMode.LOOP): Animation<out TextureRegion> {


        return Animation(animSpeed, Array<TextureRegion>(count) {
            atlas.findRegion("$path (${it + 1})")
        }.toGdxArray(), animPlaymode)

    }
}
