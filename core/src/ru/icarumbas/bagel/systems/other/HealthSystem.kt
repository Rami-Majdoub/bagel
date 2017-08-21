package ru.icarumbas.bagel.systems.other

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ru.icarumbas.bagel.components.other.DamageComponent
import ru.icarumbas.bagel.components.other.ParametersComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class HealthSystem : IteratingSystem {

    val health = Mappers.damage
    val params = Mappers.params
    val gs: GameScreen

    constructor(gs: GameScreen) : super(Family.all(
            DamageComponent::class.java,
            ParametersComponent::class.java).get()) {
        this.gs = gs
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.inView(gs.currentMapId, gs.rooms)) {
            health[entity].hitTimer += deltaTime

            if (health[entity].damage != 0 && health[entity].hitTimer > .5f) {
                params[entity].HP -= health[entity].damage
                health[entity].damage = 0
            }
        }

    }
}