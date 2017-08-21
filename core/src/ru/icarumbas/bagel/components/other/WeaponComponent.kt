package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Body


data class WeaponComponent(val type: String,
                           val weaponBody: Body? = null,
                           val weaponAnimation: Animation<TextureRegion>? = null,
                           val bulletBodies: ArrayList<Body>? = null,
                           val bulletAnimation: Animation<TextureRegion>? = null,
                           var weaponTexture: TextureRegion? = null,
                           var bulletTexture: TextureRegion? = null,
                           val stateTimer: Float = 0f,
                           var attacking: Boolean = false) : Component