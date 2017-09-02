package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.components.other.EquipmentComponent
import ru.icarumbas.bagel.utils.Mappers


class EquipmentSystem : IteratingSystem {

    private val equip = Mappers.equipment


    companion object EquipmentTypes {
        val HEAD = "HEAD"
        val BODY = "BODY"
        val LEGS = "LEGS"
        val WEAPON = "WEAPON"
        val ACCESSORY = "ACCESSORY"
    }

    constructor() : super(Family.all(EquipmentComponent::class.java).get())

    override fun processEntity(entity: Entity, deltaTime: Float) {

    }
}