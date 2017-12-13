package ru.icarumbas.bagel.engine.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.AIComponent
import ru.icarumbas.bagel.engine.components.velocity.TeleportComponent
import ru.icarumbas.bagel.engine.entities.EntityState
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*

class TeleportSystem(

        private val playerEntity: Entity,
        private val rm: RoomWorld

) : IteratingSystem(Family.all(TeleportComponent::class.java, AIComponent::class.java).get()){


    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            if (AI[entity].appeared)
            teleport[entity].teleportTimer += deltaTime

            if (teleport[entity].teleportTimer > 4f) {
                teleport[entity].playerPosSecAgo.set(body[playerEntity].body.position.x, body[playerEntity].body.position.y)
            }

            if (teleport[entity].teleportTimer > 5f &&
                    AI[entity].appeared &&
                    state[entity].currentState != EntityState.ATTACKING &&
                    body[playerEntity].body.linearVelocity.y == 0f) {
                teleport[entity].disappearing = true
            }

            if (animation[entity].animations[EntityState.DISAPPEARING]!!.isAnimationFinished(state[entity].stateTime)
                    && teleport[entity].disappearing ) {
                body[entity].body.setTransform(
                        teleport[entity].playerPosSecAgo.x,
                        teleport[entity].playerPosSecAgo.y + (size[entity].rectSize.y - size[playerEntity].rectSize.y) / 2,
                        0f)

                teleport[entity].teleportTimer = 0f
                teleport[entity].disappearing = false
                teleport[entity].appearing = true
                AI[entity].appeared = false
            }
        }
    }
}