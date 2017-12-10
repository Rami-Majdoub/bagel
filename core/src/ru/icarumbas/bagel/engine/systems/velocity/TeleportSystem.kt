package ru.icarumbas.bagel.engine.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.AIComponent
import ru.icarumbas.bagel.engine.components.velocity.TeleportComponent
import ru.icarumbas.bagel.engine.systems.other.StateSystem
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.inView

class TeleportSystem : IteratingSystem{

    private val playerEntity: Entity
    private val rm: RoomWorld


    constructor(playerEntity: Entity, rm: RoomWorld) : super(Family.all(TeleportComponent::class.java, AIComponent::class.java).get()){
        this.playerEntity = playerEntity
        this.rm = rm
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            if (AI[entity].appeared)
            teleport[entity].teleportTimer += deltaTime

            if (teleport[entity].teleportTimer > 4f) {
                teleport[entity].playerPosSecAgo.set(body[playerEntity].body.position.x, body[playerEntity].body.position.y)
            }

            if (teleport[entity].teleportTimer > 5f &&
                    AI[entity].appeared &&
                    state[entity].currentState != StateSystem.ATTACKING &&
                    body[playerEntity].body.linearVelocity.y == 0f) {
                teleport[entity].disappearing = true
            }

            if (animation[entity].animations[StateSystem.DISAPPEARING]!!.isAnimationFinished(state[entity].stateTime)
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