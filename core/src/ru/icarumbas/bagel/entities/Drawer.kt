package ru.icarumbas.bagel.entities

import com.badlogic.gdx.graphics.g2d.Batch


interface Drawer {
    fun draw(batch: Batch, dt: Float)
}