package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.DoorComponent
import ru.icarumbas.bagel.components.other.OpenComponent
import ru.icarumbas.bagel.creators.LootCreator
import ru.icarumbas.bagel.screens.scenes.UInputListener
import ru.icarumbas.bagel.utils.Mappers.Mappers.animation
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.door
import ru.icarumbas.bagel.utils.Mappers.Mappers.open
import ru.icarumbas.bagel.utils.Mappers.Mappers.roomId
import ru.icarumbas.bagel.utils.Mappers.Mappers.state
import ru.icarumbas.bagel.utils.inView


class OpeningSystem : IteratingSystem{

    private val uiListener: UInputListener
    private val rm: RoomManager

    private val deleteList: ArrayList<Entity>
    private val lootCreator: LootCreator


    constructor(uiListener: UInputListener, rm: RoomManager, deleteList: ArrayList<Entity>, lootCreator: LootCreator) : super(Family.all(OpenComponent::class.java).get()) {
        this.uiListener = uiListener
        this.rm = rm
        this.deleteList = deleteList
        this.lootCreator = lootCreator
    }

    private fun isChestOpened(e: Entity): Boolean{
        with (animation[e].animations[StateSystem.OPENING]!!) {
            return animation[e].animations[StateSystem.OPENING]!!.getKeyFrame(state[e].stateTime) == keyFrames.get(keyFrames.size - 2)
        }
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (open[e].isCollidingWithPlayer){
                if (uiListener.openButtonPressed) {
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