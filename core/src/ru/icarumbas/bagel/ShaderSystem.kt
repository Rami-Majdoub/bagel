package ru.icarumbas.bagel

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.utils.Mappers


class ShaderSystem : IteratingSystem {

    private val shader: ShaderProgram
    private val batch: Batch
    private val body = Mappers.body
    private val viewPort: FitViewport

    constructor(batch: Batch, viewPort: FitViewport) : super(Family.all(PlayerComponent::class.java).get()) {
        this.batch = batch
        this.viewPort = viewPort
        ShaderProgram.pedantic = false
        shader = ShaderProgram(FileHandle("shaders/shader.vert"), FileHandle("shaders/shader.frag"))
        if (shader.log.isNotEmpty()) println(shader.log)
        batch.shader = shader
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val plPos = Vector3(body[entity].body.position.x, body[entity].body.position.y, 0f)
        viewPort.camera.project(plPos)

        shader.begin()
        shader.setUniformf("u_playerPosition", plPos.x / viewPort.worldWidth,
                plPos.y / viewPort.worldHeight
        )
        shader.setUniformf("u_resolution", viewPort.worldWidth, viewPort.worldHeight)
        shader.end()

    }

}