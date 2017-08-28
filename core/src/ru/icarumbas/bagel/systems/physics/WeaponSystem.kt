package ru.icarumbas.bagel.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.components.other.AiComponent
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.WeaponComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.angleInDegrees
import ru.icarumbas.bagel.utils.rotatedRight


class WeaponSystem : IteratingSystem {

    val hud: Hud

    val ai = Mappers.ai
    val player = Mappers.player
    val weapon = Mappers.weapon
    val body = Mappers.body

    companion object WeaponTypes{
        val SWING = 0
        val STUB = 1
        val SHOT = 2
    }

    constructor(hud: Hud) : super(Family.all(
            WeaponComponent::class.java).one(
            PlayerComponent::class.java,
            AiComponent::class.java).get()) {
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
                        weapon[e].attacking = false

                    }
                } else {
                    if (body[weapon[e].entityLeft].body.angleInDegrees() < 0){
                        body[weapon[e].entityLeft].body.setTransform(
                                body[weapon[e].entityLeft].body.position.x,
                                body[weapon[e].entityLeft].body.position.y,
                                .1f)
                        body[weapon[e].entityLeft].body.setLinearVelocity(0f, 0f)
                        body[weapon[e].entityLeft].body.isActive = false
                        weapon[e].attacking = false
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
        if (((player.has(e) && hud.attackButtonPressed) || (ai.has(e) && ai[e].readyAttack)) && !weapon[e].attacking) {
            weapon[e].attacking = true
            when (weapon[e].type) {
                SWING -> {
                    if (e.rotatedRight()) {
                        body[weapon[e].entityRight].body.isActive = true
                        body[weapon[e].entityRight].body.applyAngularImpulse(-.00025f, true)
                    } else {
                        body[weapon[e].entityLeft].body.isActive = true
                        body[weapon[e].entityLeft].body.applyAngularImpulse(.00025f, true)
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