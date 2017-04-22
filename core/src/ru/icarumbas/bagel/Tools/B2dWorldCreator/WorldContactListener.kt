package ru.icarumbas.bagel.Tools.B2dWorldCreator

import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.bagel.Screens.GameScreen
import kotlin.experimental.or

class WorldContactListener(val gameScreen: GameScreen) : ContactListener, ContactFilter {

    var isContact = false

    override fun shouldCollide(fixtureA: Fixture, fixtureB: Fixture): Boolean {
        var playerBody = fixtureA.body
        var otherBody = fixtureB.body

        if (fixtureA.filterData.categoryBits or fixtureB.filterData.categoryBits == gameScreen.GROUND_BIT) return true

        if (fixtureB.body == gameScreen.player.playerBody){
            playerBody = fixtureB.body
            otherBody = fixtureA.body
        }

        return playerBody.position.y - gameScreen.player.playerBodyHeight / 2 > otherBody.position.y

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun beginContact(contact: Contact) {
        val fixA = contact.fixtureA.filterData.categoryBits
        val fixB = contact.fixtureB.filterData.categoryBits
        when (fixA or fixB) {
            gameScreen.PLAYER_BIT or gameScreen.PLATFORM_BIT -> isContact = true

        }
    }

    override fun endContact(contact: Contact) {
        val fixA = contact.fixtureA.filterData.categoryBits
        val fixB = contact.fixtureB.filterData.categoryBits
        when (fixA or fixB) {
            gameScreen.PLAYER_BIT or gameScreen.PLATFORM_BIT -> isContact = false

        }
    }

    fun update() {

        if (isContact && isTouchPadDown()) {
            gameScreen.worldCreator.rooms[gameScreen.worldCreator.currentMap].setPlatformsActivity(false)
        }
        if (!isContact && !isTouchPadDown()) {
            gameScreen.worldCreator.rooms[gameScreen.worldCreator.currentMap].setPlatformsActivity(true)

        }
    }

    fun isTouchPadDown() = gameScreen.hud.touchpad.knobY < gameScreen.hud.touchpad.height / 2f - 20f

}
