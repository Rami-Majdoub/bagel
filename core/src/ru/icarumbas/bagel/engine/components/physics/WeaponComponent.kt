package ru.icarumbas.bagel.engine.components.physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ru.icarumbas.bagel.engine.systems.physics.WeaponSystem


class WeaponComponent(val type: WeaponSystem.WeaponType,
                           var entityLeft: Entity,
                           var entityRight: Entity) : Component {

    var attacking: Boolean = false
}