package ru.icarumbas.bagel.view.ui.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ru.icarumbas.bagel.engine.io.MinimapInfo
import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.resources.ResourceManager
import ru.icarumbas.bagel.engine.world.PIX_PER_M
import ru.icarumbas.bagel.engine.world.RoomWorld
import java.util.*
import kotlin.collections.ArrayList


class Minimap (

        style: WindowStyle,
        var playerPoint: Image,
        private val worldState: RoomWorld

) : Window("", style) {

    // Room image on minimap size
    private var regRoomWidth = 18f
    private var regRoomHeight = 12f

    // Distance that all getRooms have to pass depends on currentRoom
    private val distance = Vector2()

    val currentRoomColor: Color = Color.BLUE

    // Room where player is
    private lateinit var currentRoom: Actor

    private var playerPosition = Vector2.Zero


    override fun setPosition(x: Float, y: Float) {
        playerPoint.setPosition(x + width / 2 - playerPoint.width / 2, y + height / 2 - playerPoint.height / 2)
        super.setPosition(x, y)
    }

    override fun setSize(width: Float, height: Float) {
        playerPoint.setPosition(x + width / 2 - playerPoint.width / 2, y + height / 2 - playerPoint.height / 2)
        super.setSize(width, height)
    }

    override fun act(delta: Float) {
        super.act(delta)

        children.filterIsInstance(MinimapRoomImage::class.java).forEach {

            if (isCurrentRoomActor(it)) {
                it.color = currentRoomColor
                it.isVisible = true
                currentRoom = it

            } else {
                it.color = Color.WHITE
            }
        }

        followCurrentRoom()


        with (children) {
            if (last() !== playerPoint) {
                swap(indexOf(playerPoint), size-1)
            }
        }


    }

    fun setPlayerPositionRelativeTo(position: Vector2) {
        playerPosition = position
    }

    fun createRooms(mesh: Array<IntArray>, assets: ResourceManager){



        mesh.forEach { y ->
            var x = 0
            while (x < y.size) {
                if (y[x] == 1) {
                    val room = worldState.getRoomForMeshCoordinate(x, mesh.indexOf(y))

                    if (room != null) {

                        var name = ""
                        assets.getTiledMap(room.path).properties.keys.forEach {
                            if (it[0].isUpperCase()) {
                                name += if (it == "Width" || it == "Height"){
                                    assets.getTiledMap(room.path).properties[it]
                                } else {
                                    it
                                }
                            }
                        }

                        fun addRoom(size: Pair<Float, Float>){
                            addActor(MinimapRoomImage(findTextureForMiniMap(name, assets))
                                    .apply {
                                        setSize(size.first, size.second)
                                        setRegularPositionOnMinimap(x, y, mesh)
                                        isVisible = false
                                    })
                        }

                        when {
                            room.height != 768 / PIX_PER_M && room.width != 1152 / PIX_PER_M -> {
                                addRoom(regRoomWidth * 2f to regRoomHeight * 2f)
                            }
                            room.width != 1152 / PIX_PER_M -> {
                                addRoom(regRoomWidth * 2f to regRoomHeight)
                            }
                            room.height != 768 / PIX_PER_M -> {
                                addRoom(regRoomWidth to regRoomHeight * 2f)
                            }
                            else -> {
                                addRoom(regRoomWidth to regRoomHeight)
                            }
                        }
                    }
                }
                x++
            }
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

    fun save(worldIO: WorldIO){

        val indexes = ArrayList<Int>()

        children.forEachIndexed { index, room ->
            if (room.isVisible) {
                indexes.add(index)
            }
        }

        worldIO.saveInfo(MinimapInfo(indexes))
    }

    fun load(worldIO: WorldIO, assets: ResourceManager){
        createRooms(worldIO.loadWorldInfo().mesh, assets)
        worldIO.loadMinimapInfo().openedRooms.forEach {
            children[it].isVisible = true
        }
    }

    private fun isCurrentRoomActor(actor: MinimapRoomImage): Boolean {

        return ( worldState.getRoomMeshCoordinate(0) == (MathUtils.round(actor.x - actor.distanceX) / regRoomWidth.toInt()) &&
                 worldState.getRoomMeshCoordinate(1) == (50 - MathUtils.round(actor.y - actor.distanceY) / regRoomHeight.toInt()) )
    }

    private fun followCurrentRoom(){
        with (distance) {
            set(originX + width / 2 - currentRoom.x, originY + height / 2 - currentRoom.y)
            add(-playerPosition.x, -playerPosition.y)
            add(-playerPoint.width, -playerPoint.height / 2)
        }

        children.filterIsInstance(MinimapRoomImage::class.java).forEach {
            it.moveBy(distance.x, distance.y)
            it.distanceX += distance.x
            it.distanceY += distance.y
        }
    }

    private fun findTextureForMiniMap(name: String, assets: ResourceManager): TextureRegion {
        assets.getTextureAtlas("Packs/minimap.pack").regions.forEach {

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

    private inner class MinimapRoomImage(texture: TextureRegion) : Image(texture){

        fun setRegularPositionOnMinimap(x: Int, y: IntArray, mesh: Array<IntArray>){
            setPosition(regRoomWidth * x, 600 - regRoomHeight * mesh.indexOf(y))
        }

        var distanceX = 0f
        var distanceY = 0f
    }
}