package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.utils.Equip


data class EquipmentComponent(val equipment: ArrayList<Equip> = ArrayList()) : Component