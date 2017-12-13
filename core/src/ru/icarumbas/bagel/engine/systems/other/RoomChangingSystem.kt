package ru.icarumbas.bagel.engine.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.bagel.engine.world.REG_ROOM_HEIGHT
import ru.icarumbas.bagel.engine.world.REG_ROOM_WIDTH
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.body
import ru.icarumbas.bagel.utils.player
import ru.icarumbas.bagel.utils.size
import ru.icarumbas.bagel.view.renderer.components.SizeComponent


class RoomChangingSystem(

        private val roomWorld: RoomWorld

) : IteratingSystem(Family.all(ru.icarumbas.bagel.engine.components.other.PlayerComponent::class.java).get()) {


    override fun processEntity(entity: Entity, deltaTime: Float) {
        checkRoomChange(size[entity], body[entity].body)

        // Fake contact = collide with platforms
        if (player.has(entity)) {
            body[entity].body.applyLinearImpulse(Vector2(0f, -.00001f), body[entity].body.localPoint2, true)
            body[entity].body.applyLinearImpulse(Vector2(0f, .00001f), body[entity].body.localPoint2, true)
        }
    }

    private fun checkRoomChange(pos: SizeComponent, body: Body) {

        if (body.position.x > roomWorld.getRoomWidth() && body.position.y < REG_ROOM_HEIGHT)
            changeRoom("Right", 2, 1, 2, body, pos) else

        if (body.position.x < 0 && body.position.y < REG_ROOM_HEIGHT)
            changeRoom("Left", 0, 1, 0, body, pos) else

        if (body.position.y > roomWorld.getRoomHeight() && body.position.x < REG_ROOM_WIDTH)
            changeRoom("Up", 0, 3, 1, body, pos) else

        if (body.position.y < 0 && body.position.x < REG_ROOM_WIDTH)
            changeRoom("Down", 0, 1, 3, body, pos) else

        if (body.position.x < 0 && body.position.y > REG_ROOM_HEIGHT)
            changeRoom("Left", 0, 3, 4, body, pos) else

        if (body.position.y < 0 && body.position.x > REG_ROOM_WIDTH)
            changeRoom("Down", 2, 1, 7, body, pos) else

        if (body.position.x > roomWorld.getRoomWidth() && body.position.y > REG_ROOM_HEIGHT)
            changeRoom("Right", 2, 3, 6, body, pos) else

        if (body.position.y > roomWorld.getRoomHeight() && body.position.x > REG_ROOM_WIDTH)
            changeRoom("Up", 2, 3, 5, body, pos)
    }

    private fun changeRoom(side: String, plX: Int, plY: Int, newIdLink: Int, body: Body, size: SizeComponent) {

        val newId = roomWorld.getRoomPass(newIdLink)

        if (side == "Up" || side == "Down") {
            // Compare top-right parts of previous and current maps
            val x10 = roomWorld.getRoomMeshCoordinate(2, newId)
            val prevX = roomWorld.getRoomMeshCoordinate(plX)

            if (side == "Up") {
                if (prevX == x10) {
                    body.setTransform(roomWorld.getRoomWidth(newId) - REG_ROOM_WIDTH / 2, 0f, 0f)
                } else body.setTransform(REG_ROOM_WIDTH / 2, 0f, 0f)
            }
            if (side == "Down") {
                if (prevX == x10) body.setTransform(roomWorld.getRoomWidth(newId) - REG_ROOM_WIDTH / 2,
                        roomWorld.getRoomHeight(newId), 0f)
                else body.setTransform(REG_ROOM_WIDTH / 2, roomWorld.getRoomHeight(newId), 0f)
            }
        } else

            if (side == "Left" || side == "Right") {
                // Compare top parts of previous and current maps
                val y11 = roomWorld.getRoomMeshCoordinate(3, newId)
                val prevY = roomWorld.getRoomMeshCoordinate(plY)

                if (side == "Left") {
                    if (prevY == y11) body.setTransform(roomWorld.getRoomWidth(newId),
                            roomWorld.getRoomHeight(newId) - REG_ROOM_HEIGHT / 2 - size.spriteSize.y / 2, 0f)
                    else body.setTransform(roomWorld.getRoomWidth(newId), REG_ROOM_HEIGHT / 2 - size.spriteSize.y / 2, 0f)
                }
                if (side == "Right") {
                    if (prevY == y11)
                        body.setTransform(0f, roomWorld.getRoomHeight(newId) - REG_ROOM_HEIGHT / 2 - size.spriteSize.y / 2, 0f)
                    else
                        body.setTransform(0f, REG_ROOM_HEIGHT / 2 - size.spriteSize.y / 2, 0f)
                }
            }

        roomWorld.currentMapId = newId
    }
}