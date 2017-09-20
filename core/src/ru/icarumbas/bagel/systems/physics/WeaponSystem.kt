package ru.icarumbas.bagel.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.WeaponComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.angleInDegrees
import ru.icarumbas.bagel.utils.rotatedRight


class WeaponSystem : IteratingSystem {

    private val hud: Hud

    private val ai = Mappers.AI
    private val player = Mappers.player
    private val weapon = Mappers.weapon
    private val attackMapper = Mappers.attack
    private val body = Mappers.body

    companion object WeaponTypes{
        val SWING = 0
        val STUB = 1
        val SHOT = 2
    }

    constructor(hud: Hud) : super(Family.all(
            WeaponComponent::class.java).one(
            PlayerComponent::class.java,
            AIComponent::class.java).get()) {
        this.hud = hud
    }

    private fun reload(e: Entity){
        when (weapon[e].type) {
            SWING -> {
                if (e.rotatedRight()) {
                    if (body[weapon[e].entityRight].body.angleInDegrees() > 0){
                        body[weapon[e].entityRight].body.setTransform(
                                body[weapon[e].entityRight].body.position.x,
                                body[weapon[e].entityRight].body.position.y,
                                -.1f)
                        body[weapon[e].entityRight].body.setLinearVelocity(0f, 0f)
                        body[weapon[e].entityRight].body.isActive = false
                        attackMapper[e].attacking = false

                    }
                } else {
                    if (body[weapon[e].entityLeft].body.angleInDegrees() < 0){
                        body[weapon[e].entityLeft].body.setTransform(
                                body[weapon[e].entityLeft].body.position.x,
                                body[weapon[e].entityLeft].body.position.y,
                                .1f)
                        body[weapon[e].entityLeft].body.setLinearVelocity(0f, 0f)
                        body[weapon[e].entityLeft].body.isActive = false
                        attackMapper[e].attacking = false
                    }
                }
            }

            STUB -> {

            }

            SHOT -> {

            }
        }
    }

    private fun attack(e: Entity){
        if (((player.has(e) && hud.attackButtonPressed) || (ai.has(e) && ai[e].readyAttack)) && !attackMapper[e].attacking) {
            attackMapper[e].attacking = true
            when (weapon[e].type) {
                SWING -> {
                    if (e.rotatedRight()) {
                        body[weapon[e].entityRight].body.setTransform(
                                body[e].body.position.x,
                                body[e].body.position.y,
                                -.1f)
                        body[weapon[e].entityRight].body.isActive = true
                        body[weapon[e].entityRight].body.applyAngularImpulse(-attackMapper[e].attackSpeed, true)
                    } else {
                        body[weapon[e].entityLeft].body.setTransform(
                                body[e].body.position.x,
                                body[e].body.position.y,
                                .1f)
                        body[weapon[e].entityLeft].body.isActive = true
                        body[weapon[e].entityLeft].body.applyAngularImpulse(attackMapper[e].attackSpeed, true)
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
        reload(e)
        attack(e)
    }
}