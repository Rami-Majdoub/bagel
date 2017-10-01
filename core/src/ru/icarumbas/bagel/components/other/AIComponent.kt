package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


data class AIComponent(var refreshSpeed: Float,
                       var attackDistance: Float,
                       var isPlayerRight: Boolean = true,
                       var isPlayerNear: Boolean = false,
                       var isPlayerUp: Boolean = false,
                       var appeared: Boolean = false,
                       var coldown: Float = 0f) : Component