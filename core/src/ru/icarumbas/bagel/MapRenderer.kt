package ru.icarumbas.bagel

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.Viewport

class MapRenderer {

    private val renderer : OrthogonalTiledMapRenderer
    private var lastMapId = -1
    private val rm: RoomManager
    private val assetManager: AssetManager
    private val viewport : Viewport


    constructor(renderer: OrthogonalTiledMapRenderer,
                rm: RoomManager,
                assetManager: AssetManager,
                viewport: Viewport){

        this.rm = rm
        this.renderer = renderer
        this.assetManager = assetManager
        this.viewport = viewport


    }

    private fun update() {
        if (lastMapId != rm.currentMapId) {
            renderer.map = assetManager[rm.rooms[rm.currentMapId].path, TiledMap::class.java]
            renderer.map.tileSets.forEach {
                it.forEach {
                    it.textureRegion.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                }
            }
            lastMapId = rm.currentMapId
        }


    }

    fun render(){
        update()

        renderer.setView(viewport.camera as OrthographicCamera)
        renderer.render()
    }

}
