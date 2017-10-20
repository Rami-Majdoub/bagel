package ru.icarumbas.bagel.screens.scenes

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import java.util.*


class Minimap {

    private val regularMapWidth = 18
    private val regularMapHeight = 12

    private lateinit var currentRoomActor: Actor

    private val stage: Stage
    private val playerEntity: Entity
    private val assets: AssetManager
    private val rm: RoomManager
    private val mesh: Array<IntArray>
    private val distance = Vector2()

    val minimapFrame: Window
    val playerPointOnMap: Image


    constructor(stage: Stage, mesh: Array<IntArray>, rm: RoomManager, assets: AssetManager, playerEntity: Entity) {

        this.stage = stage
        this.rm = rm
        this.assets = assets
        this.playerEntity = playerEntity
        this.mesh = mesh


        minimapFrame = Window("", Window.WindowStyle(
                BitmapFont(),
                Color.BLACK,
                TextureRegionDrawable(TextureRegion(Texture("Empty.png")))))


        with (minimapFrame) {
            setSize(stage.width / 4, stage.height / 4)
            setPosition(stage.width - minimapFrame.width, stage.height - minimapFrame.height)
        }


        createMinimap()

        stage.addActor(minimapFrame)

        playerPointOnMap = Image(Texture("Point.png"))
        playerPointOnMap.setPosition(minimapFrame.x + minimapFrame.width / 2, minimapFrame.y + minimapFrame.height / 2)

        stage.addActor(playerPointOnMap)

    }

    fun update() {
        minimapFrame.children.filter { it is AdvancedImage }.forEach {
            if (isCurrentRoomActor(it as AdvancedImage)) {
                it.color = Color.BLUE
                it.isVisible = true
                currentRoomActor = it
            } else {
                it.color = Color.WHITE
            }
        }

        followPlayer()


        // Move player point un top of rendering
        if (stage.actors.last() !== playerPointOnMap) {
            stage.actors.swap(stage.actors.indexOf(playerPointOnMap), stage.actors.size-1)
        }
    }

    private fun isCurrentRoomActor(actor: AdvancedImage): Boolean {

        return ( rm.rooms[rm.currentMapId].meshCoords[0] == (MathUtils.round(actor.x - actor.distanceX) / regularMapWidth) &&
                 rm.rooms[rm.currentMapId].meshCoords[1] == (50 - MathUtils.round(actor.y - actor.distanceY) / regularMapHeight) )
    }

    private fun followPlayer(){
        distance.set(minimapFrame.originX + minimapFrame.width/2 - currentRoomActor.x, minimapFrame.originY + minimapFrame.height/2 - currentRoomActor.y)
        distance.add(-body[playerEntity].body.position.x, -body[playerEntity].body.position.y)
        distance.add(-playerPointOnMap.width/2, -playerPointOnMap.height/2)

        minimapFrame.children.filter { it is AdvancedImage }.forEach {
            it.moveBy(distance.x, distance.y)
            (it as AdvancedImage).distanceX += distance.x
            (it as AdvancedImage).distanceY += distance.y
        }
    }

    private fun findTextureForMiniMap(name: String): TextureRegion {
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

    private fun AdvancedImage.setRegularPositionOnMinimap(x: Int, y: IntArray){
        setPosition(regularMapWidth.toFloat() * x, 600 - regularMapHeight.toFloat() * mesh.indexOf(y))
    }

    private fun createMinimap(){

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
                                minimapFrame.addActor(AdvancedImage(findTextureForMiniMap(name))
                                        .apply {
                                            setSize(regularMapWidth * 2f, regularMapHeight * 2f)
                                            setRegularPositionOnMinimap(x, y)
                                            isVisible = false
                                        })
                            }
                            room.width != 1152 / PIX_PER_M -> {
                                minimapFrame.addActor(AdvancedImage(findTextureForMiniMap(name))
                                        .apply {
                                            setSize(regularMapWidth * 2f, regularMapHeight.toFloat())
                                            setRegularPositionOnMinimap(x, y)
                                            isVisible = false
                                        })
                            }
                            room.height != 768 / PIX_PER_M -> {
                                minimapFrame.addActor(AdvancedImage(findTextureForMiniMap(name))
                                        .apply {
                                            setSize(regularMapWidth.toFloat(), regularMapHeight * 2f)
                                            setRegularPositionOnMinimap(x, y)
                                            isVisible = false
                                        })
                            }
                            else -> {
                                minimapFrame.addActor(AdvancedImage(findTextureForMiniMap(name))
                                        .apply {
                                            setSize(regularMapWidth.toFloat(), regularMapHeight.toFloat())
                                            setRegularPositionOnMinimap(x, y)
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

    private class AdvancedImage(texture: TextureRegion) : Image(texture){
        var distanceX = 0f
        var distanceY = 0f
    }
}