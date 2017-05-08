package ru.icarumbas.bagel.Screens.Scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.icarumbas.bagel.Characters.Player


class Hud (val player: Player){

    var jumping = false
    var touchedOnce = false
    val stage = Stage(StretchViewport(800f, 480f))
    var doubleJump = 0
    var touchpad: Touchpad
    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent = InputEvent()
    private val lStyle = Label.LabelStyle(BitmapFont(), Color.RED)
    val l = Label("", lStyle)
    val fps = Label("", lStyle)

    init {

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

        fps.setPosition(770f, 460f)
        stage.addActor(fps)


        val attackBtnImgPressed = TextureRegionDrawable(TextureRegion(Texture("attackButtonPressed.png")))
        val attackBtnImg = TextureRegionDrawable(TextureRegion(Texture("attackButton.png")))


        var attackButton = Image(Texture("attackButton.png"))
        with (attackButton) {
            setBounds(700f, 50f, 75f, 75f)

            addListener(object : InputListener() {
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    attackButton.drawable = attackBtnImg
                    player.attacking = false
                }

                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    attackButton.drawable = attackBtnImgPressed
                    player.attacking = true
                    return true
                }
            })

        }
        stage.addActor(attackButton)

        fakeTouchDownEvent.type = InputEvent.Type.touchDown

    }

    fun update() {
        getDirection()
        fps.setText(Gdx.graphics.framesPerSecond.toString())

        if (player.playerBody.linearVelocity.x < 3.5f && touchpad.knobX > touchpad.width / 2) {
            player.playerBody.applyLinearImpulse(Vector2(touchpad.knobPercentX / 12, 0f), Vector2(100f, 100f), true)
            player.lastRight = true
        }

        if (player.playerBody.linearVelocity.x > -3.5f && touchpad.knobX < touchpad.width / 2) {
            player.playerBody.applyLinearImpulse(Vector2(-touchpad.knobPercentX / -12, 0f), player.playerBody.worldCenter, true)
            player.lastRight = false
        }

        if (touchpad.knobY > touchpad.height - 45 && doubleJump < 10 && player.playerBody.linearVelocity.y < 2f) {
            if (doubleJump == 1) {
                player.playerBody.applyLinearImpulse(Vector2(0f, 1f), player.playerBody.worldCenter, true)
                doubleJump++
                jumping = true
            } else {
                player.playerBody.applyLinearImpulse(Vector2(0f, .15f), player.playerBody.worldCenter, true)
                doubleJump++
            }
        }
    }

    private fun getDirection() {
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
}