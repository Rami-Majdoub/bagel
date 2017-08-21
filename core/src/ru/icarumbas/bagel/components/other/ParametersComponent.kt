package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


data class ParametersComponent(var HP: Int,
                               var acceleration: Float = 0f,
                               var maxSpeed: Float = 0f,
                               var strength: Int = 0,
                               var knockback: Float = 0f,
                               var nearAttackStrength: Int = 0,
                               var jumpVelocity: Float = 0f,
                               val maxJumps: Int = 1,
                               val mass: Int = 0
                               ) : Component