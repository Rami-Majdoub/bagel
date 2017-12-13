package ru.icarumbas.bagel.view.renderer.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.icarumbas.bagel.engine.entities.EntityState


class AnimationComponent(val animations: HashMap<EntityState, Animation<out TextureRegion>>) : Component