package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.DoorComponent
import ru.icarumbas.bagel.components.other.OpenComponent
import ru.icarumbas.bagel.screens.scenes.Hud
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

    private lateinit var lootEntity: Entity
    private val deleteList: ArrayList<Entity>


    constructor(uiListener: UInputListener, rm: RoomManager, deleteList: ArrayList<Entity>) : super(Family.all(OpenComponent::class.java).get()) {
        this.uiListener = uiListener
        this.rm = rm
        this.deleteList = deleteList
    }

    private fun createLoot(e: Entity){
        lootEntity = open[e].loot!![MathUtils.random(0, open[e].loot!!.size-1)]
        engine.addEntity(lootEntity)
        body[lootEntity].body.isActive = true
        body[lootEntity].body.applyLinearImpulse(Vector2(MathUtils.random(2f), MathUtils.random(2f)), body[lootEntity].body.localCenter, true)
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            if (open[e].isCollidingWithPlayer){
                if (uiListener.openButtonPressed) {
                    open[e].opening = true
                }
            }
            if (animation[e].animations[StateSystem.OPENING]!!.isAnimationFinished(state[e].stateTime)){
                if (state[e].currentState == StateSystem.OPENING) {
                    open[e].opening = false

                    if (door.has(e)){
                        rm.currentMapId = roomId[engine.getEntitiesFor(Family.all(DoorComponent::class.java).get()).random()].id
                    } else {
                        createLoot(e)
                        deleteList.add(e)
                    }
                }
            }
        }
    }
}