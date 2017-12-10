package ru.icarumbas.bagel.view.renderer.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion


class TextureComponent(val tex: TextureRegion = TextureRegion()): Component {
    var color = Color.WHITE!!
}