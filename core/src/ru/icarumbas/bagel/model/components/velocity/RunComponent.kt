package ru.icarumbas.bagel.model.components.velocity

import com.badlogic.ashley.core.Component


class RunComponent(var acceleration: Float = 0f, var maxSpeed: Float = 0f) : Component