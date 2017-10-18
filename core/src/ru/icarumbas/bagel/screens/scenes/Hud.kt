package ru.icarumbas.bagel.screens.scenes

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.utils.Mappers.Mappers.damage
import java.util.*


class Hud(private val playerEntity: Entity): InputListener(){

    val stage: Stage
    var touchedOnce = false
    private var touchpad: Touchpad

    private val screenPos = Vector2()
    private var localPos = Vector2()
    private val fakeTouchDownEvent = InputEvent()

    // Debug
    private val currentRoom: Label
    private val hp: Label
    private val fps: Label
    private val money: Label

    private val openButton = Image(Texture("open.png"))
    private val attackButton = Image(Texture("attackButton.png"))

    var openButtonPressed = false
    var attackButtonPressed = false

    private val minimap = ArrayList<Sprite>()
    private val minimapBatch = SpriteBatch()

    init {

        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()

        stage = when {
            (width / height) == (16 / 10f) -> Stage(StretchViewport(800f, 480f))
            width / height == 4 / 3f -> Stage(StretchViewport(800f, 600f))
            width / height == 16 / 9f -> Stage(StretchViewport(854f, 480f))
            width / height == 3 / 2f -> Stage(StretchViewport(960f, 640f))
            else -> Stage(StretchViewport(800f, 480f))
        }

        val lStyle = Label.LabelStyle(BitmapFont(), Color.RED)
        hp = Label("HP:", lStyle)
        fps = Label("FPS: ", lStyle)
        money = Label("Money: ", lStyle)
        currentRoom = Label("Current room: ", lStyle)

        val skin = Skin()
        skin.add("touchBackground", Texture("touchBackground.png"))
        val knob = Sprite(Texture("touchKnob.png"))
        knob.setSize(stage.width/20, stage.width/20)
        skin.add("touchKnob", knob)

        val touchpadStyle = Touchpad.TouchpadStyle()
        touchpadStyle.background = skin.getDrawable("touchBackground")
        touchpadStyle.knob = skin.getDrawable("touchKnob")

        touchpad = Touchpad(6f, touchpadStyle)
        touchpad.setBounds(0f, 0f, stage.width/9, stage.width/9)
        stage.addActor(touchpad)

        currentRoom.setPosition(10f, 10f)
        currentRoom.setSize(stage.width/10, stage.height/10)
        stage.addActor(currentRoom)

        hp.setPosition(3f, stage.height - hp.height - 20)
        hp.setSize(stage.width / 10, stage.height / 10)
        stage.addActor(hp)

        fps.setPosition(hp.x, hp.y - 15)
        stage.addActor(fps)

        money.setPosition(fps.x, fps.y - 15)
        stage.addActor(money)

        attackButton.setSize(stage.width / 10, stage.width / 10)
        attackButton.setPosition(stage.width - attackButton.width - 20, stage.height/10f)
        attackButton.addListener(this)
        stage.addActor(attackButton)

        openButton.setSize(stage.width / 15, stage.width / 15)
        openButton.setPosition(stage.width - openButton.width - attackButton.width * 1.5f, stage.height/10f)
        openButton.addListener(this)
        openButton.isVisible = false
        stage.addActor(openButton)

        fakeTouchDownEvent.type = InputEvent.Type.touchDown

    }

    fun draw(rm: RoomManager) {
        stage.draw()
        update(rm)
        drawMinimap(rm)
    }

    private fun drawMinimap(rm: RoomManager){
        minimapBatch.begin()

        minimap.forEach {
            if (rm.rooms[rm.currentMapId].meshCoords[0] == (it.x / 18).toInt() &&
                    rm.rooms[rm.currentMapId].meshCoords[1] == (50 - it.y / 12).toInt()) {
                it.color = Color.RED
            }
            else {
                it.color = Color.WHITE
            }

            it.draw(minimapBatch)
        }

        minimapBatch.end()
    }

