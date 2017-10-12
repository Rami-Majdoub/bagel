package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.DoorComponent
import ru.icarumbas.bagel.components.other.OpenComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class OpeningSystem : IteratingSystem{

    private val hud: Hud
    private val rm: RoomManager
    private val open = Mappers.open
    private val anim = Mappers.animation
    private val state = Mappers.state
    private val door = Mappers.door
    private val roomId = Mappers.roomId

    private val deleteList: ArrayList<Entity>


    constructor(hud: Hud, rm: RoomManager, deleteList: ArrayList<Entity>) : super(Family.all(OpenComponent::class.java).get()) {
        this.hud = hud
        this.rm = rm
        this.deleteList = deleteList
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (open[e].isCollidingWithPlayer){
                if (hud.openButtonPressed) {
                    open[e].opening = true
                }
            }
            if (anim[e].animations[StateSystem.OPENING]!!.isAnimationFinished(state[e].stateTime)){
                if (state[e].currentState == StateSystem.OPENING) {
                    if (door.has(e)){
                        rm.currentMapId = roomId[engine.getEntitiesFor(Family.all(DoorComponent::class.java).get()).random()].id
                    } else {
                        deleteList.add(e)
                    }
                }
            }
        }
    }
}