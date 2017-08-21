package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Body
import ru.icarumbas.REG_ROOM_HEIGHT
import ru.icarumbas.REG_ROOM_WIDTH
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.utils.Mappers


class RoomChangingSystem : IteratingSystem {

    private val gs: GameScreen

    constructor(gs: GameScreen) : super(Family.all(
            PlayerComponent::class.java,
            SizeComponent::class.java,
            BodyComponent::class.java).get()) {
        this.gs = gs
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val size = Mappers.size[entity]
        val body = Mappers.body[entity]

        checkRoomChange(size, body.body)
    }

    fun checkRoomChange(pos: SizeComponent, body: Body) {

        if (body.position.x > gs.rooms[gs.currentMapId].mapWidth && body.position.y < REG_ROOM_HEIGHT)
            changeRoom("Right", 2, 1, 2, body, pos) else

        if (body.position.x < 0 && body.position.y < REG_ROOM_HEIGHT)
            changeRoom("Left", 0, 1, 0, body, pos) else

        if (body.position.y > gs.rooms[gs.currentMapId].mapHeight && body.position.x < REG_ROOM_WIDTH)
            changeRoom("Up", 0, 3, 1, body, pos) else

        if (body.position.y < 0 && body.position.x < REG_ROOM_WIDTH)
            changeRoom("Down", 0, 1, 3, body, pos) else

        if (body.position.x < 0 && body.position.y > REG_ROOM_HEIGHT)
            changeRoom("Left", 0, 3, 4, body, pos) else

        if (body.position.y < 0 && body.position.x > REG_ROOM_WIDTH)
            changeRoom("Down", 2, 1, 7, body, pos) else

        if (body.position.x > gs.rooms[gs.currentMapId].mapWidth && body.position.y > REG_ROOM_HEIGHT)
            changeRoom("Right", 2, 3, 6, body, pos) else

        if (body.position.y > gs.rooms[gs.currentMapId].mapHeight && body.position.x > REG_ROOM_WIDTH)
            changeRoom("Up", 2, 3, 5, body, pos)
    }

    fun changeRoom(side: String, plX: Int, plY: Int, newIdLink: Int, body: Body, size: SizeComponent) {

        val newId = gs.rooms[gs.currentMapId].roomLinks[newIdLink]!!

        if (side == "Up" || side == "Down") {
            // Compare top-right parts of previous and current maps
            val X10 = gs.rooms[newId].meshVertices[2]
            val prevX = gs.rooms[gs.currentMapId].meshVertices[plX]

            if (side == "Up") {
                if (prevX == X10) {
                    body.setTransform(gs.rooms[newId].mapWidth - REG_ROOM_WIDTH / 2, 0f, 0f)
                } else body.setTransform(REG_ROOM_WIDTH / 2, 0f, 0f)
            }
            if (side == "Down") {
                if (prevX == X10) body.setTransform(gs.rooms[newId].mapWidth - REG_ROOM_WIDTH / 2,
                        gs.rooms[newId].mapHeight, 0f)
                else body.setTransform(REG_ROOM_WIDTH / 2, gs.rooms[newId].mapHeight, 0f)
            }
        } else

            if (side == "Left" || side == "Right") {
                // Compare top parts of previous and current maps
                val Y11 = gs.rooms[newId].meshVertices[3]
                val prevY = gs.rooms[gs.currentMapId].meshVertices[plY]

                if (side == "Left") {
                    if (prevY == Y11) body.setTransform(gs.rooms[newId].mapWidth,
                            gs.rooms[newId].mapHeight - REG_ROOM_HEIGHT / 2 - size.height / 2, 0f)
                    else body.setTransform(gs.rooms[newId].mapWidth, REG_ROOM_HEIGHT / 2 - size.height / 2, 0f)
                }
                if (side == "Right") {
                    if (prevY == Y11)
                        body.setTransform(0f, gs.rooms[newId].mapHeight - REG_ROOM_HEIGHT / 2 - size.height / 2, 0f)
                    else
                        body.setTransform(0f, REG_ROOM_HEIGHT / 2 - size.height / 2, 0f)
                }
            }

        gs.currentMapId = newId
    }
}