package ru.icarumbas.bagel.engine.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity


class AIComponent(var refreshSpeed: Float, var attackDistance: Float, var entityTarget: Entity) : Component {

    var isTargetRight: Boolean = true
    var isTargetHigher: Boolean = true
    var isTargetNear: Boolean = false
    var isTargetEqualX: Boolean = false
    var appeared: Boolean = false
    var coldown: Float = 0f
}