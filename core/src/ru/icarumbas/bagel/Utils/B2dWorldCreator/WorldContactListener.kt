package ru.icarumbas.bagel.Utils.B2dWorldCreator

import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.GROUND_BIT
import ru.icarumbas.PLATFORM_BIT
import ru.icarumbas.PLAYER_BIT
import ru.icarumbas.bagel.Characters.Player
import ru.icarumbas.bagel.Screens.Scenes.Hud
import kotlin.experimental.or

class WorldContactListener(val player: Player, val hud: Hud) : ContactListener {

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB


        if (fixA.filterData.categoryBits or fixB.filterData.categoryBits == PLAYER_BIT or PLATFORM_BIT) {
            var playerBody = fixA.body
            var platformBody = fixB.body

            if (fixB.body == player.playerBody) {
                playerBody = fixB.body
                platformBody = fixA.body
            }


            if (playerBody.position.y < platformBody.position.y + .75 || isTouchPadDown()) {
                contact.isEnabled = false
            }
        }

    }

    override fun beginContact(contact: Contact) {}

    override fun endContact(contact: Contact) {}

    fun isTouchPadDown() = hud.touchpad.knobY < hud.touchpad.height / 2f - 20f

}
