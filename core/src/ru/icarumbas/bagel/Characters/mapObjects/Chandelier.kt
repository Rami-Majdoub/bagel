package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.CHEST_BIT
import ru.icarumbas.PIX_PER_M


class Chandelier: MapObject{

    override val bit: Short = 1
    override var destroyed = false
    lateinit override var body: Body
    override var sprite: Sprite? = null
    var path = ""
    var posX = 0f
    var posY = 0f

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) {

        posX = rectangle.x.div(PIX_PER_M)
        posY = rectangle.y.div(PIX_PER_M)

        when (MathUtils.random(1)) {
            0 -> path = "goldenChandelier"
            1 -> path = "silverChandelier"

        }
    }

    override fun loadSprite(textureAtlas: TextureAtlas) {
        sprite = textureAtlas.createSprite(path)
        sprite!!.setSize(127.div(PIX_PER_M), 192.div(PIX_PER_M))
        sprite!!.setPosition(posX, posY)
    }
}