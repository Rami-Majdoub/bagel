package ru.icarumbas.bagel.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.components.velocity.RunComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers


class RunningSystem : IteratingSystem {

    private val body = Mappers.body
    private val run = Mappers.run
    private val pl = Mappers.player
    private val ai = Mappers.AI
    private val hud: Hud

    constructor(hud: Hud) : super(Family.all(RunComponent::class.java).get()) {
        this.hud = hud
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (Math.abs(body[entity].body.linearVelocity.x) <= run[entity].maxSpeed) {
            if (pl.has(entity)) {
                if (hud.isRightPressed()) {
                    applyImpulse(body[entity].body, run[entity].acceleration, 0f)
                    pl[entity].lastRight = true
                }
                if (hud.isLeftPressed()) {
                    applyImpulse(body[entity].body, -run[entity].acceleration, 0f)
                    pl[entity].lastRight = false
                }
            }
            if (ai.has(entity) && ai[entity].appeared && !ai[entity].isPlayerNear && ai[entity].coldown > 1){
                if (ai[entity].isPlayerRight)
                    applyImpulse(body[entity].body, run[entity].acceleration, 0f)
                else
                    applyImpulse(body[entity].body, -run[entity].acceleration, 0f)
            }

        }
    }

    private fun applyImpulse(body: Body, speedX: Float, speedY: Float) =
            body.applyLinearImpulse(Vector2(speedX, speedY), body.worldCenter, true)
}
