package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.table
import ru.icarumbas.Bagel

class MainMenuScreen(

        private val game: Bagel

) : ScreenAdapter() {

    private val stage = Stage(StretchViewport(1920f, 1080f))

    private var font = FreeTypeFontGenerator(Gdx.files.internal("CastlePressNo2.ttf")).generateFont(
            FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                size = 150
                spaceX = 10
            }
    )

    private val labelStyle = Label.LabelStyle(font, Color.WHITE)

    val view = table {

        label(text = "New Game", style = "decorative") {
            style = labelStyle
            onClick {
                game.screen = GameScreen(game.assets, game, isNewGame = true)
            }
        }

        row()

        label(text = "Continue", style = "decorative") {
            style = labelStyle
            onClick {
                game.screen = GameScreen(game.assets, game, isNewGame = false)
            }
        }

        center()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun show() {
        stage.addActor(view)
    }

    override fun render(delta: Float) {
        stage.draw()
    }

    override fun dispose() {
        font.dispose()
        stage.dispose()
    }

}
