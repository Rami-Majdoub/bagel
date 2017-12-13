package ru.icarumbas.bagel.engine.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.engine.components.other.StateComponent
import ru.icarumbas.bagel.engine.entities.EntityState
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*


class StateSystem(

        private val rm: RoomWorld

) : IteratingSystem(Family.all(StateComponent::class.java).get()) {

    private fun changeState(e: Entity, newState: EntityState){
        if (state[e].currentState != newState) state[e].stateTime = 0f
        state[e].currentState = newState
    }

    private fun isEntityJumping(e: Entity): Boolean{
        return body[e].body.linearVelocity.y > .00001f || body[e].body.linearVelocity.y < -.00001f
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            when {
                state[e].states.contains(EntityState.DEAD) && damage[e].HP <= 0 -> {
                    changeState(e, EntityState.DEAD)
                }

                state[e].states.contains(EntityState.DISAPPEARING) && teleport.has(e) && teleport[e].disappearing -> {
                    changeState(e, EntityState.DISAPPEARING)
                }

                state[e].states.contains(EntityState.APPEARING) &&
                        !AI[e].appeared &&
                        (AI[e].isTargetNear || state[e].currentState == EntityState.APPEARING || (teleport.has(e) && teleport[e].appearing)) -> {
                    changeState(e, EntityState.APPEARING)
                }

                state[e].states.contains(EntityState.APPEARING) && !AI[e].appeared -> {
                    changeState(e, EntityState.NOTHING)
                }

                state[e].states.contains(EntityState.JUMP_ATTACKING) && weapon[e].attacking && isEntityJumping(e) -> {
                    changeState(e, EntityState.JUMP_ATTACKING)
                }

                state[e].states.contains(EntityState.ATTACKING) && weapon[e].attacking -> {
                    changeState(e, EntityState.ATTACKING)
                }

                state[e].states.contains(EntityState.JUMPING) && isEntityJumping(e) -> {
                    changeState(e, EntityState.JUMPING)
                }

                state[e].states.contains(EntityState.RUNNING) &&
                        MathUtils.round(body[e].body.linearVelocity.x) != 0 &&
                        Math.abs(body[e].body.linearVelocity.x) > run[e].maxSpeed - .5f -> {
                    changeState(e, EntityState.RUNNING)
                }

                state[e].states.contains(EntityState.WALKING) && body[e].body.linearVelocity.x != 0f -> {
                    changeState(e, EntityState.WALKING)
                }

                state[e].states.contains(EntityState.OPENING) && open[e].opening -> {
                    changeState(e, EntityState.OPENING)
                }

                else -> {
                    changeState(e, EntityState.STANDING)
                }
            }
        }
    }
}