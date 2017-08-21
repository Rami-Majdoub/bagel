package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.other.StateComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class StateSwapSystem : IteratingSystem {

    companion object AllStates {
        val RUNNING = "RUNNING"
        val JUMPING = "JUMPING"
        val STANDING = "STANDING"
        val DEAD = "DEAD"
        val ATTACKING = "ATTACKING"
    }

    private val state = Mappers.state
    private val gs: GameScreen


    constructor(gs: GameScreen) : super(Family.all(StateComponent::class.java).one(
            PlayerComponent::class.java,
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {
        this.gs = gs
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(gs.currentMapId, gs.rooms)) {
            if (state[entity].states.contains(DEAD) && Mappers.params[entity].HP <= 0) {
                if (state[entity].currentState != DEAD) state[entity].stateTime = 0f
                state[entity].currentState = DEAD
            } else
                if (state[entity].states.contains(ATTACKING) && Mappers.weapon[entity].attacking) {
                    if (state[entity].currentState != ATTACKING) state[entity].stateTime = 0f
                    state[entity].currentState = ATTACKING
                } else
                    if (state[entity].states.contains(JUMPING) &&
                            Mappers.body[entity].body.linearVelocity.y > .00001f ||
                            Mappers.body[entity].body.linearVelocity.y < -.00001f) {
                        if (state[entity].currentState != JUMPING) state[entity].stateTime = 0f
                        state[entity].currentState = JUMPING
                    } else
                        if (state[entity].states.contains(RUNNING) &&
                                MathUtils.round(Mappers.body[entity].body.linearVelocity.x) != 0) {
                            if (state[entity].currentState != RUNNING) state[entity].stateTime = 0f
                            state[entity].currentState = RUNNING
                        } else {
                            if (state[entity].currentState != STANDING) state[entity].stateTime = 0f
                            state[entity].currentState = STANDING
                        }
        }
    }
}