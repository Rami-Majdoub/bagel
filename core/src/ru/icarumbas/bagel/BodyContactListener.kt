package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ru.icarumbas.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.WeaponComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.systems.other.StateSystem
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.rotatedRight
import kotlin.experimental.or

class BodyContactListener : ContactListener {

    private val hud: Hud
    private val engine: Engine
    private val body = Mappers.body
    private val damage = Mappers.damage
    private val weapon = Mappers.weapon
    private val pl = Mappers.player
    private val state = Mappers.state
    private val attack = Mappers.attack
    private val ai = Mappers.AI
    private val size = Mappers.size

    private val playerEntity: Entity
    private lateinit var defendingEntity: Entity
    private lateinit var attackingEntity: Entity
    private lateinit var contactEntityA: Entity
    private lateinit var contactEntityB: Entity


    constructor(hud: Hud, playerEntity: Entity, engine: Engine) {
        this.hud = hud
        this.playerEntity = playerEntity
        this.engine = engine
    }

    private fun findAttackerAndDefender() {
        engine.getEntitiesFor(Family.all(WeaponComponent::class.java).get()).forEach{

            if (weapon[it].entityLeft === contactEntityA || weapon[it].entityRight === contactEntityA){
                attackingEntity = it
                defendingEntity = contactEntityB
            }
            if (weapon[it].entityLeft === contactEntityB || weapon[it].entityRight === contactEntityB){
                attackingEntity = it
                defendingEntity = contactEntityA
            }
        }
    }

    private fun findAttackerAndDefenderForSharp(){
        if (pl.has(contactEntityA) || ai.has(contactEntityA)) {
            defendingEntity = contactEntityA
            attackingEntity = contactEntityB
        }

        if (pl.has(contactEntityB) || ai.has(contactEntityB)) {
            defendingEntity = contactEntityB
            attackingEntity = contactEntityA
        }
    }

    private fun attack(){
        if (damage[defendingEntity].hitTimer > damage[defendingEntity].canBeDamagedTime &&
                !(state.has(defendingEntity) && state[defendingEntity].currentState == StateSystem.DEAD)) {
            damage[defendingEntity].damage = attack[attackingEntity].strength
            damage[defendingEntity].knockback.set(attack[attackingEntity].knockback)
            if (!attackingEntity.rotatedRight())
                damage[defendingEntity].knockback.scl(-1f, 1f)
            if (weapon.has(attackingEntity)) damage[defendingEntity].isWeaponContact = true
        }
    }

    private fun findContactEntities(contact: Contact){
        engine.getEntitiesFor(Family.all(BodyComponent::class.java).get()).forEach {
            if (body[it].body == contact.fixtureA.body) contactEntityA = it
            if (body[it].body == contact.fixtureB.body) contactEntityB = it
        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
     }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        findContactEntities(contact)

        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {
            PLAYER_BIT or PLATFORM_BIT -> {

                if (playerEntity == contactEntityA) {
                    if (body[contactEntityA].body.position.y <
                            body[contactEntityB].body.position.y + size[contactEntityB].rectSize.y * 2|| hud.isDownPressed()) {
                        contact.isEnabled = false
                    }
                } else {
                    if (body[contactEntityB].body.position.y <
                            body[contactEntityA].body.position.y + size[contactEntityA].rectSize.y * 2 || hud.isDownPressed()) {
                        contact.isEnabled = false
                    }
                }
            }

            AI_BIT or PLATFORM_BIT -> {
                if (ai.has(contactEntityA)) {
                    if (body[contactEntityA].body.position.y <
                            body[contactEntityB].body.position.y + size[contactEntityB].rectSize.y / 2 + size[playerEntity].rectSize.y / 2) {
                        contact.isEnabled = false
                    }
                } else {
                    if (body[contactEntityB].body.position.y <
                            body[contactEntityA].body.position.y + size[contactEntityA].rectSize.y / 2 + size[playerEntity].rectSize.y / 2) {
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
                if (!damage[defendingEntity].isWeaponContact) {
                    attack()
                }
            }

            SHARP_BIT or PLAYER_BIT, SHARP_BIT or AI_BIT -> {
                contact.isEnabled = false

                findAttackerAndDefenderForSharp()
                attack()
            }
        }
     }

    override fun beginContact(contact: Contact) {}

    override fun endContact(contact: Contact) {

        findContactEntities(contact)

        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {

            PLAYER_BIT or GROUND_BIT -> {
                pl[playerEntity].collidingWithGround = false
            }

            WEAPON_BIT or BREAKABLE_BIT, WEAPON_BIT or AI_BIT, WEAPON_BIT or PLAYER_BIT -> {
                findAttackerAndDefender()
                damage[defendingEntity].isWeaponContact = false
            }

        }
     }

}
