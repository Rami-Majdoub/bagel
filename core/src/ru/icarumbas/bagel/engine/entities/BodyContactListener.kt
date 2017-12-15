package ru.icarumbas.bagel.engine.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ru.icarumbas.bagel.engine.components.physics.WeaponComponent
import ru.icarumbas.bagel.engine.controller.PlayerMoveController
import ru.icarumbas.bagel.utils.*
import kotlin.experimental.or

class BodyContactListener(

        private val playerController: PlayerMoveController,
        private val engine: Engine,
        private val playerEntity: Entity

) : ContactListener {


    private lateinit var defendingEntity: Entity
    private lateinit var attackingEntity: Entity
    private lateinit var contactEntityA: Entity
    private lateinit var contactEntityB: Entity


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
        contactEntityA = contact.fixtureA.body.userData as Entity
        contactEntityB = contact.fixtureB.body.userData as Entity
    }


    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
     }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        findContactEntities(contact)

        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {

            PLAYER_BIT or PLATFORM_BIT, PLAYER_FEET_BIT or PLATFORM_BIT -> {

                if (playerEntity == contactEntityA) {
                    if (body[contactEntityA].body.position.y <
                            body[contactEntityB].body.position.y + size[contactEntityB].rectSize.y * 2|| playerController.isDownPressed()) {
                        contact.isEnabled = false
                    }
                } else {
                    if (body[contactEntityB].body.position.y <
                            body[contactEntityA].body.position.y + size[contactEntityA].rectSize.y * 2 || playerController.isDownPressed()) {
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

//                hud.setOpenButtonVisible(true)
            }

            WEAPON_BIT or BREAKABLE_BIT, WEAPON_BIT or AI_BIT, WEAPON_BIT or PLAYER_BIT -> {
                contact.isEnabled = false

                findAttackerAndDefender()
                if (!damage[defendingEntity].isWeaponContact) {
                    if (!(state.has(defendingEntity) &&
                        state[defendingEntity].currentState == EntityState.NOTHING &&
                        state[defendingEntity].currentState == EntityState.APPEARING &&
                        state[defendingEntity].currentState == EntityState.DEAD)) {
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

//                hud.setOpenButtonVisible(false)
            }

        }
     }

}
