package ru.icarumbas.bagel.engine.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.utils.ImmutableArray
import ru.icarumbas.bagel.engine.entities.EntityState


class StateComponent(var states: ImmutableArray<EntityState>, var stateTime: Float = 0f) : Component {
    var currentState: EntityState = EntityState.STANDING
}