package ru.icarumbas.bagel.engine.components.velocity

import com.badlogic.ashley.core.Component

class FlyComponent(val speed: Float) : Component {
    var lastRight: Boolean = false
    var lastUp: Boolean = false
}