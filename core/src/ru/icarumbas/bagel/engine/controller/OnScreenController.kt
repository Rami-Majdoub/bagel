package ru.icarumbas.bagel.engine.controller

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import ru.icarumbas.bagel.view.ui.actors.Minimap


class OnScreenController(

        private val attackBtn: Actor,
        private val openBtn: Actor,
        private val minimap: Minimap

) : UIController, InputListener() {


    // Ugly, i don't know how to do better now. Maybe i'm not skilled enough
    private var attackBtnPressed = false
    private var openBtnPressed = false
    private var minimapPressed = false

    override fun isAttackPressed() = attackBtnPressed

    override fun isOpenPressed() = openBtnPressed

    override fun isMinimapPressed() = minimapPressed

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
        when (event.target) {
            attackBtn -> {
                attackBtn.color = Color.WHITE
                attackBtnPressed = false
            }

            openBtn -> {
                openBtn.color = Color.WHITE
                openBtnPressed = false
            }

            minimap -> {
                minimap.onUp()
                minimapPressed = false
            }
        }
    }

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        when (event.target) {

            attackBtn -> {
                attackBtn.color = Color.GRAY
                attackBtnPressed = true
            }

            openBtn -> {
                openBtn.color = Color.GRAY
                openBtnPressed = true
            }

            minimap -> {
                minimap.onDown()
                minimapPressed = true
            }

        }
        return true
    }
}