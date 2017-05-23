package ru.icarumbas.bagel.Utils.B2dWorld

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.PLAYER_BIT
import ru.icarumbas.SPIKES_BIT
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Characters.mapObjects.Spikes
import ru.icarumbas.bagel.Screens.Scenes.Hud
import ru.icarumbas.bagel.Utils.WorldCreate.Room
import kotlin.experimental.or

class WorldContactListener(val player: Player, val hud: Hud, val rooms: ArrayList<Room>) : ContactListener {

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        var playerBody = fixA.body
        var otherBody = fixB.body


        if (fixA.filterData.categoryBits or fixB.filterData.categoryBits == PLAYER_BIT or PLATFORM_BIT) {

            if (fixB.body == player.playerBody) {
                playerBody = fixB.body
                otherBody = fixA.body
            }

            if (playerBody.position.y < otherBody.position.y + .7 || isTouchPadDown()) {
                contact.isEnabled = false
            }
        }

        if (fixA.filterData.categoryBits or fixB.filterData.categoryBits == PLAYER_BIT or SPIKES_BIT) {

            if (fixB.body == player.playerBody) {
                otherBody = fixA.body
            }

            rooms.forEach { it.mapObjects.forEach { if (it is Spikes && it.body == otherBody) it.isTouched = true } }
            contact.isEnabled = false
        }

    }

    override fun beginContact(contact: Contact) {

    }

    override fun endContact(contact: Contact) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        var otherBody = fixB.body

        if (fixA.filterData.categoryBits or fixB.filterData.categoryBits == PLAYER_BIT or SPIKES_BIT) {

            if (fixB.body == player.playerBody) {
                otherBody = fixA.body
            }

            rooms.forEach { it.mapObjects.forEach { if (it is Spikes && it.body == otherBody) it.isTouched = false } }

        }
    }

    fun isTouchPadDown() = hud.touchpad.knobY < hud.touchpad.height / 2f - 20f

}
