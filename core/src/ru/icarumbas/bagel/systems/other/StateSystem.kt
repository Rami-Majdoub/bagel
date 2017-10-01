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
    }

    private val state = Mappers.state
    private val damage = Mappers.damage
    private val run = Mappers.run
    private val weapon = Mappers.weapon
    private val body = Mappers.body
    private val rm: RoomManager
    private val ai = Mappers.AI
    private val teleport = Mappers.teleport


    constructor(rm: RoomManager) : super(Family.all(StateComponent::class.java).one(
            AlwaysRenderingMarkerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.rm = rm
    }


    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (state[e].states.contains(DEAD) && damage[e].HP <= 0) {
                if (state[e].currentState != DEAD) state[e].stateTime = 0f
                state[e].currentState = DEAD
            } else
                if (state[e].states.contains(DISAPPEARING) && teleport.has(e) && teleport[e].disappearing) {
                    if (state[e].currentState != DISAPPEARING) state[e].stateTime = 0f
                    state[e].currentState = DISAPPEARING
                } else
                    if (state[e].states.contains(APPEARING) &&
                            !ai[e].appeared &&
                            (ai[e].isPlayerNear || state[e].currentState == APPEARING || (teleport.has(e) && teleport[e].appearing))) {
                        if (state[e].currentState != APPEARING) state[e].stateTime = 0f
                        state[e].currentState = APPEARING
                    } else
                        if (state[e].states.contains(APPEARING) && !ai[e].appeared) {
                            state[e].currentState = NULL
                        } else
                            if (state[e].states.contains(JUMP_ATTACKING) && weapon[e].attacking &&
                                    (body[e].body.linearVelocity.y > .00001f ||
                                    body[e].body.linearVelocity.y < -.00001f)) {
                                if (state[e].currentState != JUMP_ATTACKING) state[e].stateTime = 0f
                                state[e].currentState = JUMP_ATTACKING
                            } else
                                if (state[e].states.contains(ATTACKING) && weapon[e].attacking) {
                                    if (state[e].currentState != ATTACKING) state[e].stateTime = 0f
                                    state[e].currentState = ATTACKING
                                } else
                                    if (state[e].states.contains(JUMPING) &&
                                            body[e].body.linearVelocity.y > .00001f ||
                                            body[e].body.linearVelocity.y < -.00001f) {
                                        if (state[e].currentState != JUMPING) state[e].stateTime = 0f
                                        state[e].currentState = JUMPING
                                    } else
                                        if (state[e].states.contains(RUNNING) &&
                                                MathUtils.round(body[e].body.linearVelocity.x) != 0 &&
                                                Math.abs(body[e].body.linearVelocity.x) > run[e].maxSpeed - .5f) {
                                            if (state[e].currentState != RUNNING) state[e].stateTime = 0f
                                            state[e].currentState = RUNNING
                                        } else
                                            if (state[e].states.contains(WALKING) &&
                                                    body[e].body.linearVelocity.x != 0f){
                                                if (state[e].currentState != WALKING) state[e].stateTime = 0f
                                                state[e].currentState = WALKING
                                            } else {
                                                    if (state[e].currentState != STANDING) state[e].stateTime = 0f
                                                    state[e].currentState = STANDING
                                                    }
        }
    }
}