package ru.icarumbas.bagel.model.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.model.components.other.AIComponent
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.animation
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.roomId
import ru.icarumbas.bagel.utils.Mappers.Mappers.size
import ru.icarumbas.bagel.utils.Mappers.Mappers.state
import ru.icarumbas.bagel.utils.inView

class AISystem: IteratingSystem {


    private val rm: RoomManager

    constructor(rm: RoomManager): super(Family.all(AIComponent::class.java).get()) {
        this.rm = rm
    }

    private fun isTargetRight(e: Entity) =
            body[AI[e].entityTarget].body.position.x > body[e].body.position.x

    private fun isTargetHigher(e: Entity) =
            body[AI[e].entityTarget].body.position.y > body[e].body.position.y

    private fun isTargetEqual(e: Entity) =
            MathUtils.round(body[AI[e].entityTarget].body.position.x) == MathUtils.round(body[e].body.position.x)

    private fun isTargetNear(e: Entity): Boolean {
        with (body[AI[e].entityTarget].body.position) {
            return  x >= body[e].body.position.x - AI[e].attackDistance &&
                    x <= body[e].body.position.x + AI[e].attackDistance &&
                    y >= body[e].body.position.y - size[e].rectSize.y / 2 &&
                    y <= body[e].body.position.y + size[e].rectSize.y / 2
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {


            AI[entity].isTargetRight = isTargetRight(entity)
            AI[entity].isTargetHigher = isTargetHigher(entity)
            AI[entity].isTargetNear = isTargetNear(entity)
            AI[entity].isTargetEqualX = isTargetEqual(entity)

            if (AI[entity].isTargetNear || AI[entity].coldown > 0f) AI[entity].coldown += deltaTime

            if (state[entity].states.contains(StateSystem.APPEARING) && !AI[entity].appeared) {
                if (animation[entity].animations[StateSystem.APPEARING]?.isAnimationFinished(state[entity].stateTime)!!
                        && state[entity].currentState == StateSystem.APPEARING) {
                    AI[entity].appeared = true
                    roomId[entity].serialized.appeared = true

                }
            } else {
                AI[entity].appeared = true
                roomId[entity].serialized.appeared = true
            }
        }
    }
}