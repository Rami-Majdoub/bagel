package ru.icarumbas.bagel.engine.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.RoomIdComponent
import ru.icarumbas.bagel.engine.components.physics.BodyComponent
import ru.icarumbas.bagel.engine.components.physics.StaticComponent
import ru.icarumbas.bagel.engine.world.RoomWorld

class AwakeSystem : IteratingSystem {

    private val rm: RoomWorld

    private var lastMapId = -1


    constructor(rm: RoomWorld) : super(Family.all(
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
