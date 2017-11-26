package ru.icarumbas.bagel.model.components.other

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.SerializedMapObject


class RoomIdComponent(val id: Int, var serialized: SerializedMapObject = SerializedMapObject()) : Component