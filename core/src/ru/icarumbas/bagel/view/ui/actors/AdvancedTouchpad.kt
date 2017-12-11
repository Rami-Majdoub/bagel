package ru.icarumbas.bagel.view.ui.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import ru.icarumbas.bagel.engine.controller.PlayerController


class AdvancedTouchpad : Touchpad, PlayerController {

    // Touch event to work properly
    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent = InputEvent()

    // Make invisible when not pressed
    var changeVisible = true

    constructor(deadZone: Float, style: TouchpadStyle) : super(deadZone, style) {

        fakeTouchDownEvent.type = InputEvent.Type.touchDown
    }

    override fun act(delta: Float) {
        super.act(delta)

        getDirection()
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        return if (touchable && this.touchable != Touchable.enabled) null else super.hit(x, y, touchable)
    }

    private fun getDirection() {

        if (Gdx.input.justTouched()) {

            // Get the touch point in screen coordinates.
            screenPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())

            if (screenPos.x < Gdx.graphics.width / 2) {

                // Convert the touch point into local coordinates, place the touchpad and show it.
                localPos.set(screenPos)
                localPos = parent.screenToLocalCoordinates(localPos)
                setPosition(localPos.x - width / 2, localPos.y - height / 2)

                // Fire a touch down event to get the touchpad working.
                val stagePos = stage.screenToStageCoordinates(screenPos)
                fakeTouchDownEvent.stageX = stagePos.x
                fakeTouchDownEvent.stageY = stagePos.y
                fire(fakeTouchDownEvent)

                isVisible = true

            }

        } else if (!Gdx.input.isTouched && changeVisible) {
            isVisible = false
        }

    }

    override fun isUpPressed() = knobY > height / 2 + width / 10

    override fun isRightPressed() = knobX > width / 2 + width / 20

    override fun isDownPressed() = knobY < height / 2f - width / 6f

    override fun isLeftPressed() = knobX < width / 2 - width / 20
}