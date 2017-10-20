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

    private val minimap: Dialog
    private val stage: Stage
    private val assets: AssetManager
    private val rm: RoomManager
    private val mesh: Array<IntArray>
    private val playerEntity: Entity
    private lateinit var currentRoomActor: Actor

    private val distance = Vector2()

    private val playerPointOnMap: Image

    constructor(stage: Stage, mesh: Array<IntArray>, rm: RoomManager, assets: AssetManager, playerEntity: Entity) {

        this.stage = stage
        this.rm = rm
        this.assets = assets
        this.playerEntity = playerEntity
        this.mesh = mesh


        minimap = Dialog("", Window.WindowStyle(
                BitmapFont(),
                Color.BLACK,
                TextureRegionDrawable(TextureRegion(Texture("Empty.png")))))

        minimap.setSize(stage.width / 4, stage.height / 4)
        minimap.setPosition(stage.width - minimap.width, stage.height - minimap.height)

        createMinimap()

        stage.addActor(minimap)

        playerPointOnMap = Image(Texture("Point.png"))
        playerPointOnMap.setPosition(minimap.x + minimap.width / 2, minimap.y + minimap.height / 2)

        stage.addActor(playerPointOnMap)

    }

    fun update() {
        minimap.children.filter { it is AdvancedImage }.forEach {
            if (isCurrentRoomActor(it as AdvancedImage)) {
                it.color = Color.BLUE
                currentRoomActor = it
            } else {
                it.color = Color.WHITE
            }
        }

        followPlayer()

        if (stage.actors.last() !== playerPointOnMap) {
            stage.actors.swap(stage.actors.indexOf(playerPointOnMap), stage.actors.size-1)
        }

    }

    private fun isCurrentRoomActor(actor: AdvancedImage): Boolean {

        return ( rm.rooms[rm.currentMapId].meshCoords[0] == (MathUtils.round(actor.x - actor.distanceX) / 18) &&
                 rm.rooms[rm.currentMapId].meshCoords[1] == (50 - MathUtils.round(actor.y - actor.distanceY) / 12) )
    }

    private fun followPlayer(){
        distance.set(minimap.originX + minimap.width/2 - currentRoomActor.x, minimap.originY + minimap.height/2 - currentRoomActor.y)
        distance.add(-body[playerEntity].body.position.x, -body[playerEntity].body.position.y)
        distance.add(-playerPointOnMap.width/2, -playerPointOnMap.height/2)

        minimap.children.filter { it is AdvancedImage }.forEach {
            it.moveBy(distance.x, distance.y)
            (it as AdvancedImage).distanceX += distance.x
            (it as AdvancedImage).distanceY += distance.y
        }
    }

    private fun findSpriteForMiniMap(name: String): Sprite {
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
                                minimap.addActor(AdvancedImage(findSpriteForMiniMap(name))
                                        .apply {
                                            setSize(36f, 24f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                            room.width != 1152 / PIX_PER_M -> {
                                minimap.addActor(AdvancedImage(findSpriteForMiniMap(name))
                                        .apply {
                                            setSize(36f, 12f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                            room.height != 768 / PIX_PER_M -> {
                                minimap.addActor(AdvancedImage(findSpriteForMiniMap(name))
                                        .apply {
                                            setSize(18f, 24f)
                                            setPosition(18f * x, 600 - 12f * mesh.indexOf(y))
                                        })
                            }
                            else -> {
                                minimap.addActor(AdvancedImage(findSpriteForMiniMap(name))
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

    private class AdvancedImage(texture: TextureRegion) : Image(texture){
        var distanceX = 0f
        var distanceY = 0f
    }
}