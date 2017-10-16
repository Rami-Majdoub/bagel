package ru.icarumbas.bagel.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.player
import ru.icarumbas.bagel.utils.Mappers.Mappers.run
import ru.icarumbas.bagel.utils.Mappers.Mappers.state


class RunningSystem : IteratingSystem {


    private val hud: Hud

    constructor(hud: Hud) : super(Family.all(RunComponent::class.java).get()) {
        this.hud = hud
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (Math.abs(body[e].body.linearVelocity.x) <= run[e].maxSpeed) {
            if (player.has(e) && (!player[e].collidingWithGround || player[e].standindOnGround)) {
                if (hud.isRightPressed()) {
                    applyImpulse(body[e].body, run[e].acceleration, 0f)
                    player[e].lastRight = true
                }
                if (hud.isLeftPressed()) {
                    applyImpulse(body[e].body, -run[e].acceleration, 0f)
                    player[e].lastRight = false
                }
            }
            if (AI.has(e) &&
                AI[e].appeared &&
                !AI[e].isTargetNear &&
                state[e].currentState != StateSystem.ATTACKING &&
                state[e].currentState != StateSystem.DEAD &&
                !AI[e].isTargetEqualX){

                if (AI[e].isTargetRight)
                    applyImpulse(body[e].body, run[e].acceleration, 0f)
                else
                    applyImpulse(body[e].body, -run[e].acceleration, 0f)
            }

        }
    }

    private fun applyImpulse(body: Body, speedX: Float, speedY: Float) =
            body.applyLinearImpulse(Vector2(speedX, speedY), body.worldCenter, true)
}
