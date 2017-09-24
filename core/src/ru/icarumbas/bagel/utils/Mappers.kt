package ru.icarumbas.bagel.utils

import com.badlogic.ashley.core.ComponentMapper
import ru.icarumbas.bagel.components.other.*
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AnimationComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.velocity.FlyComponent
import ru.icarumbas.bagel.components.velocity.JumpComponent
import ru.icarumbas.bagel.components.velocity.RunComponent


class Mappers {

    companion object Mappers {

        val damage          =       ComponentMapper.getFor(DamageComponent::class.java)!!
        val player          =       ComponentMapper.getFor(PlayerComponent::class.java)!!
        val roomId          =       ComponentMapper.getFor(RoomIdComponent::class.java)!!
        val state           =       ComponentMapper.getFor(StateComponent::class.java)!!
        val body            =       ComponentMapper.getFor(BodyComponent::class.java)!!
        val static          =       ComponentMapper.getFor(StaticComponent::class.java)!!
        val animation       =       ComponentMapper.getFor(AnimationComponent::class.java)!!
        val size            =       ComponentMapper.getFor(SizeComponent::class.java)!!
        val fly             =       ComponentMapper.getFor(FlyComponent::class.java)!!
        val jump            =       ComponentMapper.getFor(JumpComponent::class.java)!!
        val run             =       ComponentMapper.getFor(RunComponent::class.java)!!
        val equipment       =       ComponentMapper.getFor(EquipmentComponent::class.java)!!
        val weapon          =       ComponentMapper.getFor(WeaponComponent::class.java)!!
        val alwaysRender    =       ComponentMapper.getFor(AlwaysRenderingMarkerComponent::class.java)!!
        val AI              =       ComponentMapper.getFor(AIComponent::class.java)!!
    }
}
