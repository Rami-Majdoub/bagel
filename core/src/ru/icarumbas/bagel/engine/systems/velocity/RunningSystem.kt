package ru.icarumbas.bagel.engine.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.engine.components.velocity.RunComponent
import ru.icarumbas.bagel.engine.controller.PlayerController
import ru.icarumbas.bagel.engine.systems.other.StateSystem
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.inView


class RunningSystem : IteratingSystem {


    private val playerController: PlayerController
    private val rm: RoomWorld

    constructor(playerController: PlayerController, rm: RoomWorld) : super(Family.all(RunComponent::class.java).get()) {
        this.playerController = playerController
        this.rm = rm
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (Math.abs(body[e].body.linearVelocity.x) <= run[e].maxSpeed) {

                if (player.has(e) && (!player[e].collidingWithGround || player[e].standindOnGround) &&
                        state[e].currentState != StateSystem.DEAD) {
                    if (playerController.isRightPressed()) {
                        applyImpulse(body[e].body, run[e].acceleration, 0f)
                        player[e].lastRight = true
                    }
                    if (playerController.isLeftPressed()) {
                        applyImpulse(body[e].body, -run[e].acceleration, 0f)
                        player[e].lastRight = false
                    }
                }
                if (
                AI.has(e) &&
                AI[e].appeared &&
                !AI[e].isTargetNear &&
                state[e].currentState != StateSystem.ATTACKING &&
                state[e].currentState != StateSystem.DEAD &&
                !AI[e].isTargetEqualX) {

                    if (AI[e].isTargetRight)
                        applyImpulse(body[e].body, run[e].acceleration, 0f)
                    else
                        applyImpulse(body[e].body, -run[e].acceleration, 0f)
                }

            }
        }
    }

    private fun applyImpulse(body: Body, speedX: Float, speedY: Float) =
            body.applyLinearImpulse(Vector2(speedX, speedY), body.worldCenter, true)
}
