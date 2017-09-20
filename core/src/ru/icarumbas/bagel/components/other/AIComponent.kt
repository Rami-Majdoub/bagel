package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


data class AIComponent(var isPlayerRight: Boolean = false,
                       var isPlayerUp: Boolean = false,
                       var readyAttack: Boolean = false) : Component