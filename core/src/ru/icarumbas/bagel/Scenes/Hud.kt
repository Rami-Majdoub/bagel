package ru.icarumbas.bagel.Scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.icarumbas.bagel.Characters.Player


class Hud {

    var jumping: Boolean = false
    var touchedFirst: Boolean = false
    val stage = Stage(StretchViewport(800f, 480f))
    var doubleJump = 0
    var touchpad: Touchpad
    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent: InputEvent
    val l = Label("", Label.LabelStyle(BitmapFont(), Color.BLACK))

    init {

        Gdx.input.inputProcessor = stage

        val skin = Skin()
        skin.add("touchBackground", Texture("touchBackground.png"))
        skin.add("touchKnob", Texture("touchKnob.png"))


        val touchpadStyle = Touchpad.TouchpadStyle()
        touchpadStyle.background = skin.getDrawable("touchBackground")
        touchpadStyle.knob = skin.getDrawable("touchKnob")

        touchpad = Touchpad(10f, touchpadStyle)
        touchpad.setBounds(0f, 0f, 150f, 150f)
        stage.addActor(touchpad)

        l.setPosition(10f, 10f)
        l.setSize(25f, 25f)
        stage.addActor(l)

        fakeTouchDownEvent = InputEvent()
        fakeTouchDownEvent.type = InputEvent.Type.touchDown

    }

    fun update(player: Player) {
        getDirection()

        if (player.playerBody!!.linearVelocity.x < 3.5f && touchpad.knobX > touchpad.width / 2) {
            player.playerBody!!.applyLinearImpulse(Vector2(touchpad.knobPercentX / 12, 0f), player.playerBody!!.worldCenter, true)
            player.lastRight = true
        }

        if (player.playerBody!!.linearVelocity.x > -3.5f && touchpad.knobX < touchpad.width / 2) {
            player.playerBody!!.applyLinearImpulse(Vector2(-touchpad.knobPercentX / -12, 0f), player.playerBody!!.worldCenter, true)
            player.lastRight = false
        }

        if (touchpad.knobY > touchpad.height - 45 && doubleJump < 10 && player.playerBody!!.linearVelocity.y < 2f) {
            if (doubleJump == 1) {
                player.playerBody!!.applyLinearImpulse(Vector2(0f, 1f), player.playerBody!!.worldCenter, true)
                doubleJump++
                jumping = true
            } else {
                player.playerBody!!.applyLinearImpulse(Vector2(0f, .15f), player.playerBody!!.worldCenter, true)
                doubleJump++
            }
        }
    }

    private fun getDirection() {
        if (Gdx.input.justTouched()) {
            touchedFirst = true
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
}