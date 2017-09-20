package ru.icarumbas.bagel.components.rendering

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion


data class AnimationComponent(val animations: HashMap<String, Animation<out TextureRegion>>) : Component