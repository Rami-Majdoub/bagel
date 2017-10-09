package ru.icarumbas.bagel.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers


class RunningSystem : IteratingSystem {

    private val body = Mappers.body
    private val run = Mappers.run
    private val pl = Mappers.player
    private val ai = Mappers.AI
    private val state = Mappers.state
    private val hud: Hud

    constructor(hud: Hud) : super(Family.all(RunComponent::class.java).get()) {
        this.hud = hud
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (Math.abs(body[e].body.linearVelocity.x) <= run[e].maxSpeed) {
            if (pl.has(e)) {
                if (hud.isRightPressed()) {
                    applyImpulse(body[e].body, run[e].acceleration, 0f)
                    pl[e].lastRight = true
                }
                if (hud.isLeftPressed()) {
                    applyImpulse(body[e].body, -run[e].acceleration, 0f)
                    pl[e].lastRight = false
                }
            }
            if (ai.has(e) && ai[e].appeared && !ai[e].isPlayerNear && state[e].currentState != StateSystem.ATTACKING){
                if (ai[e].isPlayerRight)
                    applyImpulse(body[e].body, run[e].acceleration, 0f)
                else
                    applyImpulse(body[e].body, -run[e].acceleration, 0f)
            }

        }
    }

    private fun applyImpulse(body: Body, speedX: Float, speedY: Float) =
            body.applyLinearImpulse(Vector2(speedX, speedY), body.worldCenter, true)
}
