package ru.icarumbas.bagel.model.components.other

import com.badlogic.ashley.core.Component


class LootComponent(val components: ArrayList<Component>): Component {
    var isCollidingWithPlayer = false
}