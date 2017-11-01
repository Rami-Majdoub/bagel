package ru.icarumbas.bagel.components.rendering

import com.badlogic.ashley.core.Component


class TranslateComponent(var x: Float = 0f, var y: Float = 0f): Component{
    var angle = 0f
}