package ru.icarumbas.bagel.engine.components.other

import com.badlogic.ashley.core.Component


class LootComponent(val components: ArrayList<Component>): Component {
    var isCollidingWithPlayer = false
}