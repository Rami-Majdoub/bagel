package ru.icarumbas.bagel.Tools.B2dWorldCreator

import com.badlogic.gdx.physics.box2d.*
import ru.icarumbas.bagel.Screens.GameScreen
import kotlin.experimental.or

class WorldContactListener(val gameScreen: GameScreen) : ContactListener, ContactFilter {

    var isContact: Boolean = false

    override fun shouldCollide(fixtureA: Fixture, fixtureB: Fixture): Boolean {
        return  gameScreen.player.playerBody!!.linearVelocity.y < -3.5f ||
                fixtureA.filterData.categoryBits != gameScreen.PLATFORM_BIT && fixtureB.filterData.categoryBits != gameScreen.PLATFORM_BIT
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {

    }

    override fun beginContact(contact: Contact) {
        val fixA = contact.fixtureA.filterData.categoryBits
        val fixB = contact.fixtureB.filterData.categoryBits
        when (fixA or fixB){
            gameScreen.PLAYER_BIT or gameScreen.PLATFORM_BIT -> isContact = true
        }
    }

    override fun endContact(contact: Contact) {
        val fixA = contact.fixtureA.filterData.categoryBits
        val fixB = contact.fixtureB.filterData.categoryBits
        when (fixA or fixB){
            gameScreen.PLAYER_BIT or gameScreen.PLATFORM_BIT -> isContact = false
        }
    }

    fun update () {

        if (isContact && gameScreen.hud.touchpad.knobY < gameScreen.hud.touchpad.height/2f - 20f) {
            for (body in gameScreen.mapGenerator.platformArrays[gameScreen.mapGenerator.currentMap]) body.isActive = false
        }
        if (!isContact && gameScreen.player.playerBody!!.linearVelocity.y < -5f){
            for (body in gameScreen.mapGenerator.platformArrays[gameScreen.mapGenerator.currentMap]) body.isActive = true
        }
    }

}
