package ru.icarumbas.bagel.view.renderer.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2


class SizeComponent(val rectSize: Vector2, val scale: Float = 1f) : Component {
    val spriteSize: Vector2 = Vector2()
}