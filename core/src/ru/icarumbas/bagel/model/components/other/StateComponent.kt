package ru.icarumbas.bagel.model.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.utils.ImmutableArray
import ru.icarumbas.bagel.model.systems.other.StateSystem


class StateComponent(var states: ImmutableArray<String>, var stateTime: Float = 0f) : Component {
    var currentState: String = StateSystem.STANDING
}