package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.viewport.Viewport
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.utils.Mappers


class ViewportSystem : IteratingSystem {

    private val body = Mappers.body
    private val gs: GameScreen
    private val viewport: Viewport

    constructor(viewport: Viewport, gs: GameScreen) : super(
            Family.all(
                    PlayerComponent::class.java,
                    BodyComponent::class.java,
                    SizeComponent::class.java).get()) {

        this.viewport = viewport
        this.gs = gs
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        moveCamera(viewport, body[entity].body.position.x, body[entity].body.position.y)
    }

    private fun moveCamera(view: Viewport, posX: Float, posY: Float) {

        view.camera.position.x = posX
        view.camera.position.y = posY

        if (view.camera.position.y - view.worldHeight / 2f < 0)
            view.camera.position.y = view.worldHeight / 2f

        if (view.camera.position.x - view.worldWidth / 2f < 0)
            view.camera.position.x = view.worldWidth / 2f

        if (view.camera.position.x + view.worldWidth / 2f > gs.rooms[gs.currentMapId].mapWidth)
            view.camera.position.x = gs.rooms[gs.currentMapId].mapWidth - view.worldWidth / 2f

        if (view.camera.position.y + view.worldHeight / 2f > gs.rooms[gs.currentMapId].mapHeight)
            view.camera.position.y = gs.rooms[gs.currentMapId].mapHeight - view.worldHeight / 2f

        view.camera.update()
    }
}