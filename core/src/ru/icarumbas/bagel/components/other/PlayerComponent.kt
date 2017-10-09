package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.Equip


class PlayerComponent(var money: Int) : Component {
    var collidingWithGround: Boolean = false
    var lastRight: Boolean = false
}