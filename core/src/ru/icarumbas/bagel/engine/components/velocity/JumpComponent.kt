package ru.icarumbas.bagel.engine.components.velocity

import com.badlogic.ashley.core.Component


class JumpComponent(var jumpVelocity: Float = 0f, val maxJumps: Int = 1, var jumps: Int = 0) : Component