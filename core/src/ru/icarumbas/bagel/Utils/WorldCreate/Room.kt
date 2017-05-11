package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.DEFAULT
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.bagel.Characters.mapObjects.Box
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Utils.B2dWorldCreator.B2DWorldCreator


class Room {

    private var path: String = ""

    var map: TiledMap? = TiledMap()

    val roomLinks = intArrayOf(DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT)
    lateinit var meshVertices: IntArray

    var groundBodies = ArrayList<Body>()
    var platformBodies = ArrayList<Body>()
    var boxes = ArrayList<Box>()

    var mapWidth = 0f
    var mapHeight = 0f

    fun setAllBodiesActivity(active: Boolean) {
        setGroundActivity(active)
        setPlatformsActivity(active)
        boxes.forEach { it.body.isActive = true }
    }

    fun setPlatformsActivity(active: Boolean) = platformBodies.forEach { it.isActive = active }

    fun setGroundActivity(active: Boolean) = groundBodies.forEach { it.isActive = active }

    fun loadBodies(gameScreen: GameScreen, b2DWorldCreator: B2DWorldCreator){
        b2DWorldCreator.loadGround(map!!.layers.get("ground"), gameScreen.world, groundBodies, GROUND_BIT)
        if (map!!.layers["platform"] != null)
        b2DWorldCreator.loadGround(map!!.layers.get("platform"), gameScreen.world, platformBodies, PLATFORM_BIT)
        if (map!!.layers["boxes"] != null)
        b2DWorldCreator.loadBoxes(map!!.layers.get("boxes"), gameScreen.world, boxes)

    }

    fun loadTileMap(worldCreator: WorldCreator, path: String){
        this.path = path
        loadTileMap(worldCreator)

    }

    fun loadTileMap(worldCreator: WorldCreator){
        map = worldCreator.tmxLoader.load(path)
        mapWidth = map!!.properties["Width"].toString().toFloat()
        mapHeight = map!!.properties["Height"].toString().toFloat()
    }

    fun draw(batch: Batch) {
        boxes.forEach { it.draw(batch) }
    }

}