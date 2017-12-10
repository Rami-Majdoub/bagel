package ru.icarumbas.bagel.view.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.scene2d.label
import ktx.scene2d.table
import ru.icarumbas.Bagel

class MainMenuScreen(private val game: Bagel) : KtxScreen {

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
                game.setScreen<GameScreen>()
            }
        }

        row()

        label(text = "Continue", style = "decorative") {
            style = labelStyle
            onClick {
                game.setScreen<GameScreen>()
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
        clearScreen(1f, 1f, 1f, 1f)
        stage.draw()
    }

    override fun dispose() {
        font.dispose()
        stage.dispose()
    }

}
