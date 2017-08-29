package ru.icarumbas.bagel.systems.rendering

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.screens.GameScreen

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

    fun update() {
        if (lastMapId != rm.currentMapId) {
            renderer.map = assetManager[rm.rooms[rm.currentMapId].path, TiledMap::class.java]
            lastMapId = rm.currentMapId
        }


    }

    fun render(){
        update()

        renderer.setView(viewport.camera as OrthographicCamera)
        renderer.render()
    }

}
