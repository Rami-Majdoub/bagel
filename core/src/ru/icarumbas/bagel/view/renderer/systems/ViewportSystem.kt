package ru.icarumbas.bagel.view.renderer.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.viewport.Viewport
import ru.icarumbas.bagel.engine.components.other.PlayerComponent
import ru.icarumbas.bagel.engine.components.physics.BodyComponent
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.body
import ru.icarumbas.bagel.view.renderer.components.SizeComponent


class ViewportSystem(

        private val viewport: Viewport,
        private val rm: RoomWorld

) : IteratingSystem(Family.all(PlayerComponent::class.java, BodyComponent::class.java, SizeComponent::class.java).get()) {

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

        if (view.camera.position.x + view.worldWidth / 2f > rm.getRoomWidth())
            view.camera.position.x = rm.getRoomWidth() - view.worldWidth / 2f

        if (view.camera.position.y + view.worldHeight / 2f > rm.getRoomWidth())
            view.camera.position.y = rm.getRoomWidth() - view.worldHeight / 2f

        view.camera.update()
    }
}