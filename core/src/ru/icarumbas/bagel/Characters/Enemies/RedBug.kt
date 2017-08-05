package ru.icarumbas.bagel.Characters.Enemies

import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Utils.WorldCreate.AnimationCreator


class RedBug : Enemy, FlyMovement {

    override val velocity = Vector2()
    override var lastRight = true
    override var lastUp = true

    override val strength = 10
    override val width = 92f.div(PIX_PER_M)
    override val height = 92f.div(PIX_PER_M)
    override val speed = .25f
    override val gravityScale = 0f

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    override fun move(player: Player, delta: Float) {
        fly(player, body!!, isPlayerRight(player), isPlayerHigher(player))
    }

    override fun loadAnimation(textureAtlas: TextureAtlas, animationCreator: AnimationCreator) {
        stateAnimation = Animation(.1f, textureAtlas.findRegions("redBug"), Animation.PlayMode.LOOP)
        sprite = Sprite()

    }


}