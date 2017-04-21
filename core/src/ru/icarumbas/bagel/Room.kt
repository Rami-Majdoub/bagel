package ru.icarumbas.bagel

import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Tools.WorldCreate.WorldCreator


class Room(val worldCreator: WorldCreator, val gameScreen: GameScreen, path: String) {
    val map = worldCreator.tmxLoader.load(path)

    val roomLinks = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    lateinit var meshVertices: IntArray

    val groundBodies = ArrayList<Body>()
    val platformBodies = ArrayList<Body>()

    val mapWidth = map.properties["Width"].toString().toFloat()
    val mapHeight = map.properties["Height"].toString().toFloat()

    fun setAllBodiesActivity(active: Boolean) {
        setGroundActivity(active)
        setPlatformsActivity(active)
    }

    fun setPlatformsActivity(active: Boolean) = platformBodies.forEach { it.isActive = active }

    fun setGroundActivity(active: Boolean) = groundBodies.forEach { it.isActive = active }

    fun loadBodies(){
        worldCreator.b2DWorldCreator.loadBodies(map.layers.get("ground"), gameScreen.world, groundBodies, gameScreen.GROUND_BIT)
        if (map.layers["platform"] != null)
        worldCreator.b2DWorldCreator.loadBodies(map.layers.get("platform"), gameScreen.world, platformBodies, gameScreen.PLATFORM_BIT)
    }

}