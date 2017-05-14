package ru.icarumbas.bagel.Utils.WorldCreate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import java.util.*

class AnimationCreator(val assetManager: AssetManager){

    private var elapsedSinceAnimation = 0f
    private var fireTiles = HashMap<Int, TiledMapTile>()
    lateinit private var fireCellsInScene: ArrayList<TiledMapTileLayer.Cell>

    fun updateAnimations() {
        elapsedSinceAnimation += Gdx.graphics.deltaTime

        if (elapsedSinceAnimation > 0.025f && fireTiles.isNotEmpty() ) {
            updateFireAnimations()
            elapsedSinceAnimation = 0f
        }
    }

    fun createTileAnimation(currentMap: Int, rooms: ArrayList<Room>) {
        if (assetManager.get(rooms[currentMap].path, TiledMap::class.java).tileSets.getTileSet("Fire") == null) return
        val tileset = assetManager.get(rooms[currentMap].path, TiledMap::class.java).tileSets.getTileSet("Fire")
        fireTiles = HashMap<Int, TiledMapTile>()

        for (tile in tileset) {
            val property = tile.properties.get("FireFrame")
            if (property != null) fireTiles.put(property as Int, tile)
        }

        fireCellsInScene = ArrayList<TiledMapTileLayer.Cell>()
        val layer = assetManager.get(rooms[currentMap].path, TiledMap::class.java).layers.get("Fire") as TiledMapTileLayer

        for (x in 0..layer.width - 1) (0..layer.height - 1).forEach({
            val cell = layer.getCell(x, it)
            if (cell != null) {
                if (cell.tile.properties.get("FireFrame") != null) {
                    fireCellsInScene.add(cell)
                }
            }
        })
    }

    private fun updateFireAnimations() {
        for (cell in fireCellsInScene) {
            var currentAnimationFrame = cell.tile.properties.get("FireFrame") as Int
            currentAnimationFrame++
            if (currentAnimationFrame > fireTiles.size) currentAnimationFrame = 1

            val newTile = fireTiles[currentAnimationFrame]
            cell.tile = newTile
        }
    }
}
