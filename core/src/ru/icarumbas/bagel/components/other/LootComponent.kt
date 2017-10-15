package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


class LootComponent(val components: ArrayList<Component>): Component {
    var isCollidingWithPlayer = false
}