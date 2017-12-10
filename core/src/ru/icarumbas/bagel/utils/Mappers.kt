package ru.icarumbas.bagel.utils

import ru.icarumbas.bagel.engine.components.other.*
import ru.icarumbas.bagel.engine.components.physics.BodyComponent
import ru.icarumbas.bagel.engine.components.physics.InactiveMarkerComponent
import ru.icarumbas.bagel.engine.components.physics.StaticComponent
import ru.icarumbas.bagel.engine.components.physics.WeaponComponent
import ru.icarumbas.bagel.engine.components.velocity.FlyComponent
import ru.icarumbas.bagel.engine.components.velocity.JumpComponent
import ru.icarumbas.bagel.engine.components.velocity.RunComponent
import ru.icarumbas.bagel.engine.components.velocity.TeleportComponent
import ru.icarumbas.bagel.view.renderer.components.*


object Mappers {

    val damage          =       mapperFor<HealthComponent>()
    val player          =       mapperFor<PlayerComponent>()
    val roomId          =       mapperFor<RoomIdComponent>()
    val state           =       mapperFor<StateComponent>()
    val body            =       mapperFor<BodyComponent>()
    val static          =       mapperFor<StaticComponent>()
    val animation       =       mapperFor<AnimationComponent>()
    val size            =       mapperFor<SizeComponent>()
    val jump            =       mapperFor<JumpComponent>()
    val run             =       mapperFor<RunComponent>()
    val weapon          =       mapperFor<WeaponComponent>()
    val alwaysRender    =       mapperFor<AlwaysRenderingMarkerComponent>()
    val AI              =       mapperFor<AIComponent>()
    val inActive        =       mapperFor<InactiveMarkerComponent>()
    val teleport        =       mapperFor<TeleportComponent>()
    val attack          =       mapperFor<AttackComponent>()
    val texture         =       mapperFor<TextureComponent>()
    val open            =       mapperFor<OpenComponent>()
    val door            =       mapperFor<DoorComponent>()
    val loot            =       mapperFor<LootComponent>()
    val fly             =       mapperFor<FlyComponent>()
    val translate       =       mapperFor<TranslateComponent>()
    val shader          =       mapperFor<ShaderComponent>()

}

