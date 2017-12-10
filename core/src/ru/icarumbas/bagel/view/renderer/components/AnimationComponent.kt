package ru.icarumbas.bagel.view.renderer.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion


class AnimationComponent(val animations: HashMap<String, Animation<out TextureRegion>>) : Component