package ru.icarumbas.bagel.engine.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.RoomIdComponent
import ru.icarumbas.bagel.engine.components.physics.BodyComponent
import ru.icarumbas.bagel.engine.components.physics.StaticComponent
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.body
import ru.icarumbas.bagel.utils.inActive
import ru.icarumbas.bagel.utils.roomId
import ru.icarumbas.bagel.utils.statik

class AwakeSystem(

        private val rm: RoomWorld

) : IteratingSystem(Family.all(BodyComponent::class.java).one(RoomIdComponent::class.java, StaticComponent::class.java).get()) {


    private var lastMapId = -1


    override fun update(deltaTime: Float) {
        if (lastMapId != rm.currentMapId) {
            super.update(deltaTime)
            lastMapId = rm.currentMapId
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if (statik.has(entity))
        body[entity].body.isActive = statik[entity].mapPath == rm.getMapPath()
        if (roomId.has(entity) && !inActive.has(entity))
        body[entity].body.isActive = roomId[entity].id == rm.currentMapId
    }
}
