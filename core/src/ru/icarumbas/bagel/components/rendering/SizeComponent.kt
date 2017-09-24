package ru.icarumbas.bagel.components.rendering

import com.badlogic.ashley.core.Component


data class SizeComponent(val scale: Float = 1f, var width: Float = 0f, var height: Float = 0f) : Component