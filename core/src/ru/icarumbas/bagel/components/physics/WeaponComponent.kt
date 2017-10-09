package ru.icarumbas.bagel.components.physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity


class WeaponComponent(val type: Int,
                           var attackSpeed: Float,
                           val entityLeft: Entity,
                           val entityRight: Entity) : Component {

    var attacking: Boolean = false
}