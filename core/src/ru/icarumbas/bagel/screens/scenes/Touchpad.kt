package ru.icarumbas.bagel.screens.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable


class Touchpad : PlayerController{

    private var touchedOnce = false
    private var touchpad: Touchpad

    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent = InputEvent()

    constructor(stage: Stage, uiAtlas: TextureAtlas) {

        val knob = Sprite(uiAtlas.findRegion("touchKnob"))
        knob.setSize(stage.width / 20, stage.width / 20)

        val touchpadStyle = Touchpad.TouchpadStyle()
        touchpadStyle.background = TextureRegionDrawable(uiAtlas.findRegion("touchBackground"))
        touchpadStyle.knob = SpriteDrawable(knob)

        touchpad = Touchpad(6f, touchpadStyle)
        touchpad.setBounds(0f, 0f, stage.width/9, stage.width/9)
        stage.addActor(touchpad)

        fakeTouchDownEvent.type = InputEvent.Type.touchDown
    }


    fun getDirection() {
        if (Gdx.input.justTouched()) {
            touchedOnce = true

            // Get the touch point in screen coordinates.
            screenPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())

            if (screenPos.x < Gdx.graphics.width / 2) {

                // Convert the touch point into local coordinates, place the touchpad and show it.
                localPos.set(screenPos)
                localPos = touchpad.parent.screenToLocalCoordinates(localPos)
                touchpad.setPosition(localPos.x - touchpad.width / 2, localPos.y - touchpad.height / 2)
                touchpad.isVisible = true

                // Fire a touch down event to get the touchpad working.
                val stagePos = touchpad.stage.screenToStageCoordinates(screenPos)
                fakeTouchDownEvent.stageX = stagePos.x
                fakeTouchDownEvent.stageY = stagePos.y
                touchpad.fire(fakeTouchDownEvent)
            }
        } else if (!Gdx.input.isTouched) {
            touchpad.isVisible = false
        }

    }

    override fun isUpPressed() = touchpad.knobY > touchpad.height / 2 + touchpad.width/10

    override fun isRightPressed() = touchpad.knobX > touchpad.width / 2 + touchpad.width/20

    override fun isDownPressed() = touchpad.knobY < touchpad.height / 2f - touchpad.width/6f

    override fun isLeftPressed() = touchpad.knobX < touchpad.width / 2 - touchpad.width/20
}