package ru.icarumbas.bagel.model.components.velocity

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class TeleportComponent: Component {
    var disappearing: Boolean = false
    var appearing: Boolean = false
    var teleportTimer: Float = 0f
    val playerPosSecAgo = Vector2()
}