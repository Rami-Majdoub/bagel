package ru.icarumbas.bagel

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class MapRenderer {

    private val renderer : OrthogonalTiledMapRenderer
    private var lastMapId = -1
    private val rm: RoomManager
    private val assetManager: AssetManager
    private val camera: Camera


    constructor(renderer: OrthogonalTiledMapRenderer,
                rm: RoomManager,
                assetManager: AssetManager,
                camera: Camera){

        this.rm = rm
        this.renderer = renderer
        this.assetManager = assetManager
        this.camera = camera


    }

    private fun update() {
        if (lastMapId != rm.currentMapId) {
            renderer.map = assetManager[rm.rooms[rm.currentMapId].path, TiledMap::class.java]
            lastMapId = rm.currentMapId
        }
    }

    fun render(){
        update()

        renderer.setView(camera as OrthographicCamera)
        renderer.render()
    }

}
