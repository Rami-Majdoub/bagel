package ru.icarumbas.bagel.engine.components.velocity

import com.badlogic.ashley.core.Component


class RunComponent(var acceleration: Float = 0f, var maxSpeed: Float = 0f) : Component