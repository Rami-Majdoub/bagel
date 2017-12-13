package ru.icarumbas.bagel.view.renderer

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class MapRenderer(
        private val camera: Camera
) {

    val renderer = OrthogonalTiledMapRenderer(null, .01f)


    fun render(){
        renderer.setView(camera as OrthographicCamera)
        renderer.render()
    }

}
