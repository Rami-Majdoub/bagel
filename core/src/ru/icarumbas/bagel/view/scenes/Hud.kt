package ru.icarumbas.bagel.view.scenes

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ru.icarumbas.bagel.engine.world.RoomWorldState
import ru.icarumbas.bagel.view.actors.AdvancedTouchpad
import ru.icarumbas.bagel.view.actors.Minimap
import ru.icarumbas.bagel.view.actors.RegularBar


class Hud(assetManager: AssetManager,
          private val worldState: RoomWorldState,
          private val player: Entity) : InputListener(){

    val stage = Stage(ExtendViewport(800f, 480f))
    private val uiAtlas = assetManager.get("Packs/UI.pack", TextureAtlas::class.java)

    private val hp: RegularBar
    private val mana: RegularBar

    private val fps: Label
    private val currentRoom: Label

    private val openButton: Image
    private val attackButton: Image

    val minimap: Minimap
    val touchpad: AdvancedTouchpad


    init {

        val lStyle = Label.LabelStyle(BitmapFont(), Color.RED)


        hp = RegularBar(
                Image(uiAtlas.findRegion("healthBar")),
                Image(uiAtlas.findRegion("barForeground")),
                Image(uiAtlas.findRegion("barBackground")),
                stage).apply {

            setSize(stage.width / 5, stage.height / 10)
            setPosition(0f, stage.height - stage.height / 10)
        }


        mana = RegularBar(
                Image(uiAtlas.findRegion("manaBar")),
                Image(uiAtlas.findRegion("barForeground")),
                Image(uiAtlas.findRegion("barBackground")),
                stage).apply {

            setSize(stage.width / 5, stage.height / 10)
            setPosition(0f, stage.height - hp.height - stage.height / 10)
        }


        fps = Label("FPS: ", lStyle).apply {
            setPosition(3f, mana.posY - 15)
        }
        stage.addActor(fps)


        currentRoom = Label("Current room: ", lStyle).apply {
            setPosition(10f, 10f)
            setSize(stage.width/10, stage.height/10)
        }
        stage.addActor(currentRoom)


        attackButton = Image(uiAtlas.findRegion("open")).apply {
            setSize(stage.width / 10, stage.width / 10)
            setPosition(stage.width - stage.width / 10 * 1.5f, stage.width / 10 * .5f)
        }
        stage.addActor(attackButton)


        openButton = Image(uiAtlas.findRegion("attackButton")).apply {
            setSize(stage.width / 15, stage.width / 15)
            setPosition(attackButton.x - stage.width / 15, attackButton.y)
            isVisible = false
        }
        stage.addActor(openButton)


        minimap = Minimap(
                Window.WindowStyle(
                        BitmapFont(),
                        Color.BLACK,
                        TextureRegionDrawable(TextureRegion(uiAtlas.findRegion("Empty")))
                ),
                Image(uiAtlas.findRegion("Point")),
                body[player].body.position,
                worldState)


        touchpad = AdvancedTouchpad(.6f, Touchpad.TouchpadStyle().apply {
            background = SpriteDrawable(Sprite(uiAtlas.findRegion("touchBackground")).apply {
                setSize(20f, 20f)
            })

            knob = SpriteDrawable(Sprite(uiAtlas.findRegion("touchKnob")).apply {
                setSize(40f, 40f)
            })
        })


    }

    fun draw() {
        stage.draw()
    }

    fun update() {

        currentRoom.setText("Current room: ${worldState.getCurrentMapId()}")
        fps.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        hp.setValue(damage[player].HP / 100f)
    }

    fun dispose(){
        stage.dispose()
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
        when (event.target) {
            attackButton -> {
                attackButton.color = Color.WHITE
            }

            openButton -> {
                openButton.color = Color.WHITE
            }

            minimap -> {
                minimap.onUp()
            }
        }
    }

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        when (event.target) {

            attackButton -> {
                attackButton.color = Color.GRAY
            }

            openButton -> {
                openButton.color = Color.GRAY
            }

            minimap -> {
                minimap.onDown()
            }

        }
        return true
    }
}