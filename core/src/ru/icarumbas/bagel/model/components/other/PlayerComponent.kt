package ru.icarumbas.bagel.model.components.other

import com.badlogic.ashley.core.Component


class PlayerComponent(var money: Int) : Component {
    var collidingWithGround: Boolean = false
    var standindOnGround: Boolean = false
    var lastRight: Boolean = false
}