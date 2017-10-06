package ru.icarumbas.bagel.components.physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity


data class WeaponComponent(val type: Int,
                           var attackSpeed: Float = 0f,
                           val entityLeft: Entity,
                           val entityRight: Entity,
                           var attacking: Boolean = false) : Component