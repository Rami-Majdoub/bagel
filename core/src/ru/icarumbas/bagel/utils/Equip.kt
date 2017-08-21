package ru.icarumbas.bagel.utils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.icarumbas.bagel.components.rendering.EquipmentSystem

class Equip(val texture: TextureRegion,
                 val type : EquipmentSystem.EquipmentTypes,
                 val hpUpgrade: Float = 1f,
                 val strengthUpgrade: Float = 1f,
                 val speedUpgrade: Float = 1f,
                 val knockBackUpgrade: Float = 1f,
                 val attackSpeed: Float = 1f)
