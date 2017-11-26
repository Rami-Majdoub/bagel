package ru.icarumbas.bagel.model.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.model.components.other.RoomIdComponent
import ru.icarumbas.bagel.model.components.physics.BodyComponent
import ru.icarumbas.bagel.model.components.physics.StaticComponent
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.inActive
import ru.icarumbas.bagel.utils.Mappers.Mappers.roomId
import ru.icarumbas.bagel.utils.Mappers.Mappers.static

class AwakeSystem : IteratingSystem {

    private val rm: RoomManager

    private var lastMapId = -1


    constructor(rm: RoomManager) : super(Family.all(
            BodyComponent::class.java).one(
            RoomIdComponent::class.java,
            StaticComponent::class.java).get()) {

        this.rm = rm
    }

    override fun update(deltaTime: Float) {
        if (lastMapId != rm.currentMapId) {
            super.update(deltaTime)
            lastMapId = rm.currentMapId
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if (static.has(entity))
        body[entity].body.isActive = static[entity].mapPath == rm.path()
        if (roomId.has(entity) && !inActive.has(entity))
        body[entity].body.isActive = roomId[entity].id == rm.currentMapId
    }
}
