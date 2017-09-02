package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.DamageComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class HealthSystem : IteratingSystem {

    private val damage = Mappers.damage

    private val rm: RoomManager
    private val world: World
    private val deleteList: ArrayList<Entity>
    private val coins: ArrayList<Body>

    constructor(rm: RoomManager, world: World, coins: ArrayList<Body>, deleteList: ArrayList<Entity>) : super(Family.all(
            DamageComponent::class.java).get()) {

        this.rm = rm
        this.world = world
        this.coins = coins
        this.deleteList = deleteList
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(rm)) {
            damage[entity].hitTimer += deltaTime

            if (damage[entity].damage != 0 && damage[entity].hitTimer > .5f) {
                damage[entity].HP -= damage[entity].damage
                damage[entity].damage = 0
            }

            if (damage[entity].HP <= 0){
                deleteList.add(entity)
            }
        }
    }
}