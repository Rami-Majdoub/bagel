package ru.icarumbas.bagel.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.components.velocity.TeleportComponent
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.animation
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.size
import ru.icarumbas.bagel.utils.Mappers.Mappers.state
import ru.icarumbas.bagel.utils.Mappers.Mappers.teleport
import ru.icarumbas.bagel.utils.inView

class TeleportSystem : IteratingSystem{

    private val playerEntity: Entity
    private val rm: RoomManager


    constructor(playerEntity: Entity, rm: RoomManager) : super(Family.all(TeleportComponent::class.java, AIComponent::class.java).get()){
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