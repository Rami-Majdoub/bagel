package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


class AIComponent(var refreshSpeed: Float, var attackDistance: Float) : Component {

    var isPlayerRight: Boolean = true
    var isPlayerNear: Boolean = false
    var appeared: Boolean = false
    var coldown: Float = 0f
}