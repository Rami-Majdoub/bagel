package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.icarumbas.Bagel

class MainMenuScreen(private val game: Bagel) : ScreenAdapter() {

    private val stage = Stage(StretchViewport(1920f, 1080f))

    private var font = FreeTypeFontGenerator(Gdx.files.internal("CastlePressNo2.ttf"))
            .generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                size = 150
                spaceX = 10
            })

    override fun show() {

        Gdx.input.inputProcessor = stage

        val labelStyle = Label.LabelStyle(font, Color.WHITE)

        Table()
                .also {
                    stage.addActor(it)
                }
                .apply {

            val newGame = Label("New Game", labelStyle).also {
                add(it).row()
            }

            val continueGame = Label("Continue", labelStyle).also {
                if (game.worldIO.loadRoomInfo().canContinue) {
                    add(it).row()
                }

            }

            addListener(object : InputListener() {
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    when (event.target) {
                        newGame -> game.screen = GameScreen(game.assets, game.worldIO, game, isNewGame = true)
                        continueGame -> game.screen = GameScreen(game.assets, game.worldIO, game, isNewGame = false)
                    }
                }

                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    event.target.color = Color.CORAL
                    return true
                }
            })

            setPosition(stage.width / 2 - width / 2, stage.height / 2)
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,  0f,  0f,  1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        font.dispose()
        stage.dispose()
    }

}