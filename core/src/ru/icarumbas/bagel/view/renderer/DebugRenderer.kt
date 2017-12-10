package ru.icarumbas.bagel.view.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable


class DebugRenderer(private val debugRenderer: Box2DDebugRenderer,
                    private val world: World,
                    private val camera: Camera) : Disposable{

    private var shouldRender = false


    private fun checkShouldRender(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            shouldRender = shouldRender.not()
        }

    }

    fun render(){
        checkShouldRender()
        if (shouldRender) debugRenderer.render(world, camera.combined)
    }

    override fun dispose() {
        debugRenderer.dispose()
    }
}
