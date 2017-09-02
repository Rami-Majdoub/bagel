package ru.icarumbas.bagel.components.velocity

import com.badlogic.ashley.core.Component


data class RunComponent(var acceleration: Float = 0f, var maxSpeed: Float = 0f, var lastRight: Boolean = false) : Component