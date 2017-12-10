package ru.icarumbas.bagel.view.renderer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import ru.icarumbas.bagel.engine.world.RoomWorld

class MapRenderer {

    private val renderer : OrthogonalTiledMapRenderer
    private var lastMapId = -1
    private val rm: RoomWorld
    private val assetManager: AssetManager
    private val camera: Camera


    constructor(renderer: OrthogonalTiledMapRenderer,
                rm: RoomWorld,
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
