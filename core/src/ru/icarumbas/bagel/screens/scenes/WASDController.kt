package ru.icarumbas.bagel.screens.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import ru.icarumbas.bagel.view.scenes.Hud


class WASDController(private val hud: Hud) : PlayerController, UIController, InputListener(){

    private var upPressed = false
    private var downPressed = false
    private var leftPressed = false
    private var rightPressed = false
    private var attackPressed = false
    private var openPressed = false
    private var minimapPressed = false


    override fun keyUp(event: InputEvent, keycode: Int): Boolean {

        when (keycode) {
            Input.Keys.W -> {
                upPressed = false
            }

            Input.Keys.S -> {
                downPressed = false
            }

            Input.Keys.A -> {
                leftPressed = false
            }

            Input.Keys.D -> {
                rightPressed = false
            }

            Input.Keys.Q -> {
                hud.openButton.color = Color.WHITE
                openPressed = false
            }

            Input.Keys.E -> {
                hud.minimap.onDown()
                minimapPressed = false
            }

            Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> {
                hud.attackButton.color = Color.WHITE
                attackPressed = false
            }
        }
        return true
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {

        when (keycode) {
            Input.Keys.W -> {
                upPressed = true
            }

            Input.Keys.S -> {
                downPressed = true
            }

            Input.Keys.A -> {
                leftPressed = true
            }

            Input.Keys.D -> {
                rightPressed = true
            }

            Input.Keys.Q -> {
                hud.openButton.color = Color.GRAY
                openPressed = true
            }

            Input.Keys.E -> {
                hud.minimap.onDown()
                minimapPressed = true
            }

            Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> {
                hud.attackButton.color = Color.GRAY
                attackPressed = true
            }
        }
        return true
    }

    override fun isUpPressed() = upPressed

    override fun isDownPressed() = downPressed

    override fun isLeftPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.A)
    }

    override fun isRightPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.D)
    }

    override fun attackPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
    }

    override fun openPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.E)
    }

    override fun minimapPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.Q)
    }
}