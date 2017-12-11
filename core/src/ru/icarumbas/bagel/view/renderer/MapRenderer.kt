package ru.icarumbas.bagel.view.renderer

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import ru.icarumbas.bagel.engine.resources.ResourceManager

class MapRenderer(

        private val roomWorld: RoomWorldState,
        private val camera: Camera
) {

    private var lastMapId = -1
    private val renderer = OrthogonalTiledMapRenderer(null)

    private fun update() {
        if (isCurrentMapIdChanged()) {
            changeRoomMap()
        }
    }

    private fun isCurrentMapIdChanged() = lastMapId != roomWorld.getCurrentMapId()

    private fun changeRoomMap(){
        renderer.map = ResourceManager.getTiledMap(roomWorld.getMapPath())
        lastMapId = roomWorld.getCurrentMapId()
    }

    fun render(){
        update()

        renderer.setView(camera as OrthographicCamera)
        renderer.render()
    }

}
