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
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers
import kotlin.experimental.or

class ContactSystem : ContactListener, IteratingSystem {

    private val hud: Hud
    private val pl = Mappers.player
    private val body = Mappers.body
    private val damage = Mappers.damage
    private val weapon = Mappers.weapon

    private lateinit var playerEntity: Entity
    private lateinit var contactEntityA: Entity
    private lateinit var contactEntityB: Entity

    constructor(hud: Hud) : super(Family.all(BodyComponent::class.java).get()) {
        this.hud = hud
    }

    override fun update(deltaTime: Float) {}

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
            if (pl.has(it)) playerEntity = it
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
                if (playerEntity == contactEntityA) {
                    pl[contactEntityA].collidingWithGround = true
                } else {
                    pl[contactEntityB].collidingWithGround = true
                }
            }

            PLAYER_WEAPON_BIT or BREAKABLE_BIT -> {
                if (weapon[playerEntity].entityLeft == contactEntityA || weapon[playerEntity].entityRight == contactEntityA) {
                    damage[contactEntityB].damage += weapon[playerEntity].strength
                } else {
                    damage[contactEntityA].damage += weapon[playerEntity].strength
                }
                contact.isEnabled = false
            }

            PLAYER_BIT or TAKE_BIT -> {
                /*if (pl.has(entityA)) {

                } else {

                }*/
                contact.isEnabled = false
            }


        }


        /*
         when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {



             PLAYER_BIT or COIN_BIT -> {
                 contact.isEnabled = false

                 if (entityDeleteList.isEmpty()) {
                     gameScreen.game.assetManager["Sounds/coinpickup.wav", Sound::class.java].play()

                     entityDeleteList.add(otherBody)
                     gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                         if (it is Chest && it.coins.contains(otherBody)) it.coins.remove(otherBody) else
                             if (it is BreakableMapObject && it.coins.contains(otherBody)) it.coins.remove(otherBody)
                     }
                     gameScreen.rooms[gameScreen.currentMap].enemies.forEach {
                         if (it.coins.contains(otherBody)) it.coins.remove(otherBody)
                     }
                     gameScreen.player.money += 1

                 }
             }



             PLAYER_BIT or ENEMY_BIT -> {
                 contact.isEnabled = false
                 gameScreen.rooms[gameScreen.currentMap].enemies.forEach {
                     if (it is FlyMovement && it.body == otherBody) it.attack(gameScreen.player)
                 }
             }



             ENEMY_BIT or PLATFORM_BIT -> {
                 if (contact.fixtureB.filterData.categoryBits == ENEMY_BIT){
                     playerBody = contact.fixtureB.body
                     otherBody = contact.fixtureA.body
                 }

                 if (playerBody!!.position!!.y < otherBody!!.position!!.y) {
                     contact.isEnabled = false
                 }
             }

             PLAYER_BIT or CHEST_BIT -> {
                 touchedOpeningItem = true
                 gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                     if (it is Chest && it.body == otherBody) it.isOpened = true
                 }
                 contact.isEnabled = false
             }

             PLAYER_BIT or SPIKE_BIT -> {
                 gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                     if (it is Spike && it.body == otherBody) it.isTouched = true
                 }
                 contact.isEnabled = false
             }

             PLAYER_BIT or PORTAL_DOOR_BIT -> {
                 touchedOpeningItem = true
                 gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                     if (it is PortalDoor) {
                         it.isOpened = true
                     }
                 }
                 contact.isEnabled = false
             }

         }
    */
     }

    override fun beginContact(contact: Contact) {

     }

    override fun endContact(contact: Contact) {

        var entityA = Entity()
        var entityB = Entity()

        entities.forEach {
            if (Mappers.body[it].body == contact.fixtureA.body) entityA = it
            if (Mappers.body[it].body == contact.fixtureB.body) entityB = it
        }

        when (contact.fixtureA.filterData.categoryBits or contact.fixtureB.filterData.categoryBits) {

            PLAYER_BIT or GROUND_BIT -> {
                if (Mappers.player.has(entityA)) {
                    Mappers.player[entityA].collidingWithGround = false
                } else {
                    Mappers.player[entityB].collidingWithGround = false
                }
            }
        }



        /* val fixA = contact.fixtureA
         val fixB = contact.fixtureB

         var otherBody = fixB.body

         if (fixB.body == gameScreen.player.playerBody) {
             otherBody = fixA.body
         }

         when (fixA.filterData.categoryBits or fixB.filterData.categoryBits) {

             PLAYER_BIT or GROUND_BIT -> {
                 gameScreen.player.collidingWithGround = false
             }

             SWORD_BIT or BREAKABLE_BIT, SWORD_BIT_LEFT or BREAKABLE_BIT -> {
                 gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                     if (it is BreakableMapObject && it.body == otherBody) it.canBeBroken = false
                 }
             }

             SWORD_BIT or ENEMY_BIT,  SWORD_BIT_LEFT or ENEMY_BIT -> {
                 gameScreen.rooms[gameScreen.currentMap].enemies.forEach {
                     if (it is Enemy && it.body == otherBody) it.canHit = false
                 }
             }

             PLAYER_BIT or SPIKE_BIT -> {
                 gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach { if (it is Spike && it.body == otherBody) it.isTouched = false }
             }

             PLAYER_BIT or CHEST_BIT -> {
                 touchedOpeningItem = false
             }

             PLAYER_BIT or PORTAL_DOOR_BIT -> {
                 touchedOpeningItem = false
             }
         }*/
     }

}
