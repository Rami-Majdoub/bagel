package ru.icarumbas.bagel.screens.scenes.widget

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.RoomWorldState
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import java.util.*


class Minimap : Window {


    private var regRoomWidth = 1f
    private var regRoomHeight = 1f

    private val distance = Vector2()

    val currentRoomColor: Color = Color.BLUE

    private lateinit var currentRoomActor: Actor
    private var playerPoint: Image



    constructor(back: WindowStyle, playerPoint: Image) : super("", back){
        this.playerPoint = playerPoint
        addActor(playerPoint)
    }

    override fun setPosition(x: Float, y: Float) {
        playerPoint.setPosition(x + width / 2 - playerPoint.width / 2, y + height / 2 - playerPoint.height / 2)
        super.setPosition(x, y)
    }

    override fun setSize(width: Float, height: Float) {
        playerPoint.setPosition(x + width / 2 - playerPoint.width / 2, y + height / 2 - playerPoint.height / 2)
        super.setSize(width, height)
    }

    override fun act(delta: Float) {

        children.filterIsInstance(MinimapRoomImage::class.java).forEach {
            if (isCurrentRoomActor(it)) {
                it.color = currentRoomColor
                it.isVisible = true
                currentRoomActor = it
            } else {
                it.color = Color.WHITE
            }
        }

        followPlayer()

        // Move player point un top of rendering
        playerPoint.let {
            if (children.last() !== it) {
                stage.actors.swap(children.indexOf(playerPoint), stage.actors.size-1)
            }
        }

        super.act(delta)
    }

    private fun isCurrentRoomActor(actor: MinimapRoomImage, worldState: RoomWorldState): Boolean {

        return ( worldState.roomPass(0) == (MathUtils.round(actor.x - actor.distanceX) / regRoomWidth.toInt()) &&
                 worldState.roomPass(1) == (50 - MathUtils.round(actor.y - actor.distanceY) / regRoomHeight.toInt()) )
    }

    private fun followPlayer(){
        distance.set(originX + width/2 - currentRoomActor.x, originY + height/2 - currentRoomActor.y)
        distance.add(-body[playerEntity].body.position.x, -body[playerEntity].body.position.y)

        children.filterIsInstance(MinimapRoomImage::class.java).forEach {
            it.moveBy(distance.x, distance.y)
            it.distanceX += distance.x
            it.distanceY += distance.y
        }
    }

    private fun findTextureForMiniMap(name: String, assets: AssetManager): TextureRegion {
        assets["Packs/minimap.pack", TextureAtlas::class.java].regions.forEach {

            val arr1 = it.name.toCharArray()
            Arrays.sort(arr1)

            val arr2 = name.toCharArray()
            Arrays.sort(arr2)

            if (Arrays.equals(arr1, arr2)) {
                return Sprite(it)
            }
        }
        throw Exception("Cant't find sprite for $name")
    }

    fun createRooms(mesh: Array<IntArray>, assets: AssetManager, worldState: RoomWorldState){

        mesh.forEach { y ->
            var x = 0
            while (x < y.size) {
                if (y[x] == 1) {
                    val room = worldState.roomFor(x, mesh.indexOf(y))

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
                                addActor(MinimapRoomImage(findTextureForMiniMap(name, assets))
                                        .apply {
                                            setSize(regRoomWidth * 2f, regRoomHeight * 2f)
                                            setRegularPositionOnMinimap(x, y, mesh)
                                            isVisible = false
                                        })
                            }
                            room.width != 1152 / PIX_PER_M -> {
                                addActor(MinimapRoomImage(findTextureForMiniMap(name, assets))
                                        .apply {
                                            setSize(regRoomWidth * 2f, regRoomHeight)
                                            setRegularPositionOnMinimap(x, y, mesh)
                                            isVisible = false
                                        })
                            }
                            room.height != 768 / PIX_PER_M -> {
                                addActor(MinimapRoomImage(findTextureForMiniMap(name, assets))
                                        .apply {
                                            setSize(regRoomWidth, regRoomHeight * 2f)
                                            setRegularPositionOnMinimap(x, y, mesh)
                                            isVisible = false
                                        })
                            }
                            else -> {
                                addActor(MinimapRoomImage(findTextureForMiniMap(name, assets))
                                        .apply {
                                            setSize(regRoomWidth, regRoomHeight)
                                            setRegularPositionOnMinimap(x, y, mesh)
                                            isVisible = false
                                        })
                            }
                        }
                    }
                }
                x++
            }
        }

    }

    fun loadRooms(visibleRooms: ArrayList<Int>, mesh: Array<IntArray>, assets: AssetManager, worldState: RoomWorldState){
        createRooms(mesh, assets, worldState)
        visibleRooms.forEach {
            children[it].isVisible = true
        }
    }


    fun onUp(){
        setSize(stage.width / 4, stage.height / 4)

        setPosition(
                stage.width - width,
                stage.height - height)

    }

    fun onDown(){
        setSize(stage.width, stage.height)
        setPosition(0f, 0f)
    }


    private inner class MinimapRoomImage(texture: TextureRegion) : Image(texture){

        fun setRegularPositionOnMinimap(x: Int, y: IntArray, mesh: Array<IntArray>){
            setPosition(regRoomWidth * x, 600 - regRoomHeight * mesh.indexOf(y))
        }

        var distanceX = 0f
        var distanceY = 0f
    }
}