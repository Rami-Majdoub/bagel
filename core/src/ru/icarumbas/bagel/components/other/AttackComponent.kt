package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class AttackComponent(var strength: Int = 0,
                      var knockback: Vector2 = Vector2(0f, 0f)) : Component