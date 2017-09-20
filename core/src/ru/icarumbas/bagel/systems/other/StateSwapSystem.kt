package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.other.StateComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class StateSwapSystem : IteratingSystem {

    companion object AllStates {
        val RUNNING = "RUNNING"
        val JUMPING = "JUMPING"
        val STANDING = "STANDING"
        val DEAD = "DEAD"
        val ATTACKING = "ATTACKING"
        val WALKING = "WALKING"
        val JUMP_ATTACKING = "JUMP-ATTACKING"
        val APPEARING = "APPEARING"
    }

    private val state = Mappers.state
    private val damage = Mappers.damage
    private val run = Mappers.run
    private val attack = Mappers.attack
    private val body = Mappers.body
    private val rm: RoomManager


    constructor(rm: RoomManager) : super(Family.all(StateComponent::class.java).one(
            AlwaysRenderingMarkerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.rm = rm
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            if (state[entity].states.contains(DEAD) && damage[entity].HP <= 0) {
                if (state[entity].currentState != DEAD) state[entity].stateTime = 0f
                state[entity].currentState = DEAD
            } else
                if (state[entity].states.contains(JUMP_ATTACKING) && attack[entity].attacking &&
                        (body[entity].body.linearVelocity.y > .00001f ||
                        body[entity].body.linearVelocity.y < -.00001f)
                        ) {
                    if (state[entity].currentState != JUMP_ATTACKING) state[entity].stateTime = 0f
                    state[entity].currentState = JUMP_ATTACKING
                } else
                    if (state[entity].states.contains(ATTACKING) && attack[entity].attacking) {
                        if (state[entity].currentState != ATTACKING) state[entity].stateTime = 0f
                        state[entity].currentState = ATTACKING
                    } else
                        if (state[entity].states.contains(JUMPING) &&
                                body[entity].body.linearVelocity.y > .00001f ||
                                body[entity].body.linearVelocity.y < -.00001f) {
                            if (state[entity].currentState != JUMPING) state[entity].stateTime = 0f
                            state[entity].currentState = JUMPING
                        } else
                            if (state[entity].states.contains(RUNNING) &&
                                    MathUtils.round(body[entity].body.linearVelocity.x) != 0 &&
                                    Math.abs(body[entity].body.linearVelocity.x) > run[entity].maxSpeed - .5f) {
                                if (state[entity].currentState != RUNNING) state[entity].stateTime = 0f
                                state[entity].currentState = RUNNING
                            } else
                                if (state[entity].states.contains(WALKING) &&
                                        MathUtils.round(body[entity].body.linearVelocity.x) != 0){
                                    if (state[entity].currentState != WALKING) state[entity].stateTime = 0f
                                    state[entity].currentState = WALKING
                                } else {
                                        if (state[entity].currentState != STANDING) state[entity].stateTime = 0f
                                        state[entity].currentState = STANDING
                                        }
        }
    }
}