package ru.icarumbas.bagel.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.bagel.components.velocity.JumpComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers


class JumpingSystem : IteratingSystem {

    private val body = Mappers.body
    private val jump = Mappers.jump
    private val hud: Hud

    constructor(hud: Hud) : super(Family.all(JumpComponent::class.java).get()) {
        this.hud = hud
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (jump[entity].maxJumps > jump[entity].jumps && body[entity].body.linearVelocity.y < 3.5f) {
            if (Mappers.player.has(entity) && hud.isUpPressed()) {
                jump(entity)
            }
        }

        relax(entity)
    }

    //TODO("Entities except player climb on walls")

    fun relax(e: Entity){
        // Here i took only player's situation

        if (Mappers.state[e].currentState != StateSystem.JUMPING) {
            if (Mappers.player.has(e)) {
                if (!hud.isUpPressed() || !Mappers.player[e].collidingWithGround){
                    jump[e].jumps = 0
                }
            } else {
                jump[e].jumps = 0
            }
        }
    }

    fun jump(e: Entity) {
        if (jump[e].jumps == 0)
            body[e].body.applyLinearImpulse(Vector2(0f, jump[e].jumpVelocity), body[e].body.worldCenter, true)
        else
            body[e].body.applyLinearImpulse(Vector2(0f, jump[e].jumpVelocity / 2), body[e].body.worldCenter, true)

        jump[e].jumps++
    }
}