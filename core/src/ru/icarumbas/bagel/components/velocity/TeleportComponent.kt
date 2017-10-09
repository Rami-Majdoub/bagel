package ru.icarumbas.bagel.components.velocity

import com.badlogic.ashley.core.Component

class TeleportComponent: Component {
    var disappearing: Boolean = false
    var appearing: Boolean = false
    var teleportTimer: Float = 0f
}