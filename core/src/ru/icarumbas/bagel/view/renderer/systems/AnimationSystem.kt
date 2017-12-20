package ru.icarumbas.bagel.view.renderer.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.engine.components.other.RoomIdComponent
import ru.icarumbas.bagel.engine.components.other.StateComponent
import ru.icarumbas.bagel.engine.components.physics.StaticComponent
import ru.icarumbas.bagel.engine.entities.EntityState
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*
import ru.icarumbas.bagel.view.renderer.components.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.view.renderer.components.AnimationComponent


class AnimationSystem(

        private val rm: RoomWorld

) : IteratingSystem(Family.all(AnimationComponent::class.java, StateComponent::class.java)
                .one(AlwaysRenderingMarkerComponent::class.java, RoomIdComponent::class.java, StaticComponent::class.java).get()) {



    private fun flip(e: Entity) {

        texture[e].tex.apply {
            if (e.rotatedRight() && isFlipX) {
                flip(true, false)
            } else
                if (!e.rotatedRight() && !isFlipX) {
                    flip(true, false)
                }
        }
    }

    override fun processEntity(e: Entity, deltaTime: Float) {

        if (e.inView(rm)) {
            state[e].stateTime += deltaTime

            if (state[e].currentState == EntityState.ATTACKING) {

                val frame = if (e.rotatedRight()){
                    body[weapon[e].entityRight].body.angleInDegrees().div(
                            MathUtils.PI / animation[e].animations[EntityState.ATTACKING]!!.keyFrames.size).toInt() * -1
                } else {
                    body[weapon[e].entityLeft].body.angleInDegrees().div(
                            MathUtils.PI / animation[e].animations[EntityState.ATTACKING]!!.keyFrames.size).toInt()
                }

                texture[e].tex.setRegion(animation[e].animations[state[e].currentState]!!.keyFrames.get(frame))

            } else
                if (animation[e].animations.containsKey(state[e].currentState)) {

                    texture[e].tex.setRegion(
                                    animation[e].
                                    animations[state[e].
                                    currentState]!!.
                                    getKeyFrame(state[e].stateTime))

                }

            flip(e)
        }
    }
}