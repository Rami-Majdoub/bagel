package ru.icarumbas.bagel

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.Bagel
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Utils.WorldCreate.WorldCreator


class Room {

    private var path: String = ""

    var map: TiledMap? = TiledMap()

    val roomLinks = intArrayOf(999, 999, 999, 999, 999, 999, 999, 999)  // Game.DEFAULT
    lateinit var meshVertices: IntArray

    var groundBodies: ArrayList<Body> = ArrayList()
    var platformBodies: ArrayList<Body> = ArrayList()

    var mapWidth = 0f
    var mapHeight = 0f

    fun setAllBodiesActivity(active: Boolean) {
        setGroundActivity(active)
        setPlatformsActivity(active)
    }

    fun setPlatformsActivity(active: Boolean) = platformBodies.forEach { it.isActive = active }

    fun setGroundActivity(active: Boolean) = groundBodies.forEach { it.isActive = active }

    fun loadBodies(worldCreator: WorldCreator, gameScreen: GameScreen){
        worldCreator.b2DWorldCreator.loadBodies(map!!.layers.get("ground"), gameScreen.world, groundBodies, GROUND_BIT)
        if (map!!.layers["platform"] != null)
        worldCreator.b2DWorldCreator.loadBodies(map!!.layers.get("platform"), gameScreen.world, platformBodies, PLATFORM_BIT)
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

}