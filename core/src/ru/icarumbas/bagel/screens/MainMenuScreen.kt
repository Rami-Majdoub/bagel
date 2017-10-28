package ru.icarumbas.bagel.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
    private var font = BitmapFont()

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun show() {

        Gdx.input.inputProcessor = stage

        val generator = FreeTypeFontGenerator(Gdx.files.internal("CastlePressNo2.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 150
        parameter.spaceX = 10

        font = generator.generateFont(parameter)

        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        val table = Table()

        val newGame = Label("New Game", labelStyle)
        table.add(newGame).row()

        if (game.worldIO.prefs.getString("Continue") == "Yes") {
            val continueGame = Label("Continue", labelStyle)
            table.add(continueGame).row()
        }

        val options = Label("Options", labelStyle)
        table.add(options)

        table.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                when (event.target) {
                    newGame -> game.screen = GameScreen(newWorld = true, game = game)
                    options -> TODO()
                    else -> game.screen = GameScreen(newWorld = false, game = game)
                }
            }

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                event.target.color = Color.CORAL
                return true
            }
        })

        table.setPosition(stage.width / 2 - table.width / 2, stage.height / 2)
        stage.addActor(table)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,  0f,  0f,  1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        font.dispose()
        stage.dispose()
    }

}
