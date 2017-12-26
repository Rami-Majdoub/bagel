package ru.icarumbas.bagel.engine.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.DoorComponent
import ru.icarumbas.bagel.engine.components.other.OpenComponent
import ru.icarumbas.bagel.engine.controller.UIController
import ru.icarumbas.bagel.engine.entities.EntityState
import ru.icarumbas.bagel.engine.entities.factories.EntityFactory
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*


class OpeningSystem (

        private val uiController: UIController,
        private val rm: RoomWorld,
        private val entityFactory: EntityFactory,
        private val playerEntity: Entity

): IteratingSystem(Family.all(OpenComponent::class.java).get()){


    private fun isChestOpened(e: Entity): Boolean{
        with (animation[e].animations[EntityState.OPENING]!!) {
            return getKeyFrame(state[e].stateTime) == keyFrames.get(keyFrames.size - 2) && !door.has(e)
        }
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (open[e].isCollidingWithPlayer){
                if (uiController.isOpenPressed()) {
                    open[e].opening = true
                }
            }

            if (state[e].currentState == EntityState.OPENING) {
                if (animation[e].animations[EntityState.OPENING]!!.isAnimationFinished(state[e].stateTime)) {
                    if (door.has(e)) {
                        rm.currentMapId = roomId[engine.getEntitiesFor(Family.all(DoorComponent::class.java).get()).random()].id
                    }
                }
                if (isChestOpened(e)) {
                    engine.addEntity(entityFactory.lootEntity(
                            0,
                            body[e].body.position.x to body[e].body.position.y + .5f,
                            roomId[e].id,
                            playerEntity
                            ))
                    state[e].stateTime -= deltaTime
                }

            }
        }
    }
}