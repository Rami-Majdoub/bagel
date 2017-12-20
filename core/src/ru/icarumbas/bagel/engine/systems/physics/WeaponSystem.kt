package ru.icarumbas.bagel.engine.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.engine.components.other.AIComponent
import ru.icarumbas.bagel.engine.components.other.PlayerComponent
import ru.icarumbas.bagel.engine.components.physics.WeaponComponent
import ru.icarumbas.bagel.engine.controller.UIController
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*


class WeaponSystem(

        private val uiController: UIController,
        private val rm: RoomWorld

) : IteratingSystem(Family.all(WeaponComponent::class.java).one(PlayerComponent::class.java, AIComponent::class.java).get()) {



    enum class WeaponType{
        SWING,
        STUB,
        SHOT
    }


    private fun reload(e: Entity){
        when (weapon[e].type) {
            WeaponType.SWING -> {
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

            WeaponType.STUB -> {

            }

            WeaponType.SHOT -> {

            }
        }
    }

    private fun attack(e: Entity){
        if ((
                (player.has(e) && uiController.isAttackPressed())
                || (AI.has(e) && AI[e].appeared && AI[e].coldown > AI[e].refreshSpeed))
                && !weapon[e].attacking
                && damage[e].HP > 0) {


            weapon[e].attacking = true

            when (weapon[e].type) {
                WeaponType.SWING -> {

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

                WeaponType.STUB -> {

                }

                WeaponType.SHOT -> {

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