package ru.icarumbas.bagel.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.AIComponent
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.physics.WeaponComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.angleInDegrees
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight


class WeaponSystem : IteratingSystem {

    private val hud: Hud
    private val rm: RoomManager

    private val ai = Mappers.AI
    private val player = Mappers.player
    private val weapon = Mappers.weapon
    private val body = Mappers.body
    private val damage = Mappers.damage

    companion object WeaponTypes{
        val SWING = 0
        val STUB = 1
        val SHOT = 2
    }

    constructor(hud: Hud, rm: RoomManager) : super(Family.all(
            WeaponComponent::class.java).one(
            PlayerComponent::class.java,
            AIComponent::class.java).get()) {
        this.hud = hud
        this.rm = rm
    }

    private fun reload(e: Entity){
        when (weapon[e].type) {
            SWING -> {
                if (body[weapon[e].entityRight].body.angleInDegrees() > 0){

                    body[weapon[e].entityRight].body.setTransform(
                            body[weapon[e].entityRight].body.position.x,
                            body[weapon[e].entityRight].body.position.y,
                            -.15f)

                    body[weapon[e].entityRight].body.setLinearVelocity(0f, 0f)
                    body[weapon[e].entityRight].body.isActive = false
                    weapon[e].attacking = false
                    if (ai.has(e)) ai[e].coldown = 0f

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
                    if (ai.has(e)) ai[e].coldown = 0f

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
                (player.has(e) && hud.attackButtonPressed)
                || (ai.has(e) && ai[e].appeared && ai[e].coldown > ai[e].refreshSpeed))
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
                        body[weapon[e].entityRight].body.applyAngularImpulse(-weapon[e].attackSpeed, true)
                    } else {
                        body[weapon[e].entityLeft].body.setTransform(
                                body[e].body.position.x,
                                body[e].body.position.y,
                                .2f)
                        body[weapon[e].entityLeft].body.isActive = true
                        body[weapon[e].entityLeft].body.applyAngularImpulse(weapon[e].attackSpeed, true)
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