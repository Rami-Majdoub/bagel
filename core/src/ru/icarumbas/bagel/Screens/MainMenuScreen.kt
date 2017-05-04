package ru.icarumbas.bagel.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.icarumbas.Bagel

class MainMenuScreen(private val game: Bagel) : ScreenAdapter() {

    private val stage = Stage(StretchViewport(1920f, 1080f))
    private var font = BitmapFont()
    lateinit private var animation: Animation<Any>
    private val batch = SpriteBatch()
    private var frame: Sprite = Sprite()
    private var back: Sprite = Sprite()
    private var stateTimer = 0f

    override fun show() {

        Gdx.input.inputProcessor = stage

        val atlas = TextureAtlas("Main_Menu.txt")
        back = Sprite(atlas.findRegion("menu_background"))
        back.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        val frames = Array<Sprite>()
        for (i in 1..4) {
            val frame = Sprite(atlas.findRegion("rain_drops-0" + i))
            frame.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            frames.add(frame)
        }
        animation = Animation(.1f, frames)
        animation.playMode = Animation.PlayMode.LOOP
        frames.clear()

        val generator = FreeTypeFontGenerator(Gdx.files.internal("dc_s.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 125

        font = generator.generateFont(parameter)

        val labelStyle = Label.LabelStyle(font, Color.CORAL)
        val table = Table()

        val newGame = Label("New Game", labelStyle)
        table.add(newGame).row()
        val continueGame = Label("Continue", labelStyle)
        table.add(continueGame).row()
        val options = Label("Options", labelStyle)
        table.add(options)

        table.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                when (event.target) {
                    newGame -> game.screen = GameScreen(game, true)
                    continueGame -> game.screen = GameScreen(game, false)
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
        stateTimer += delta
        batch.begin()
        back.draw(batch)
        frame = animation.getKeyFrame(stateTimer) as Sprite
        frame.draw(batch)
        batch.end()

        stage.draw()
    }

    override fun dispose() {
        font.dispose()
        stage.dispose()
    }
}
