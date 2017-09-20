package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.utils.Mappers

class AISystem: IteratingSystem {

    private val ai = Mappers.AI
    private val body = Mappers.body

    private val playerEntity: Entity

    constructor(playerEntity: Entity): super(Family.all(AIComponent::class.java).get()) {
        this.playerEntity = playerEntity
    }

    private fun isPlayerRight(e: Entity): Boolean{
        return body[playerEntity].body.position.x >= body[e].body.position.x
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        ai[entity].isPlayerRight = isPlayerRight(entity)
    }
}