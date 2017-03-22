package ru.icarumbas.bagel.Tools.WorldCreate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileSet

import java.util.ArrayList
import java.util.HashMap

class AnimationCreator {

    private var elapsedSinceAnimation = 0f
    private var fireTiles: HashMap<String, TiledMapTile>? = null
    private var fireCellsInScene: ArrayList<TiledMapTileLayer.Cell>? = null

    fun updateAnimations() {
        elapsedSinceAnimation += Gdx.graphics.deltaTime

        if (elapsedSinceAnimation > 0.025f) {
            updateFireAnimations()
            elapsedSinceAnimation = 0f
        }
    }

    fun createTileAnimation(currentMap: Int, maps: ArrayList<TiledMap>) {
        val tileset = maps[currentMap].tileSets.getTileSet("Fire")
        fireTiles = HashMap<String, TiledMapTile>()

        for (tile in tileset) {
            val property = tile.properties.get("FireFrame")
            if (property != null) fireTiles!!.put(property as String, tile)
        }

        fireCellsInScene = ArrayList<TiledMapTileLayer.Cell>()
        val layer = maps[currentMap].layers.get("Fire") as TiledMapTileLayer

        for (x in 0..layer.width - 1) {
            for (y in 0..layer.height - 1) {
                val cell = layer.getCell(x, y)
                if (cell != null) {
                    if (cell.tile.properties.get("FireFrame") != null) {
                        fireCellsInScene!!.add(cell)
                    }
                }
            }
        }
    }

    private fun updateFireAnimations() {
        for (cell in fireCellsInScene!!) {
            val property = cell.tile.properties.get("FireFrame") as String
            var currentAnimationFrame: Int = Integer.parseInt(property)
            currentAnimationFrame++
            if (currentAnimationFrame > fireTiles!!.size) currentAnimationFrame = 1

            val newTile = fireTiles!![currentAnimationFrame.toString()]
            cell.tile = newTile
        }
    }
}