    private fun findSpriteForMiniMap(name: String, assets: AssetManager): Sprite{
        assets["Packs/minimap.pack", TextureAtlas::class.java].regions.forEach {

            val arr1 = it.name.toCharArray()
            Arrays.sort(arr1)

            val arr2 = name.toCharArray()
            Arrays.sort(arr2)

            println("it.name ${it.name}  name $name")
            println()

            if (Arrays.equals(arr1, arr2)) {
                return Sprite(it)
            }
        }
        throw Exception("Cant't find sprite for $name")
    }

    fun createMinimap(mesh: Array<IntArray>, rm: RoomManager, assets: AssetManager){

        mesh.forEach { y ->
            var x = 0
            while (x < y.size) {
                if (y[x] == 1) {
                    val room = rm.roomOnMeshLeftBottomCoordinate(x, mesh.indexOf(y))

                    if (room != null) {

                        var name = ""
                        assets[room.path, TiledMap::class.java].properties.keys.forEach {
                            if (it[0].isUpperCase()) {
                                name += if (it == "Width" || it == "Height"){
                                    assets[room.path, TiledMap::class.java].properties[it]
                                } else {
                                    it
                                }
                            }
                        }

                        when {
                            room.height != 768 / PIX_PER_M && room.width != 1152 / PIX_PER_M -> {
                                minimap.add(findSpriteForMiniMap(name, assets)
                                        .apply {
                                            setSize(36f, 24f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                            room.width != 1152 / PIX_PER_M -> {
                                minimap.add(findSpriteForMiniMap(name, assets)
                                        .apply {
                                            setSize(36f, 12f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                            room.height != 768 / PIX_PER_M -> {
                                minimap.add(findSpriteForMiniMap(name, assets)
                                        .apply {
                                            setSize(18f, 24f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                            else -> {
                                minimap.add(findSpriteForMiniMap(name, assets)
                                        .apply {
                                            setSize(18f, 12f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                        }
                    }
                }
                x++
            }
        }

    }

    private fun update(rm: RoomManager) {
        currentRoom.setText("${rm.currentMapId}")
        getDirection()
        fps.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        hp.setText("HP: ${damage[playerEntity].HP}")
    }

    fun setOpenButtonVisible(visible: Boolean) {
        openButton.isVisible = visible
    }

    private fun getDirection() {
        if (Gdx.input.justTouched()) {
            touchedOnce = true

            // Get the touch point in screen coordinates.
            screenPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())

            if (screenPos.x < Gdx.graphics.width / 2) {

                // Convert the touch point into local coordinates, place the touchpad and show it.
                localPos.set(screenPos)
                localPos = touchpad.parent.screenToLocalCoordinates(localPos)
                touchpad.setPosition(localPos.x - touchpad.width / 2, localPos.y - touchpad.height / 2)
                touchpad.isVisible = true

                // Fire a touch down event to get the touchpad working.
                val stagePos = touchpad.stage.screenToStageCoordinates(screenPos)
                fakeTouchDownEvent.stageX = stagePos.x
                fakeTouchDownEvent.stageY = stagePos.y
                touchpad.fire(fakeTouchDownEvent)
            }
        } else if (!Gdx.input.isTouched) {
            touchpad.isVisible = false
        }

    }

    fun isUpPressed() = touchpad.knobY > touchpad.height / 2 + touchpad.width/10

    fun isRightPressed() = touchpad.knobX > touchpad.width / 2 + touchpad.width/20

    fun isDownPressed() = touchpad.knobY < touchpad.height / 2f - touchpad.width/6f

    fun isLeftPressed() = touchpad.knobX < touchpad.width / 2 - touchpad.width/20

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        when (event!!.target) {
            attackButton -> {
                attackButtonPressed = false
                attackButton.color = Color.WHITE
            }

            openButton -> {
                openButtonPressed = false
                openButton.color = Color.WHITE
            }
        }
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        when (event!!.target) {
            attackButton -> {
                attackButtonPressed = true
                attackButton.color = Color.GRAY
            }


            openButton -> {
                openButtonPressed = true
                openButton.color = Color.GRAY
            }

        }
        return true
    }
}