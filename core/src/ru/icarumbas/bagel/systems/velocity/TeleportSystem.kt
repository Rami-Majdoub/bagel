package ru.icarumbas.bagel.systems.velocity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.components.velocity.TeleportComponent
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight

class TeleportSystem : IteratingSystem{

    private val teleport = Mappers.teleport
    private val body = Mappers.body
    private val size = Mappers.size
    private val ai = Mappers.AI
    private val state = Mappers.state
    private val anim = Mappers.animation
    private val playerEntity: Entity
    private val rm: RoomManager


    constructor(playerEntity: Entity, rm: RoomManager) : super(Family.all(TeleportComponent::class.java, AIComponent::class.java).get()){
        this.playerEntity = playerEntity
        this.rm = rm
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            teleport[entity].teleportTimer += deltaTime

            if (teleport[entity].teleportTimer > 10f &&
                    ai[entity].appeared &&
                    state[entity].currentState != StateSystem.ATTACKING &&
                    body[playerEntity].body.linearVelocity.y == 0f) {
                teleport[entity].disappearing = true
            }

            if (anim[entity].animations[StateSystem.DISAPPEARING]!!.isAnimationFinished(state[entity].stateTime)
                    && teleport[entity].disappearing ) {
                body[entity].body.setTransform(
                        body[playerEntity].body.position.x + if (playerEntity.rotatedRight()) -ai[entity].attackDistance else ai[entity].attackDistance,
                        body[playerEntity].body.position.y + (size[entity].rectSize.y - size[playerEntity].rectSize.y) / 2,
                        0f)
                teleport[entity].teleportTimer = 0f
                teleport[entity].disappearing = false
                teleport[entity].appearing = true
                ai[entity].appeared = false
            }
        }
    }
}