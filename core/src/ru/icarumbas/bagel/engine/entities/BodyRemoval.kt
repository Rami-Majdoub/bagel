package ru.icarumbas.bagel.engine.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.Bagel
import ru.icarumbas.bagel.engine.components.other.AIComponent
import ru.icarumbas.bagel.engine.io.PlayerInfo
import ru.icarumbas.bagel.engine.io.SerializedMapObject
import ru.icarumbas.bagel.engine.io.WorldIO
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*
import ru.icarumbas.bagel.view.screens.MainMenuScreen


class BodyRemoval(

        private val game: Bagel,
        private val world: World,
        private val engine: Engine,
        private val worldIO: WorldIO,
        private val roomWorld: RoomWorld,
        private val ioEntities: ArrayList<SerializedMapObject>,
        private val player: Entity

) : EntityListener{

    override fun entityRemoved(entity: Entity) {
        world.destroyBody(body[entity].body)

        if (weapon.has(entity)){
            world.destroyBody(body[weapon[entity].entityLeft].body)
            world.destroyBody(body[weapon[entity].entityRight].body)
        }
        if (roomId.has(entity)) {
            ioEntities.remove(roomId[entity].serialized)
        }

        engine.getEntitiesFor(Family.all(AIComponent::class.java).get()).forEach {
            if (AI[it].entityTarget == entity) {
                AI[it].entityTarget = player
            }
        }

        if (entity === player){
            worldIO.saveInfo(PlayerInfo(
                    position = translate[entity].x to translate[entity].y,
                    currentMap = roomWorld.currentMapId,
                    canContinue = false)
            )

            game.screen = MainMenuScreen(game)
        }
    }

    override fun entityAdded(entity: Entity) {

    }
}