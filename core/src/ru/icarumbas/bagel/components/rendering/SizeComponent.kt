package ru.icarumbas.bagel.components.rendering

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2


data class SizeComponent(val rectSize: Vector2, val scale: Float = 1f, val spriteSize: Vector2 = Vector2()) : Component