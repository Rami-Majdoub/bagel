package ru.icarumbas.bagel.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.physics.WeaponComponent
import ru.icarumbas.bagel.screens.scenes.UInputListener
import ru.icarumbas.bagel.utils.Mappers.Mappers.AI
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.damage
import ru.icarumbas.bagel.utils.Mappers.Mappers.player
import ru.icarumbas.bagel.utils.Mappers.Mappers.weapon
import ru.icarumbas.bagel.utils.angleInDegrees
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight


class WeaponSystem : IteratingSystem {

    private val uiListener: UInputListener
    private val rm: RoomManager

    companion object WeaponTypes{
        val SWING = 0
        val STUB = 1
        val SHOT = 2
    }

    constructor(uiListener: UInputListener, rm: RoomManager) : super(Family.all(
            WeaponComponent::class.java).one(
            PlayerComponent::class.java,
            AIComponent::class.java).get()) {
        this.uiListener = uiListener
        this.rm = rm
    }

    private fun reload(e: Entity){
        when (weapon[e].type) {
            SWING -> {
                if (body[weapon[e].entityRight].body.angleInDegrees() > 0){

                    body[weapon[e].entityRight].body.setTransform(
                            body[weapon[e].entityRight].body.position.x,
                            body[weapon[e].entityRight].body.position.y,
                            -.1f)

                    body[weapon[e].entityRight].body.setLinearVelocity(0f, 0f)
                    body[weapon[e].entityRight].body.isActive = false
                    weapon[e].attacking = false
                    if (AI.has(e)) AI[e].coldown = 0f

                }
                if (body[weapon[e].entityLeft].body.angleInDegrees() < 0){

                    body[weapon[e].entityRight].body.angleInDegrees()
                    body[weapon[e].entityLeft].body.setTransform(
                            body[weapon[e].entityLeft].body.position.x,
                            body[weapon[e].entityLeft].body.position.y,
                            .1f)

                    body[weapon[e].entityLeft].body.setLinearVelocity(0f, 0f)
                    body[weapon[e].entityLeft].body.isActive = false
                    weapon[e].attacking = false
                    if (AI.has(e)) AI[e].coldown = 0f

                }

            }

            STUB -> {

            }

            SHOT -> {

            }
        }
    }

    private fun attack(e: Entity){
        if ((
                (player.has(e) && uiListener.attackButtonPressed)
                || (AI.has(e) && AI[e].appeared && AI[e].coldown > AI[e].refreshSpeed))
                && !weapon[e].attacking
                && damage[e].HP > 0) {


            weapon[e].attacking = true

            when (weapon[e].type) {
                SWING -> {

                    if (e.rotatedRight()) {
                        body[weapon[e].entityRight].body.setTransform(
                                body[e].body.position.x,
                                body[e].body.position.y,
                                -.2f)
                        body[weapon[e].entityRight].body.isActive = true
                    } else {
                        body[weapon[e].entityLeft].body.setTransform(
                                body[e].body.position.x,
                                body[e].body.position.y,
                                .2f)
                        body[weapon[e].entityLeft].body.isActive = true
                    }
                }

                STUB -> {

                }

                SHOT -> {

                }
            }
        }

    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)) {
            reload(e)
            attack(e)
        }
    }
}