package ru.icarumbas.bagel.components.other

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.utils.SerializedMapObject


data class RoomIdComponent(val id: Int, var serialized: SerializedMapObject = SerializedMapObject()) : Component