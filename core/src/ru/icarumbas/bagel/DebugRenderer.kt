package ru.icarumbas.bagel

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport


class DebugRenderer(private val debugRenderer: Box2DDebugRenderer,
                    private val world: World,
                    private val viewPort: Viewport) : Disposable{

    var shouldRender = false


    fun checkShouldRender(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            shouldRender = shouldRender.not()
        }
    }

    fun render(){
        checkShouldRender()
        if (shouldRender) debugRenderer.render(world, viewPort.camera.combined)
    }

    override fun dispose() {
        debugRenderer.dispose()
    }
}
