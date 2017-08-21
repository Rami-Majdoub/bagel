package ru.icarumbas.bagel.components.velocity

import com.badlogic.ashley.core.Component

data class FlyComponent(var lastRight: Boolean, var lastUp: Boolean) : Component