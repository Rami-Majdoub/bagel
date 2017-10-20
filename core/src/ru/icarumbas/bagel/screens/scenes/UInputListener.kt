package ru.icarumbas.bagel.screens.scenes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage


class UInputListener(private val stage: Stage,
                     private val hud: Hud,
                     private val minimap: Minimap): InputListener() {


    var openButtonPressed = false
    var attackButtonPressed = false


    init {
        hud.attackButton.addListener(this)
        hud.openButton.addListener(this)
        minimap.minimapFrame.addListener(this)
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

            minimap.minimapFrame -> {

                minimap.minimapFrame.setSize(stage.width / 4, stage.height / 4)
                minimap.minimapFrame.setPosition(
                        stage.width - minimap.minimapFrame.width,
                        stage.height - minimap.minimapFrame.height)
                minimap.playerPointOnMap.setPosition(
                        minimap.minimapFrame.x + minimap.minimapFrame.width / 2,
                        minimap.minimapFrame.y + minimap.minimapFrame.height / 2)
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

            minimap.minimapFrame -> {

                minimap.minimapFrame.setSize(stage.width, stage.height)
                minimap.minimapFrame.setPosition(0f, 0f)
                minimap.playerPointOnMap.setPosition(
                        stage.width / 2 + minimap.playerPointOnMap.width / 2,
                        stage.height / 2 + minimap.playerPointOnMap.height / 2)
            }

        }
        return true
    }
}