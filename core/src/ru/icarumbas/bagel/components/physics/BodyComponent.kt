package ru.icarumbas.bagel.components.physics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body

data class BodyComponent(@Transient val body: Body) : Component