package ru.icarumbas.bagel.model.components.rendering

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion


class TextureComponent(var tex: TextureRegion? = null): Component {
    var color = Color.WHITE!!
}