package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView

class AISystem: IteratingSystem {

    private val ai = Mappers.AI
    private val body = Mappers.body
    private val anim = Mappers.animation
    private val state = Mappers.state
    private val size = Mappers.size
    private val playerEntity: Entity

    private val rm: RoomManager

    constructor(playerEntity: Entity, rm: RoomManager): super(Family.all(AIComponent::class.java).get()) {
        this.playerEntity = playerEntity
        this.rm = rm
    }

    private fun isTargetRight(e: Entity): Boolean{
        return body[ai[e].entityTarget].body.position.x > body[e].body.position.x
    }

    private fun isTargetEqual(e: Entity): Boolean{
        return MathUtils.round(body[ai[e].entityTarget].body.position.x) == MathUtils.round(body[e].body.position.x)
    }

    private fun isTargetNear(e: Entity): Boolean {
        with (body[ai[e].entityTarget].body.position) {
            return  x >= body[e].body.position.x - ai[e].attackDistance &&
                    x <= body[e].body.position.x + ai[e].attackDistance &&
                    y >= body[e].body.position.y - size[e].rectSize.y / 2 &&
                    y <= body[e].body.position.y + size[e].rectSize.y / 2
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {

            if (ai[entity].entityTarget == null) ai[entity].entityTarget = playerEntity

            ai[entity].isTargetRight = isTargetRight(entity)
            ai[entity].isTargetNear = isTargetNear(entity)
            ai[entity].isTargetEqualX = isTargetEqual(entity)

            if (ai[entity].isTargetNear || ai[entity].coldown > 0f) ai[entity].coldown += deltaTime

            if (anim[entity].animations[StateSystem.APPEARING]?.isAnimationFinished(state[entity].stateTime)!!
                    && state[entity].currentState == StateSystem.APPEARING)
                ai[entity].appeared = true
        }
    }
}