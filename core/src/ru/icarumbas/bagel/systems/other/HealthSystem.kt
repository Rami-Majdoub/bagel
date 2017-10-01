package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.HealthComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class HealthSystem : IteratingSystem {

    private val damage = Mappers.damage
    private val body = Mappers.body
    private val anim = Mappers.animation
    private val state = Mappers.state

    private val rm: RoomManager
    private val world: World
    private val deleteList: ArrayList<Entity>
    private val coins: ArrayList<Body>

    constructor(rm: RoomManager, world: World, coins: ArrayList<Body>, deleteList: ArrayList<Entity>) : super(Family.all(
            HealthComponent::class.java).get()) {

        this.rm = rm
        this.world = world
        this.coins = coins
        this.deleteList = deleteList
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {

            damage[e].hitTimer += deltaTime

            if (damage[e].HP <= 0 &&
                    !(anim.has(e) && anim[e].animations.contains(StateSystem.DEAD) &&
                            !anim[e].animations[StateSystem.DEAD]?.isAnimationFinished(state[e].stateTime)!!)){
                deleteList.add(e)
            }

            if ((damage[e].damage != 0 || !damage[e].knockback.isZero) && damage[e].hitTimer > .5) {
                damage[e].HP -= damage[e].damage
                damage[e].damage = 0
                body[e].body.applyLinearImpulse(damage[e].knockback, body[e].body.worldCenter, true)
                damage[e].knockback.set(0f, 0f)
                damage[e].canBeAttacked = false
                damage[e].hitTimer = 0f
            }
        }
    }
}