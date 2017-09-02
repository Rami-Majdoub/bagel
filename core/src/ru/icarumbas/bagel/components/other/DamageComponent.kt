package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


data class DamageComponent(var HP: Int,
                           var canBeAttacked: Boolean = false,
                           var hitTimer: Float = 0f,
                           var damage: Int = 0) : Component