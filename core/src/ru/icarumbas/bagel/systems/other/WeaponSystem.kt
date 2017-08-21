package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.components.other.AiComponent
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.WeaponComponent
import ru.icarumbas.bagel.screens.scenes.Hud
import ru.icarumbas.bagel.utils.Mappers


class WeaponSystem : IteratingSystem {

    val hud: Hud

    val ai = Mappers.ai
    val run = Mappers.run
    val weapon = Mappers.weapon

    companion object WeaponTypes{
        val SWING = "SWING"
        val STUB = "STUB"
        val SHOT = "SHOT"
    }

    constructor(hud: Hud) : super(Family.all(
            WeaponComponent::class.java).one(
            PlayerComponent::class.java,
            AiComponent::class.java).get()) {
        this.hud = hud
    }

    private fun raise(e: Entity){
        weapon[e].weaponBody!!.setTransform(
                weapon[e].weaponBody!!.position.x,
                weapon[e].weaponBody!!.position.y,
                0f)

    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ((run.has(entity) && !run[entity].lastRight) || (ai.has(entity) && ai[entity].isPlayerRight)){
        }

    }
}