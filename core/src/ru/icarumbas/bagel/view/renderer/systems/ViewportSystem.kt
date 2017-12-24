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

        private val view: Viewport,
        private val rm: RoomWorld

) : IteratingSystem(Family.all(PlayerComponent::class.java, BodyComponent::class.java, SizeComponent::class.java).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        moveCamera(body[entity].body.position.x, body[entity].body.position.y)


    }

    private fun moveCamera(posX: Float, posY: Float) {

        with (view) {
            camera.position.x = posX
            camera.position.y = posY

            if (camera.position.y - worldHeight / 2f < 0)
                camera.position.y = worldHeight / 2f

            if (camera.position.x - worldWidth / 2f < 0)
                camera.position.x = worldWidth / 2f

            if (camera.position.x + worldWidth / 2f > rm.getRoomWidth())
                camera.position.x = rm.getRoomWidth() - worldWidth / 2f

            if (camera.position.y + worldHeight / 2f > rm.getRoomHeight())
                camera.position.y = rm.getRoomHeight() - worldHeight / 2f

            camera.update()
        }
    }
}