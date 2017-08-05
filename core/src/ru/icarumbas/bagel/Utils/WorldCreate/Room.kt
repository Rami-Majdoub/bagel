package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import ru.icarumbas.Bagel
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.Characters.Enemies.Enemy
import ru.icarumbas.bagel.Characters.mapObjects.BreakableMapObject
import ru.icarumbas.bagel.Characters.mapObjects.Chest
import ru.icarumbas.bagel.Characters.mapObjects.MapObject
import ru.icarumbas.bagel.Screens.GameScreen
import ru.icarumbas.bagel.Utils.B2dWorld.B2DWorldCreator
import ru.icarumbas.bagel.Utils.B2dWorld.WorldContactListener


class Room {

    var path: String = ""

    val roomLinks = arrayOfNulls<Int>(8)
    lateinit var meshVertices: IntArray

    var mapObjects = ArrayList<MapObject>()
    var enemies = ArrayList<Enemy>()

    var mapWidth = 0f
    var mapHeight = 0f

    fun loadMap(path: String, assetManager: AssetManager){
        this.path = path
        mapWidth = assetManager.get(path, TiledMap::class.java).properties["Width"].toString().toFloat().div(PIX_PER_M)
        mapHeight = assetManager.get(path, TiledMap::class.java).properties["Height"].toString().toFloat().div(PIX_PER_M)
    }

    fun removeUnserealizableObjects(){
        mapObjects.forEach {
            it.body = null
            if (it is BreakableMapObject) {
                it.coins.clear()
                it.coin = null
            }
        }
        enemies.forEach {
            it.body = null
            it.coins.clear()
            it.coin = null
            it.appearAnimation = null
            it.attackAnimation = null
            it.dieAnimation = null
            it.jumpAnimation = null
            it.runAnimation = null
            it.stateAnimation = null
        }
    }

    fun loadEntities(b2DWorldCreator: B2DWorldCreator, assetManager: AssetManager){

        b2DWorldCreator.loadMapObject(path, "boxes", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "chandeliers", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "chests", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "statues", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "spikeTraps", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "spikes", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "portalDoor", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "chairs", assetManager, mapObjects)
        b2DWorldCreator.loadMapObject(path, "tables", assetManager, mapObjects)


        b2DWorldCreator.loadEnemies(path, "flyingEnemies", assetManager,  enemies)
        b2DWorldCreator.loadEnemies(path, "groundEnemies", assetManager,  enemies)
    }

    fun clearEntities(worldContactListener: WorldContactListener){

        // Clear chest coins
        mapObjects.forEach {
            if (it is Chest) {
                it.coins.forEach { body ->
                    worldContactListener.deleteList.add(body)
                }
                it.coins.clear()
            } else
                if (it is BreakableMapObject) {
                    it.coins.forEach { body ->
                        worldContactListener.deleteList.add(body)
                    }
                    it.coins.clear()
                }

            it.sprite = null
            it.body?.isActive = false
        }

        // Deleting used MapObjects
        val it = mapObjects.iterator()
        while (it.hasNext()) {
            if (it.next().destroyed) it.remove()
        }

        enemies.forEach {
            it.coins.forEach { worldContactListener.deleteList.add(it) }
            it.coins.clear()
            it.sprite = null
            it.body?.isActive = false
        }

        // Deleting killed Enemies
        val itEn = enemies.iterator()
        while (itEn.hasNext()) {
            if (itEn.next().killed) itEn.remove()
        }
    }

    fun awakeEntities(animationCreator: AnimationCreator, game: Bagel){
        mapObjects.forEach {
            it.loadSprite(game.assetManager.get("Packs/RoomObjects.txt", TextureAtlas::class.java))
            it.body?.isActive = true
        }

        enemies.forEach {
            it.loadAnimation(game.assetManager.get("Packs/Enemies.txt", TextureAtlas::class.java), animationCreator)
            it.body?.isActive = true
        }
    }

    fun draw(batch: Batch, delta: Float, gameScreen: GameScreen) {
        mapObjects.forEach { it.draw(batch, delta, gameScreen) }
        enemies.forEach { it.draw(batch, delta, gameScreen) }
    }

}