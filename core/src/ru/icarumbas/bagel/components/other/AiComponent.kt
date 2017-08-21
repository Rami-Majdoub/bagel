package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component


data class AiComponent(var isPlayerRight: Boolean = false, val isPlayerUp: Boolean = false) : Component