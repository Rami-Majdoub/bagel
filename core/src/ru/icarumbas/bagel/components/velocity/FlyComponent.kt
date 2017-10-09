package ru.icarumbas.bagel.components.velocity

import com.badlogic.ashley.core.Component

class FlyComponent : Component {
    var lastRight: Boolean = false
    var lastUp: Boolean = false
}