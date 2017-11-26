package ru.icarumbas.bagel.model.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.model.components.other.LootComponent
import ru.icarumbas.bagel.utils.Mappers.Mappers.loot
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.view.scenes.Hud


class LootSystem : IteratingSystem {

    private val hud: Hud
    private val rm: RoomManager
    private val playerEntity: Entity
    private val deleteList: ArrayList<Entity>


    constructor(hud: Hud, rm: RoomManager, playerEntity: Entity, deleteList: ArrayList<Entity>) : super(Family.all(LootComponent::class.java).get()) {
        this.hud = hud
        this.rm = rm
        this.playerEntity = playerEntity
        this.deleteList = deleteList
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (loot[e].isCollidingWithPlayer) {
                loot[e].components.forEach { playerEntity.add(it) }
                deleteList.add(e)
            }
        }
    }

}