package ru.icarumbas.bagel.engine.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.LootComponent
import ru.icarumbas.bagel.engine.world.RoomWorldState
import ru.icarumbas.bagel.utils.Mappers.loot
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.view.scenes.Hud


class LootSystem : IteratingSystem {

    private val hud: Hud
    private val roomWorldState: RoomWorldState
    private val playerEntity: Entity
    private val deleteList: ArrayList<Entity>


    constructor(roomWorldState: RoomWorldState, hud: Hud, playerEntity: Entity, deleteList: ArrayList<Entity>) : super(Family.all(LootComponent::class.java).get()) {
        this.hud = hud
        this.roomWorldState = roomWorldState
        this.playerEntity = playerEntity
        this.deleteList = deleteList
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(roomWorldState)) {
            if (loot[e].isCollidingWithPlayer) {
                loot[e].components.forEach { playerEntity.add(it) }
                deleteList.add(e)
            }
        }
    }

}