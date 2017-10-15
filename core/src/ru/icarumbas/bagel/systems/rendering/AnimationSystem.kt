package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.other.StateComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.Mappers.Mappers.animation
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.state
import ru.icarumbas.bagel.utils.Mappers.Mappers.texture
import ru.icarumbas.bagel.utils.Mappers.Mappers.weapon
import ru.icarumbas.bagel.utils.angleInDegrees
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight


class AnimationSystem : IteratingSystem {

    private val rm: RoomManager


    constructor(rm: RoomManager) : super(Family.all(
            AnimationComponent::class.java,
            StateComponent::class.java)
            .one(
            AlwaysRenderingMarkerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.rm = rm
    }

    private fun flip(e: Entity) {

        if (texture[e].tex != null) {
            val textureReg = texture[e].tex

            if (e.rotatedRight() && textureReg!!.isFlipX) {
                textureReg.flip(true, false)
            } else
                if (!e.rotatedRight() && !textureReg!!.isFlipX) {
                    textureReg.flip(true, false)
                }
        }
    }

    override fun processEntity(e: Entity, deltaTime: Float) {

        if (e.inView(rm)) {
            state[e].stateTime += deltaTime

            if (state[e].currentState == StateSystem.ATTACKING) {

                val frame = if (e.rotatedRight()){
                    body[weapon[e].entityRight].body.angleInDegrees().div(
                            MathUtils.PI / animation[e].animations[StateSystem.ATTACKING]!!.keyFrames.size).toInt() * -1
                } else {
                    body[weapon[e].entityLeft].body.angleInDegrees().div(
                            MathUtils.PI / animation[e].animations[StateSystem.ATTACKING]!!.keyFrames.size).toInt()
                }
                texture[e].tex = animation[e].animations[state[e].currentState]!!.keyFrames.get(frame)

            } else
                if (animation[e].animations.containsKey(state[e].currentState)) {
                    texture[e].tex =
                                    animation[e].
                                    animations[state[e].
                                    currentState]!!.
                                    getKeyFrame(state[e].stateTime)

                }

            flip(e)
        }
    }
}