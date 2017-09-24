package ru.icarumbas.bagel.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ru.icarumbas.*
import ru.icarumbas.bagel.components.other.WeaponComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.rotatedRight
import kotlin.experimental.or

class ContactSystem : ContactListener, IteratingSystem {

    private val hud: Hud
    private val body = Mappers.body
    private val damage = Mappers.damage
    private val weapon = Mappers.weapon
    private val pl = Mappers.player

    private val playerEntity: Entity
    private lateinit var defendingEntity: Entity
    private lateinit var attackingEntity: Entity
    private lateinit var contactEntityA: Entity
    private lateinit var contactEntityB: Entity


    constructor(hud: Hud, playerEntity: Entity) : super(Family.all(BodyComponent::class.java).get()) {
        this.hud = hud
        this.playerEntity = playerEntity
    }

    private fun findAttackerAndDefender() {
        engine.getEntitiesFor(Family.all(WeaponComponent::class.java).get()).forEach{

            if (weapon[it].entityLeft == contactEntityA || weapon[it].entityRight == contactEntityA){
                attackingEntity = it
                defendingEntity = contactEntityB
            }
            if (weapon[it].entityLeft == contactEntityB || weapon[it].entityRight == contactEntityB){
                attackingEntity = it
                defendingEntity = contactEntityA
            }

        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        // Fake contact = collide with platforms
        if (Mappers.player.has(entity)) {
            Mappers.body[entity].body.applyLinearImpulse(Vector2(0f, -.00001f), Mappers.body[entity].body.localPoint2, true)
            Mappers.body[entity].body.applyLinearImpulse(Vector2(0f, .00001f), Mappers.body[entity].body.localPoint2, true)
        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
     }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

        entities.forEach {
            if (body[it].body == contact.fixtureA.body) contactEntityA = it
            if (body[it].body == contact.fixtureB.body) contactEntityB = it
        }


        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {
            PLAYER_BIT or PLATFORM_BIT -> {
                if (playerEntity == contactEntityA) {
                    if (body[contactEntityA].body.position.y < body[contactEntityB].body.position.y + .6 || hud.isDownPressed()) {
                        contact.isEnabled = false
                    }
                } else {
                    if (body[contactEntityB].body.position.y < body[contactEntityA].body.position.y + .6 || hud.isDownPressed()) {
                        contact.isEnabled = false
                    }
                }
            }

            PLAYER_BIT or GROUND_BIT -> {
                pl[playerEntity].collidingWithGround = true
            }

            WEAPON_BIT or BREAKABLE_BIT, WEAPON_BIT or AI_BIT, WEAPON_BIT or PLAYER_BIT -> {
                contact.isEnabled = false

                findAttackerAndDefender()

                if (damage[defendingEntity].canBeAttacked) {
                    damage[defendingEntity].damage += weapon[attackingEntity].strength
                    damage[defendingEntity].knockback.add(weapon[attackingEntity].knockback)
                    if (!attackingEntity.rotatedRight())
                        damage[defendingEntity].knockback.scl(-1f, 1f)
                }

            }

            PLAYER_BIT or TAKE_BIT -> {
                /*if (pl.has(entityA)) {

                } else {

                }*/
                contact.isEnabled = false
            }
        }
     }

    override fun beginContact(contact: Contact) {}

    override fun endContact(contact: Contact) {

        entities.forEach {
            if (body[it].body == contact.fixtureA.body) contactEntityA = it
            if (body[it].body == contact.fixtureB.body) contactEntityB = it
        }

        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {

            PLAYER_BIT or GROUND_BIT -> {
                pl[playerEntity].collidingWithGround = false
            }

            WEAPON_BIT or BREAKABLE_BIT, WEAPON_BIT or AI_BIT, WEAPON_BIT or PLAYER_BIT -> {
                findAttackerAndDefender()
                damage[defendingEntity].canBeAttacked = true
            }

        }
     }

}
