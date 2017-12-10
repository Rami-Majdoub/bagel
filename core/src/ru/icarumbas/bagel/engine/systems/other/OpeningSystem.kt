package ru.icarumbas.bagel.engine.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.DoorComponent
import ru.icarumbas.bagel.engine.components.other.OpenComponent
import ru.icarumbas.bagel.engine.controller.UIController
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.inView


class OpeningSystem : IteratingSystem{

    private val uiController: UIController
    private val rm: RoomWorld


    private val deleteList: ArrayList<Entity>


    constructor(uiListener: UIController, rm: RoomWorld, deleteList: ArrayList<Entity>) : super(Family.all(OpenComponent::class.java).get()) {
        this.uiController = uiListener
        this.rm = rm
        this.deleteList = deleteList
    }

    private fun isChestOpened(e: Entity): Boolean{
        with (animation[e].animations[StateSystem.OPENING]!!) {
            return animation[e].animations[StateSystem.OPENING]!!.getKeyFrame(state[e].stateTime) == keyFrames.get(keyFrames.size - 2) && !door.has(e)
        }
    }


    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (open[e].isCollidingWithPlayer){
                if (uiController.isOpenPressed()) {
                    open[e].opening = true
                }
            }

            if (state[e].currentState == StateSystem.OPENING) {
                if (animation[e].animations[StateSystem.OPENING]!!.isAnimationFinished(state[e].stateTime)) {
                    if (door.has(e)) {
                        rm.currentMapId = roomId[engine.getEntitiesFor(Family.all(DoorComponent::class.java).get()).random()].id
                    }
                }
                if (isChestOpened(e)) {
                    engine.addEntity(lootCreator.createLoot(body[e].body.position.x, body[e].body.position.y + .5f, roomId[e].id))
                    state[e].stateTime -= deltaTime
                }

            }
        }
    }
}