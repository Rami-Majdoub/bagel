package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.viewport.Viewport
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.Mappers.Mappers.body


class ViewportSystem : IteratingSystem {

    private val rm: RoomManager
    private val viewport: Viewport

    constructor(viewport: Viewport, rm: RoomManager) : super(
            Family.all(
                    PlayerComponent::class.java,
                    BodyComponent::class.java,
                    SizeComponent::class.java).get()) {

        this.viewport = viewport
        this.rm = rm
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

        if (view.camera.position.x + view.worldWidth / 2f > rm.width())
            view.camera.position.x = rm.width() - view.worldWidth / 2f

        if (view.camera.position.y + view.worldHeight / 2f > rm.height())
            view.camera.position.y = rm.height() - view.worldHeight / 2f

        view.camera.update()
    }
}