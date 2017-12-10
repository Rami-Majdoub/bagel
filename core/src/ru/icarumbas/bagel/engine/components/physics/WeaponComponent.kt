package ru.icarumbas.bagel.engine.components.physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity


class WeaponComponent(val type: Int,
                           var entityLeft: Entity,
                           var entityRight: Entity) : Component {

    var attacking: Boolean = false
}