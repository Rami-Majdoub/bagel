package ru.icarumbas.bagel.screens.scenes

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor


/**
 * @author Mats Svensson
 */

class LoadingBar : Actor{

    private val animation: Animation<out TextureRegion>
    private val reg: TextureRegion
    private var stateTime: Float = 0f

    constructor(animation: Animation<out TextureRegion>) {
        this.animation = animation
        reg = animation.getKeyFrame(0f)
    }

    override fun act(delta: Float) {
        stateTime += delta
        reg.setRegion(animation.getKeyFrame(stateTime))
    }


    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(reg, x, y)
    }
}