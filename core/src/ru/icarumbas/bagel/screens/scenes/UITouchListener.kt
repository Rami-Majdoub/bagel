package ru.icarumbas.bagel.screens.scenes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import ru.icarumbas.bagel.view.scenes.Hud


class UITouchListener(private val hud: Hud): InputListener(), UIController {


    private var openButtonPressed = false
    private var attackButtonPressed = false
    private var minimapPressed = false


    init {
        hud.attackButton.addListener(this)
        hud.openButton.addListener(this)
        hud.minimap.minimapFrame.addListener(this)
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
        when (event.target) {
            hud.attackButton -> {
                attackButtonPressed = false
                hud.attackButton.color = Color.WHITE
            }

            hud.openButton -> {
                openButtonPressed = false
                hud.openButton.color = Color.WHITE
            }

            hud.minimap.minimapFrame -> {
                hud.minimap.onUp()
                minimapPressed = false
            }
        }
    }

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        when (event.target) {

            hud.attackButton -> {
                attackButtonPressed = true
                hud.attackButton.color = Color.GRAY
            }

            hud.openButton -> {
                openButtonPressed = true
                hud.openButton.color = Color.GRAY
            }

            hud.minimap.minimapFrame -> {
                hud.minimap.onDown()
                minimapPressed = true
            }

        }
        return true
    }

    override fun attackPressed(): Boolean {
        return attackButtonPressed
    }

    override fun openPressed(): Boolean {
        return openButtonPressed
    }

    override fun minimapPressed(): Boolean {
        return minimapPressed
    }
}