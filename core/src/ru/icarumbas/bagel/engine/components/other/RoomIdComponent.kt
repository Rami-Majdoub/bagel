package ru.icarumbas.bagel.engine.components.other

import com.badlogic.ashley.core.Component
import ru.icarumbas.bagel.engine.io.SerializedMapObject


class RoomIdComponent(val id: Int, var serialized: SerializedMapObject = SerializedMapObject()) : Component