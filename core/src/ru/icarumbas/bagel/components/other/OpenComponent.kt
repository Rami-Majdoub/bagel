package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity


class OpenComponent(val loot: ArrayList<Entity>? = null) : Component{
    var isCollidingWithPlayer = false
    var opening = false
}