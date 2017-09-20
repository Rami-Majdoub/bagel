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
    private val body = Mappers.body

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

            if (damage[entity].damage != 0) {
                damage[entity].HP -= damage[entity].damage
                damage[entity].damage = 0
                damage[entity].canBeAttacked = false
            }

            if (!damage[entity].knockback.isZero){
                body[entity].body.applyLinearImpulse(damage[entity].knockback, body[entity].body.worldCenter, true)
                damage[entity].knockback.set(0f, 0f)
                damage[entity].canBeAttacked = false
            }

            if (damage[entity].HP <= 0){
                deleteList.add(entity)
            }
        }
    }
}