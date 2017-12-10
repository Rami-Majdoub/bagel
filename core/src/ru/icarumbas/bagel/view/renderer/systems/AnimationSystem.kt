package ru.icarumbas.bagel.view.renderer.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.engine.components.other.RoomIdComponent
import ru.icarumbas.bagel.engine.components.other.StateComponent
import ru.icarumbas.bagel.engine.components.physics.StaticComponent
import ru.icarumbas.bagel.engine.systems.other.StateSystem
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight
import ru.icarumbas.bagel.view.renderer.components.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.view.renderer.components.AnimationComponent


class AnimationSystem : IteratingSystem {

    private val rm: RoomWorld


    constructor(rm: RoomWorld) : super(Family.all(
            AnimationComponent::class.java,
            StateComponent::class.java)
            .one(
            AlwaysRenderingMarkerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.rm = rm
    }

    private fun flip(e: Entity) {

        texture[e].tex?.let {
            if (e.rotatedRight() && it.isFlipX) {
                it.flip(true, false)
            } else
                if (!e.rotatedRight() && !it.isFlipX) {
                    it.flip(true, false)
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