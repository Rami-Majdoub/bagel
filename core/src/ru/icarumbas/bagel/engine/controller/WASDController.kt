package ru.icarumbas.bagel.engine.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import ru.icarumbas.bagel.view.ui.actors.Minimap


class WASDController(

        private val minimap: Minimap

) : PlayerMoveController, UIController, InputListener(){

    // Ugly, i don't know how to do better now. Maybe i'm not skilled enough
    private var attackBtnPressed = false
    private var openBtnPressed = false
    private var minimapPressed = false

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        when (event.keyCode) {
            Input.Keys.ENTER -> {
                attackBtnPressed = true
            }
            Input.Keys.Q -> {
                openBtnPressed = true
            }
            Input.Keys.E -> {
                minimapPressed = true
                minimap.onUp()
            }

        }
        return true
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (event.keyCode) {
            Input.Keys.ENTER -> {
                attackBtnPressed = false
            }
            Input.Keys.Q -> {
                openBtnPressed = false
            }
            Input.Keys.E -> {
                minimapPressed = false
                minimap.onDown()
            }

        }

        return true
    }

    override fun isUpPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE)
    }

    override fun isDownPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.S)
    }

    override fun isLeftPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.A)
    }

    override fun isRightPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.D)
    }

    override fun isAttackPressed() = attackBtnPressed

    override fun isOpenPressed() = openBtnPressed

    override fun isMinimapPressed() = minimapPressed
}