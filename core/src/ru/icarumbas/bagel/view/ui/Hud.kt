package ru.icarumbas.bagel.view.ui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
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
import ru.icarumbas.bagel.engine.controller.HudInputListener
import ru.icarumbas.bagel.engine.controller.PlayerMoveController
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.body
import ru.icarumbas.bagel.view.ui.actors.AdvancedTouchpad
import ru.icarumbas.bagel.view.ui.actors.HpBar
import ru.icarumbas.bagel.view.ui.actors.Minimap
import ru.icarumbas.bagel.view.ui.actors.RegularBar


class Hud(

        assets: ResourceManager,
        private val worldState: RoomWorld,
        private val player: Entity

){

    val stage = Stage(ExtendViewport(800f, 480f))

    private val uiAtlas = assets.getTextureAtlas("Packs/UI.pack")

    private lateinit var fps: Label
    private lateinit var currentRoom: Label

    val minimap: Minimap
    private val hp: HpBar
    private val mana: RegularBar


    init {
        minimap = createMinimap()
        hp = createHpBar()
        mana = createManaBar()
        createDebuggingStaff(stage)
    }

    fun draw(dt: Float) {
        update(dt)
        stage.draw()
    }

    fun update(dt: Float) {

        stage.act()
        hp.act(dt)

        // Debugging temporary staff
        currentRoom.setText("Current room: ${worldState.currentMapId}")
        fps.setText("FPS: ${Gdx.graphics.framesPerSecond}")
    }

    fun dispose(){
        stage.dispose()
    }

    fun createOnScreenPlayerMoveController(): PlayerMoveController{
        return createTouchpad().also {
            stage.addActor(it)
        }
    }

    fun createUIController(): HudInputListener {
        return HudInputListener(
                openBtn = createOpenButton(),
                attackBtn = createAttackButton(),
                minimap = minimap
        )

    }

    private fun createDebuggingStaff(stage: Stage){
        val lStyle = Label.LabelStyle(BitmapFont(), Color.RED)

        fps = Label("FPS: ", lStyle).apply {
            setPosition(3f, mana.y - 15)
        }.also {
            stage.addActor(it)
        }

        currentRoom = Label("Current room: ", lStyle).apply {
            setPosition(10f, 10f)
            setSize(80f, 48f)
        }.also {
            stage.addActor(it)
        }

    }

    private fun createOpenButton(): Image {
        return Image(uiAtlas.findRegion("open"))
                .also {
                    stage.addActor(it)
                }
                .apply {
            setSize(60f, 60f)
            setPosition(stage.width - 180, 30f)
            isVisible = false
        }
    }

    private fun createAttackButton(): Image {
        return Image(uiAtlas.findRegion("attackButton"))
                .also {
                    stage.addActor(it)
                }
                .apply {
            setSize(80f, 80f)
            setPosition(stage.width - 100, 30f)
        }
    }

    private fun createTouchpad(): AdvancedTouchpad {
        return AdvancedTouchpad(.6f, Touchpad.TouchpadStyle().apply {
            background = SpriteDrawable(Sprite(uiAtlas.findRegion("touchBackground")).apply {
                setSize(100f, 100f)
            })

            knob = SpriteDrawable(Sprite(uiAtlas.findRegion("touchKnob")).apply {
                setSize(60f, 60f)
            })
        })
    }

    private fun createMinimap(): Minimap {

        return Minimap(
                Window.WindowStyle(
                        BitmapFont(),
                        Color.BLACK,
                        TextureRegionDrawable(TextureRegion(uiAtlas.findRegion("Empty")))
                ),
                Image(uiAtlas.findRegion("Point")),
                worldState)
                .also {
                    stage.addActor(it)
                    stage.addActor(it.playerPoint)
                }
                .apply {
                    setPlayerPositionRelativeTo(body[player].body.position)

                    addListener(object : InputListener(){
                        override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                            minimap.onUp()
                        }

                        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                            minimap.onDown()
                            return true
                        }
                    })

                    setSize(stage.width / 4, stage.height / 4)

                    setPosition(
                            stage.width - width,
                            stage.height - height)

                    add(playerPoint)
                }

    }

    private fun createHpBar(): HpBar{
        return HpBar(
                Image(uiAtlas.findRegion("barBackground")).also { stage.addActor(it) },
                Image(uiAtlas.findRegion("healthBar")).also { stage.addActor(it) },
                Image(uiAtlas.findRegion("barForeground")).also { stage.addActor(it) },
                player
        )
                .also {
                    stage.addActor(it)
                }
                .apply {

            setSize(stage.width / 5, stage.height / 10)
            setPosition(0f, stage.height - stage.height / 10)
        }
    }

    private fun createManaBar(): RegularBar{
        return RegularBar(
                Image(uiAtlas.findRegion("barBackground")).also { stage.addActor(it) },
                Image(uiAtlas.findRegion("manaBar")).also { stage.addActor(it) },
                Image(uiAtlas.findRegion("barForeground")).also { stage.addActor(it) }
        )
                .also {
                    stage.addActor(it)
                }
                .apply {

            setSize(stage.width / 5, stage.height / 10)
            setPosition(0f, stage.height - hp.height - stage.height / 10)
        }
    }
}