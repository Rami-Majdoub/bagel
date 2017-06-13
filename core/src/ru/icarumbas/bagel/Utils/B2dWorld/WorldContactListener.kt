package ru.icarumbas.bagel.Utils.B2dWorld

import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.*
import ru.icarumbas.bagel.Characters.mapObjects.Chest
import ru.icarumbas.bagel.Characters.mapObjects.Spikes
import ru.icarumbas.bagel.Screens.GameScreen
import kotlin.experimental.or

class WorldContactListener(val gameScreen: GameScreen) : ContactListener {

    private val deleteList = ArrayList<Body>()

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

            PLAYER_BIT or PLATFORM_BIT -> {
                if (playerBody.position.y < otherBody.position.y + .7 || isTouchPadDown()) {
                    contact.isEnabled = false
                }
            }

            PLAYER_BIT or SPIKES_BIT -> {
                gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach { if (it is Spikes && it.body == otherBody) it.isTouched = true }
                contact.isEnabled = false
            }

            PLAYER_BIT or CHEST_BIT -> {
                gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach { if (it is Chest && it.body == otherBody) it.isOpened = true }
                contact.isEnabled = false
            }

            PLAYER_BIT or COIN_BIT -> {
                contact.isEnabled = false

                if (deleteList.size == 0) {

                    gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach { if (it is Chest) it.coins.remove(otherBody) }
                    deleteList.add(otherBody)
                    gameScreen.player.money += 1
                }
            }
        }

    }

    fun update(){
        deleteList.forEach {
            gameScreen.world.destroyBody(it)
        }
        deleteList.clear()
    }

    override fun beginContact(contact: Contact) {

    }

    override fun endContact(contact: Contact) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        var otherBody = fixB.body

        if (fixA.filterData.categoryBits or fixB.filterData.categoryBits == PLAYER_BIT or SPIKES_BIT) {

            if (fixB.body == gameScreen.player.playerBody) {
                otherBody = fixA.body
            }

            gameScreen.rooms[gameScreen.currentMap].mapObjects.forEach { if (it is Spikes && it.body == otherBody) it.isTouched = false }

        }
    }

    fun isTouchPadDown() = gameScreen.hud.touchpad.knobY < gameScreen.hud.touchpad.height / 2f - gameScreen.hud.touchpad.width/7f

}
