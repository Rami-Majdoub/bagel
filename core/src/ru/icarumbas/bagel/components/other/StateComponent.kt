package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.utils.ImmutableArray
import ru.icarumbas.bagel.systems.other.StateSwapSystem


data class StateComponent(var states: ImmutableArray<String>,
                          var stateTime: Float = 0f,
                          var currentState: String = StateSwapSystem.STANDING
                          ) : Component