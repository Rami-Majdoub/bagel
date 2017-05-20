package ru.icarumbas.bagel.Screens.Scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ru.icarumbas.bagel.Characters.Player


class Hud (val player: Player){

    var jumping = false
    var touchedOnce = false
    val stage: Stage
    var doubleJump = 0
    var touchpad: Touchpad
    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent = InputEvent()
    private val lStyle = Label.LabelStyle(BitmapFont(), Color.RED)
    val l = Label("", lStyle)
    val fps = Label("", lStyle)
    val width = Gdx.graphics.width.toFloat()
    val height = Gdx.graphics.height.toFloat()

    init {

        if ((width / height) == (16 / 10f))
            stage = Stage(StretchViewport(800f, 480f)) else
        if (width / height == 4 / 3f)
            stage = Stage(StretchViewport(800f, 600f)) else
        if (width / height == 16 / 9f)
            stage = Stage(StretchViewport(854f, 480f)) else
        if (width / height == 3 / 2f)
            stage = Stage(StretchViewport(960f, 640f)) else
        stage = Stage(StretchViewport(800f, 480f))


        val skin = Skin()
        skin.add("touchBackground", Texture("touchBackground.png"))
        val knob = Sprite(Texture("touchKnob.png"))
        knob.setSize(50f, 50f)
        skin.add("touchKnob", knob)

        val touchpadStyle = Touchpad.TouchpadStyle()
        touchpadStyle.background = skin.getDrawable("touchBackground")
        touchpadStyle.knob = skin.getDrawable("touchKnob")

        touchpad = Touchpad(6f, touchpadStyle)
        touchpad.setBounds(0f, 0f, 100f, 100f)
        stage.addActor(touchpad)

        l.setPosition(10f, 10f)
        l.setSize(25f, 25f)
        stage.addActor(l)

        fps.setPosition(770f, 460f)
        stage.addActor(fps)


        val attackBtnImgPressed = TextureRegionDrawable(TextureRegion(Texture("attackButtonPressed.png")))
        val attackBtnImg = TextureRegionDrawable(TextureRegion(Texture("attackButton.png")))


        val attackButton = Image(Texture("attackButton.png"))
        with (attackButton) {
            setBounds(700f, 50f, 85f, 85f)

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

    fun update(currentMap: Int) {
        stage.draw()
        l.setText("$currentMap")
        getDirection()
        fps.setText(Gdx.graphics.framesPerSecond.toString())
        detectJumping()

    }

    fun detectJumping(){
        if (player.playerBody.linearVelocity.x < 4.5f && touchpad.knobX > touchpad.width / 2) {
            player.playerBody.applyLinearImpulse(Vector2(touchpad.knobPercentX / 30, 0f), player.playerBody.worldCenter, true)
            player.lastRight = true
        }

        if (player.playerBody.linearVelocity.x > -4.5f && touchpad.knobX < touchpad.width / 2) {
            player.playerBody.applyLinearImpulse(Vector2(touchpad.knobPercentX / 30, 0f), player.playerBody.worldCenter, true)
            player.lastRight = false
        }

        if (touchpad.knobY > touchpad.height - 45 && doubleJump < 5 && player.playerBody.linearVelocity.y < 3.5) {
            if (doubleJump == 0) {
                jump(.15f)
            }
            if (doubleJump == 2 || doubleJump == 3 || doubleJump == 4) {
                jump(.07f)
            } else {
                jump(.05f)
            }

        }
        if (player.playerBody.linearVelocity.y == 0f && touchpad.knobY < touchpad.height - 45) {
            doubleJump = 0
            jumping = false
        }
    }

    private fun jump(velocity: Float){
        player.playerBody.applyLinearImpulse(Vector2(0f, velocity), player.playerBody.worldCenter, true)
        doubleJump++

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