package ru.icarumbas.bagel.Utils.B2dWorld

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import ru.icarumbas.bagel.Characters.mapObjects.Breakable
import ru.icarumbas.bagel.Characters.mapObjects.Chest
import ru.icarumbas.bagel.Characters.mapObjects.PortalDoor
import ru.icarumbas.bagel.Characters.mapObjects.Spike
import ru.icarumbas.bagel.Screens.GameScreen
import kotlin.experimental.or

class WorldContactListener(val gameScreen: GameScreen) : ContactListener {

    val deleteList = ArrayList<Body>()
    var touchedOpeningItem = false


    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        var playerBody = fixA.body
        var otherBody = fixB.body

        if (fixB.body == gameScreen.player.playerBody) {
            playerBody = fixB.body
            otherBody = fixA.body
        }


        when (fixA.filterData.categoryBits or fixB.filterData.categoryBits) {

            PLAYER_BIT or COIN_BIT -> {
                contact.isEnabled = false

                if (deleteList.isEmpty()) {
                    gameScreen.assetManager["Sounds/coinpickup.wav", Sound::class.java].play()

                    deleteList.add(otherBody)
                    gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                        if (it is Chest && it.coins.contains(otherBody)) it.coins.remove(otherBody) else
                        if (it is Breakable && it.coins.contains(otherBody)) it.coins.remove(otherBody)
                    }
                    gameScreen.player.money += 1
                }
            }

            PLAYER_BIT or BREAKABLE_BIT -> {
                contact.isEnabled = false
                gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                    if (it is Breakable && it.body == otherBody) it.canBeBroken = true
                }
            }

            PLAYER_BIT or PLATFORM_BIT -> {
                if (playerBody.position.y < otherBody.position.y + .7 || isTouchPadDown()) {
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

    }

    fun deleteBodies(){
        if (deleteList.isNotEmpty()) {

            deleteList.forEach {
                gameScreen.world.destroyBody(it)
                println("Destroyed body $it.")
            }
            deleteList.clear()
        }
    }

    override fun beginContact(contact: Contact) {

    }

    override fun endContact(contact: Contact) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        var playerBody = fixA.body
        var otherBody = fixB.body

        if (fixB.body == gameScreen.player.playerBody) {
            playerBody = fixB.body
            otherBody = fixA.body
        }

        when (fixA.filterData.categoryBits or fixB.filterData.categoryBits) {

            PLAYER_BIT or BREAKABLE_BIT -> {
                gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach {
                    if (it is Breakable && it.body == otherBody) it.canBeBroken = false
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
        }
    }

    fun isTouchPadDown() = gameScreen.hud.touchpad.knobY < gameScreen.hud.touchpad.height / 2f - gameScreen.hud.touchpad.width/7f

}
