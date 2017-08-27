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
                    if (weapon[e].weaponBodyRight?.angleInDegrees()!! > 0){
                        weapon[e].weaponBodyRight?.setTransform(
                                weapon[e].weaponBodyRight?.position?.x!!,
                                weapon[e].weaponBodyRight?.position?.y!!,
                                -.1f)
                        weapon[e].weaponBodyRight?.setLinearVelocity(0f, 0f)
                        weapon[e].weaponBodyRight?.isActive = false
                        weapon[e].attacking = false

                    }
                } else {
                    if (weapon[e].weaponBodyLeft?.angleInDegrees()!! < 0){
                        weapon[e].weaponBodyLeft?.setTransform(
                                weapon[e].weaponBodyLeft?.position?.x!!,
                                weapon[e].weaponBodyLeft?.position?.y!!,
                                .1f)
                        weapon[e].weaponBodyLeft?.setLinearVelocity(0f, 0f)
                        weapon[e].weaponBodyLeft?.isActive = false
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
                        weapon[e].weaponBodyRight?.isActive = true
                        weapon[e].weaponBodyRight?.applyAngularImpulse(-.00025f, true)
                    } else {
                        weapon[e].weaponBodyLeft?.isActive = true
                        weapon[e].weaponBodyLeft?.applyAngularImpulse(.00025f, true)
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