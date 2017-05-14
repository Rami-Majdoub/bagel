package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World


interface MapObject{

    val body: Body
    var sprite: Sprite?

    fun defineBody(world: World)

    fun loadSprite(textureAtlas: TextureAtlas)

    fun draw(batch: Batch)

}