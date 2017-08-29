package ru.icarumbas.bagel.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.utils.Mappers

class AwakeSystem : IteratingSystem {

    private val body = Mappers.body
    private val roomId = Mappers.roomId
    private val staticMarker = Mappers.static

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
        if (staticMarker.has(entity))
        body[entity].body.isActive = staticMarker[entity].mapPath == rm.path()
        if (roomId.has(entity))
        body[entity].body.isActive = roomId[entity].id == rm.currentMapId
    }
}
