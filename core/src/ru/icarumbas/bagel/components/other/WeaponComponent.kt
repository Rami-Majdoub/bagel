package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Body


data class WeaponComponent(val type: Int,
                           var strength: Int = 0,
                           val attackSpeed: Float = 0f,
                           var nearAttackStrength: Int = 0,
                           var knockback: Float = 0f,
                           val entityLeft: Entity,
                           val entityRight: Entity,
                           val bulletBodies: ArrayList<Body>? = null,
                           val bulletAnimation: Animation<TextureRegion>? = null,
                           val stateTimer: Float = 0f,
                           var attacking: Boolean = false) : Component