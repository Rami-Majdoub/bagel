package ru.icarumbas.bagel.model.components.rendering

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion


class AnimationComponent(val animations: HashMap<String, Animation<out TextureRegion>>) : Component