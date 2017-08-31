package ru.icarumbas.bagel

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.systems.rendering.EquipmentSystem

class Equip(
        val type : EquipmentSystem.EquipmentTypes,
        val component: Component,
        val hpUp: Float = 1f,
        val strengthUp: Float = 1f,
        val speedUp: Float = 1f,
        val knockBackUp: Float = 1f,
        val attackSpeedUp: Float = 1f)
