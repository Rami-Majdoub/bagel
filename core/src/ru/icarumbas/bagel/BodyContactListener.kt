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
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.attack
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.damage
import ru.icarumbas.bagel.utils.Mappers.Mappers.loot
import ru.icarumbas.bagel.utils.Mappers.Mappers.open
import ru.icarumbas.bagel.utils.Mappers.Mappers.player
import ru.icarumbas.bagel.utils.Mappers.Mappers.size
import ru.icarumbas.bagel.utils.Mappers.Mappers.state
import ru.icarumbas.bagel.utils.Mappers.Mappers.weapon
import ru.icarumbas.bagel.utils.rotatedRight
import kotlin.experimental.or

class BodyContactListener : ContactListener {

    private val hud: Hud
    private val engine: Engine

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
        if (player.has(contactEntityA) || AI.has(contactEntityA)) {
            defendingEntity = contactEntityA
            attackingEntity = contactEntityB
        }

        if (player.has(contactEntityB) || AI.has(contactEntityB)) {
            defendingEntity = contactEntityB
            attackingEntity = contactEntityA
        }
    }

    private fun attackEntity(){
        if (damage[defendingEntity].hitTimer > damage[defendingEntity].canBeDamagedTime) {

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

            PLAYER_BIT or PLATFORM_BIT, PLAYER_FEET_BIT or PLATFORM_BIT -> {

                if (playerEntity == contactEntityA) {
                    if (body[contactEntityA].body.position.y <
                            body[contactEntityB].body.position.y + size[contactEntityB].rectSize.y * 2|| hud.touchpad.isDownPressed()) {
                        contact.isEnabled = false
                    }
                } else {
                    if (body[contactEntityB].body.position.y <
                            body[contactEntityA].body.position.y + size[contactEntityA].rectSize.y * 2 || hud.touchpad.isDownPressed()) {
                        contact.isEnabled = false
                    }
                }
                player[playerEntity].standindOnGround = true

            }

            AI_BIT or PLATFORM_BIT -> {
                if (AI.has(contactEntityA)) {
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
                player[playerEntity].collidingWithGround = true
            }

            PLAYER_FEET_BIT or GROUND_BIT -> {
                player[playerEntity].standindOnGround = true
            }

            SHARP_BIT or PLAYER_FEET_BIT, SHARP_BIT or AI_BIT, SHARP_BIT or PLAYER_BIT -> {
                contact.isEnabled = false

                findAttackerAndDefenderForSharp()
                attackEntity()
            }

            PLAYER_BIT or KEY_OPEN_BIT -> {
                contact.isEnabled = false

                if (contactEntityA === playerEntity) {
                    open[contactEntityB].isCollidingWithPlayer = true

                } else {
                    open[contactEntityA].isCollidingWithPlayer = true
                }

                hud.setOpenButtonVisible(true)
            }

            WEAPON_BIT or BREAKABLE_BIT, WEAPON_BIT or AI_BIT, WEAPON_BIT or PLAYER_BIT -> {
                contact.isEnabled = false

                findAttackerAndDefender()
                if (!damage[defendingEntity].isWeaponContact) {
                    if (!(state.has(defendingEntity) &&
                        state[defendingEntity].currentState == StateSystem.NULL &&
                        state[defendingEntity].currentState == StateSystem.APPEARING &&
                        state[defendingEntity].currentState == StateSystem.DEAD)) {
                        attackEntity()
                        if (AI.has(defendingEntity)) AI[defendingEntity].entityTarget = attackingEntity
                    }
                }
            }

            PLAYER_BIT or AI_BIT -> {
                contact.isEnabled = false

                if (contactEntityA === playerEntity){
                    defendingEntity = contactEntityA
                    attackingEntity = contactEntityB
                } else {
                    defendingEntity = contactEntityB
                    attackingEntity = contactEntityA
                }

                attackEntity()
            }

            PLAYER_FEET_BIT or LOOT_BIT, PLAYER_BIT or LOOT_BIT -> {
                contact.isEnabled = false

                if (contactEntityA === playerEntity) {
                    loot[contactEntityB].isCollidingWithPlayer = true

                } else {
                    loot[contactEntityA].isCollidingWithPlayer = true
                }

            }
        }
     }

    override fun beginContact(contact: Contact) {}

    override fun endContact(contact: Contact) {

        findContactEntities(contact)

        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {

            PLAYER_BIT or GROUND_BIT -> {
                player[playerEntity].collidingWithGround = false
            }

            PLAYER_FEET_BIT or GROUND_BIT, PLAYER_FEET_BIT or PLATFORM_BIT -> {
                player[playerEntity].standindOnGround = false
            }

            WEAPON_BIT or BREAKABLE_BIT, WEAPON_BIT or AI_BIT, WEAPON_BIT or PLAYER_BIT -> {
                findAttackerAndDefender()
                damage[defendingEntity].isWeaponContact = false
            }

            PLAYER_BIT or KEY_OPEN_BIT -> {
                if (contactEntityA === playerEntity) {
                    open[contactEntityB].isCollidingWithPlayer = false
                } else {
                    open[contactEntityA].isCollidingWithPlayer = false
                }

                hud.setOpenButtonVisible(false)
            }

            PLAYER_FEET_BIT or LOOT_BIT, PLAYER_BIT or LOOT_BIT -> {
                contact.isEnabled = false

                if (contactEntityA === playerEntity) {
                    loot[contactEntityB].isCollidingWithPlayer = false

                } else {
                    loot[contactEntityA].isCollidingWithPlayer = false
                }

            }

        }
     }

}
