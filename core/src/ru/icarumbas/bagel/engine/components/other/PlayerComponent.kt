package ru.icarumbas.bagel.engine.components.other

import com.badlogic.ashley.core.Component


class PlayerComponent(var money: Int = 0) : Component {
    var collidingWithGround: Boolean = false
    var standindOnGround: Boolean = false
    var lastRight: Boolean = false
}