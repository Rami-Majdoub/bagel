package ru.icarumbas.bagel.Characters.mapObjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.PORTAL_DOOR_BIT
import ru.icarumbas.bagel.Screens.GameScreen


class PortalDoor : MapObject {

    override val bit = PORTAL_DOOR_BIT
    override var path = "doorClosed"
    override val width = 128f.div(PIX_PER_M)
    override val height = 192f.div(PIX_PER_M)

    var opened = false
    var timer = 0f

    @Suppress("Used for JSON Serialization")
    private constructor()

    constructor(rectangle: Rectangle) : super(rectangle)

    private fun open(gameScreen: GameScreen, delta: Float) {
        if (opened) {
            if (timer == 0f) {
                path = "doorOpened"
                loadSprite(gameScreen.textureAtlas)
            }
            timer += delta
        }

        if (timer > .25) {
            path = "doorClosed"
            loadSprite(gameScreen.textureAtlas)
            gameScreen.updateRoomObjects(gameScreen.currentMap, MathUtils.random(0, gameScreen.rooms.size.minus(1)))
            timer = 0f
            opened = false
        }

    }

    override fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        super.draw(batch, delta, gameScreen)
        open(gameScreen, delta)
    }
}