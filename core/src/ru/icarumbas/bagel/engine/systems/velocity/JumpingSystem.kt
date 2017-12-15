package ru.icarumbas.bagel.engine.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.bagel.engine.components.velocity.JumpComponent
import ru.icarumbas.bagel.engine.controller.PlayerMoveController
import ru.icarumbas.bagel.engine.entities.EntityState
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*


class JumpingSystem(

        private val playerController: PlayerMoveController,
        private val rm: RoomWorld

) : IteratingSystem(Family.all(JumpComponent::class.java).get()) {


    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            if (jump[entity].maxJumps > jump[entity].jumps && body[entity].body.linearVelocity.y < 3.5f) {
                if (player.has(entity) && playerController.isUpPressed()) {
                    jump(entity)
                }
            }

            relax(entity)
        }
    }

    private fun relax(e: Entity){
        // Here i took only player's situation

        if (state[e].currentState != EntityState.JUMPING && state[e].currentState != EntityState.JUMP_ATTACKING) {
            if (player.has(e)) {
                if (!playerController.isUpPressed() || !player[e].collidingWithGround){
                    jump[e].jumps = 0
                }
            } else {
                jump[e].jumps = 0
            }
        }
    }

    private fun jump(e: Entity) {
        if (jump[e].jumps == 0)
            body[e].body.applyLinearImpulse(Vector2(0f, jump[e].jumpVelocity), body[e].body.worldCenter, true)
        else
            body[e].body.applyLinearImpulse(Vector2(0f, jump[e].jumpVelocity / 2), body[e].body.worldCenter, true)

        jump[e].jumps++
    }
}