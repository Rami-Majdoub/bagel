package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.utils.ImmutableArray
import ru.icarumbas.bagel.systems.other.StateSystem


data class StateComponent(var states: ImmutableArray<String>,
                          var stateTime: Float = 0f,
                          var currentState: String = StateSystem.STANDING
                          ) : Component