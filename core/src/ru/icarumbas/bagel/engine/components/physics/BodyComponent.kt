package ru.icarumbas.bagel.engine.components.physics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body

class BodyComponent(@Transient val body: Body) : Component