package ru.icarumbas.bagel.Screens.Scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.GameScreen


class Hud(val gameScreen: GameScreen): InputListener(){

    var touchedOnce = false
    var openButtonPressed = false
    val stage: Stage
    var touchpad: Touchpad
    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent = InputEvent()
    private val lStyle = Label.LabelStyle(BitmapFont(), Color.RED)
    private val currentRoom = Label("Current room: ", lStyle)
    val hp = Label("HP: 100", lStyle)
    val fps = Label("FPS: ", lStyle)
    val money = Label("Money: ", lStyle)
    val width = Gdx.graphics.width.toFloat()
    val height = Gdx.graphics.height.toFloat()
    val openButton = Image(Texture("open.png"))
    val attackButton = Image(Texture("attackButton.png"))


    init {

        if ((width / height) == (16 / 10f)) stage = Stage(StretchViewport(800f, 480f)) else
        if (width / height == 4 / 3f) stage = Stage(StretchViewport(800f, 600f)) else
        if (width / height == 16 / 9f) stage = Stage(StretchViewport(854f, 480f)) else
        if (width / height == 3 / 2f) stage = Stage(StretchViewport(960f, 640f)) else
        stage = Stage(StretchViewport(800f, 480f))


        val skin = Skin()
        skin.add("touchBackground", Texture("touchBackground.png"))
        val knob = Sprite(Texture("touchKnob.png"))
        knob.setSize(stage.width/20, stage.width/20)
        skin.add("touchKnob", knob)

        val touchpadStyle = Touchpad.TouchpadStyle()
        touchpadStyle.background = skin.getDrawable("touchBackground")
        touchpadStyle.knob = skin.getDrawable("touchKnob")

        touchpad = Touchpad(6f, touchpadStyle)
        touchpad.setBounds(0f, 0f, stage.width/9, stage.width/9)
        stage.addActor(touchpad)

        currentRoom.setPosition(10f, 10f)
        currentRoom.setSize(stage.width/10, stage.height/10)
        stage.addActor(currentRoom)

        hp.setPosition(3f, stage.height - hp.height - 20)
        hp.setSize(stage.width / 10, stage.height / 10)
        stage.addActor(hp)

        fps.setPosition(hp.x, hp.y - 15)
        stage.addActor(fps)

        money.setPosition(fps.x, fps.y - 15)
        stage.addActor(money)

        attackButton.setSize(stage.width / 10, stage.width / 10)
        attackButton.setPosition(stage.width - attackButton.width - 20, stage.height/10f)
        attackButton.addListener(this)
        stage.addActor(attackButton)

        openButton.setSize(stage.width / 15, stage.width / 15)
        openButton.setPosition(stage.width - openButton.width - attackButton.width * 1.5f, stage.height/10f)
        openButton.addListener(this)
        openButton.isVisible = false
        stage.addActor(openButton)

        fakeTouchDownEvent.type = InputEvent.Type.touchDown

    }

    fun update(gameScreen: GameScreen) {
        stage.draw()
        currentRoom.setText("${gameScreen.currentMap}")
        getDirection()
        fps.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        openButton.isVisible = gameScreen.worldContactListener.touchedOpeningItem

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

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        when (event!!.target) {
            attackButton -> {
                gameScreen.player.canDamage = false
                attackButton.color = Color.WHITE
            }

            openButton -> {
                openButtonPressed = false
                openButton.color = Color.WHITE
            }
        }
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        when (event!!.target) {
            attackButton -> {
                if (
                    gameScreen.player.attackAnimation.animationDuration < gameScreen.player.stateTimer ||
                    gameScreen.player.currentState != GameScreen.State.Attacking
                    )
                {
                        gameScreen.player.attacking = true
                        gameScreen.player.canDamage = true
                        attackButton.color = Color.GRAY
                        gameScreen.game.assetManager["Sounds/sword.wav", Sound::class.java].play()
                }

            }

            openButton -> {
                openButtonPressed = true
                openButton.color = Color.GRAY
            }

        }
        return true
    }
}