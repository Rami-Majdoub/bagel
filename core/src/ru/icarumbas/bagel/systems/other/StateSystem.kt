package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.other.StateComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.damage
import ru.icarumbas.bagel.utils.Mappers.Mappers.open
import ru.icarumbas.bagel.utils.Mappers.Mappers.run
import ru.icarumbas.bagel.utils.Mappers.Mappers.state
import ru.icarumbas.bagel.utils.Mappers.Mappers.teleport
import ru.icarumbas.bagel.utils.Mappers.Mappers.weapon
import ru.icarumbas.bagel.utils.inView


class StateSystem : IteratingSystem {

    companion object AllStates {
        val RUNNING = "RUNNING"
        val JUMPING = "JUMPING"
        val STANDING = "STANDING"
        val DEAD = "DEAD"
        val ATTACKING = "ATTACKING"
        val WALKING = "WALKING"
        val JUMP_ATTACKING = "JUMP-ATTACKING"
        val APPEARING = "APPEARING"
        val NULL = "NULL"
        val DISAPPEARING = "DISAPPEAR"
        val OPENING = "OPENING"
    }

    private val rm: RoomManager


    constructor(rm: RoomManager) : super(Family.all(StateComponent::class.java).one(
            AlwaysRenderingMarkerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.rm = rm
    }


    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            when {
                state[e].states.contains(DEAD) && damage[e].HP <= 0 -> {
                    if (state[e].currentState != DEAD) state[e].stateTime = 0f
                    state[e].currentState = DEAD
                }
                state[e].states.contains(DISAPPEARING) && teleport.has(e) && teleport[e].disappearing -> {
                    if (state[e].currentState != DISAPPEARING) state[e].stateTime = 0f
                    state[e].currentState = DISAPPEARING
                }
                state[e].states.contains(APPEARING) &&
                        !AI[e].appeared &&
                        (AI[e].isTargetNear || state[e].currentState == APPEARING || (teleport.has(e) && teleport[e].appearing)) -> {
                    if (state[e].currentState != APPEARING) state[e].stateTime = 0f
                    state[e].currentState = APPEARING
                }
                state[e].states.contains(APPEARING) && !AI[e].appeared -> state[e].currentState = NULL
                state[e].states.contains(JUMP_ATTACKING) && weapon[e].attacking &&
                        (body[e].body.linearVelocity.y > .00001f ||
                                body[e].body.linearVelocity.y < -.00001f) -> {
                    if (state[e].currentState != JUMP_ATTACKING) state[e].stateTime = 0f
                    state[e].currentState = JUMP_ATTACKING
                }
                state[e].states.contains(ATTACKING) && weapon[e].attacking -> {
                    if (state[e].currentState != ATTACKING) state[e].stateTime = 0f
                    state[e].currentState = ATTACKING
                }
                state[e].states.contains(JUMPING) &&
                        body[e].body.linearVelocity.y > .00001f || body[e].body.linearVelocity.y < -.00001f -> {
                    if (state[e].currentState != JUMPING) state[e].stateTime = 0f
                    state[e].currentState = JUMPING
                }
                state[e].states.contains(RUNNING) &&
                        MathUtils.round(body[e].body.linearVelocity.x) != 0 &&
                        Math.abs(body[e].body.linearVelocity.x) > run[e].maxSpeed - .5f -> {
                    if (state[e].currentState != RUNNING) state[e].stateTime = 0f
                    state[e].currentState = RUNNING
                }
                state[e].states.contains(WALKING) &&
                        body[e].body.linearVelocity.x != 0f -> {
                    if (state[e].currentState != WALKING) state[e].stateTime = 0f
                    state[e].currentState = WALKING
                }
                state[e].states.contains(OPENING) && open[e].opening -> {
                    if (state[e].currentState != OPENING) state[e].stateTime = 0f
                    state[e].currentState = OPENING
                }
                else -> {
                    if (state[e].currentState != STANDING) state[e].stateTime = 0f
                    state[e].currentState = STANDING
                }
            }
        }
    }
}