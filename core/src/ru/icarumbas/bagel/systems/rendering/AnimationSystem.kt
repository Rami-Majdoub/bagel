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
import ru.icarumbas.bagel.utils.angleInDegrees
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight


class AnimationSystem : IteratingSystem {

    private val anim = Mappers.animation
    private val state = Mappers.state
    private val weapon = Mappers.weapon
    private val body = Mappers.body

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

        if (body[e].body.userData != null) {
            val textureReg = body[e].body.userData as TextureRegion

            if (e.rotatedRight() && textureReg.isFlipX) {
                textureReg.flip(true, false)
            } else
                if (!e.rotatedRight() && !textureReg.isFlipX) {
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
                            MathUtils.PI / anim[e].animations[StateSystem.ATTACKING]!!.keyFrames.size).toInt() * -1
                } else {
                    body[weapon[e].entityLeft].body.angleInDegrees().div(
                            MathUtils.PI / anim[e].animations[StateSystem.ATTACKING]!!.keyFrames.size).toInt()
                }
                body[e].body.userData = anim[e].animations[state[e].currentState]!!.keyFrames.get(frame)

            } else
                if (anim[e].animations.containsKey(state[e].currentState)) {
                    body[e].body.userData =
                                    anim[e].
                                    animations[state[e].
                                    currentState]!!.
                                    getKeyFrame(state[e].stateTime)

                }

            flip(e)
        }
    }
}