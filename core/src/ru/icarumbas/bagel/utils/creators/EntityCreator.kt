package ru.icarumbas.bagel.utils.creators

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import ru.icarumbas.*
import ru.icarumbas.bagel.model.components.other.*
import ru.icarumbas.bagel.model.components.physics.BodyComponent
import ru.icarumbas.bagel.model.components.physics.InactiveMarkerComponent
import ru.icarumbas.bagel.model.components.physics.WeaponComponent
import ru.icarumbas.bagel.model.components.rendering.*
import ru.icarumbas.bagel.model.components.velocity.FlyComponent
import ru.icarumbas.bagel.model.components.velocity.JumpComponent
import ru.icarumbas.bagel.model.components.velocity.RunComponent
import ru.icarumbas.bagel.model.components.velocity.TeleportComponent
import ru.icarumbas.bagel.model.systems.other.StateSystem
import ru.icarumbas.bagel.model.systems.physics.WeaponSystem
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.createRevoluteJoint
import kotlin.experimental.or


object EntityCreator {

    fun createSwingWeaponEntity(world: World,
                                width: Int,
                                height: Int,
                                mainBody: Body,
                                anchorA: Vector2,
                                anchorB: Vector2,
                                speed: Float,
                                maxSpeed: Float): Entity {

        val weaponEntity = Entity().apply {

            add(BodyComponent(BodyCreator.createSwordWeapon(
                    world = world,
                    categoryBit = WEAPON_BIT,
                    maskBit = AI_BIT or BREAKABLE_BIT or PLAYER_BIT,
                    size = Vector2(width / PIX_PER_M, height / PIX_PER_M))
                    .createRevoluteJoint(mainBody, anchorA, anchorB, maxSpeed, speed)))
            add(InactiveMarkerComponent())
        }

        body[weaponEntity].body.userData = weaponEntity
        return weaponEntity
    }

    fun createPlayerEntity(world: World, assets: AssetManager): Entity {

        val playerBody = BodyCreator.createPlayerBody(world)

        val weaponAtlas = assets["Packs/weapons.pack", TextureAtlas::class.java]
        val playerAtlas = assets["Packs/GuyKnight.pack", TextureAtlas::class.java]

        val player = Entity()
                .add(TextureComponent())
                .add(PlayerComponent(0))
                .add(RunComponent(acceleration = .4f, maxSpeed = 6f))
                .add(SizeComponent(Vector2(50 / PIX_PER_M, 105 / PIX_PER_M), .425f))
                .add(JumpComponent(jumpVelocity = 2f, maxJumps = 5))
                .add(HealthComponent(
                        HP = 100,
                        canBeDamagedTime = 1f))
                .add(BodyComponent(playerBody))
                .add(EquipmentComponent())
                .add(WeaponComponent(
                        type = WeaponSystem.SWING,
                        entityLeft = createSwingWeaponEntity(
                                world = world,
                                width = 30,
                                height = 100,
                                mainBody = playerBody,
                                anchorA = Vector2(0f, -.2f),
                                anchorB = Vector2(0f, -1f),
                                speed = 5f,
                                maxSpeed = 3f)
                                .add(AlwaysRenderingMarkerComponent())
                                .add(TextureComponent(weaponAtlas.findRegion("eyes_sword")))
                                .add((TranslateComponent()))
                                .add(SizeComponent(Vector2(30 / PIX_PER_M, 150 / PIX_PER_M), .1f)),
                        entityRight = createSwingWeaponEntity(
                                world = world,
                                width = 30,
                                height = 100,
                                mainBody = playerBody,
                                anchorA = Vector2(0f, -.2f),
                                anchorB = Vector2(0f, -.8f),
                                speed = -5f,
                                maxSpeed = 3f)
                                .add(AlwaysRenderingMarkerComponent())
                                .add((TranslateComponent()))
                                .add(TextureComponent(weaponAtlas.findRegion("eyes_sword")))
                                .add(SizeComponent(Vector2(30 / PIX_PER_M, 150 / PIX_PER_M), .1f))))

                .add(StateComponent(ImmutableArray<String>(Array.with(
                        StateSystem.RUNNING,
                        StateSystem.JUMPING,
                        StateSystem.STANDING,
                        StateSystem.ATTACKING,
                        StateSystem.DEAD,
                        StateSystem.WALKING,
                        StateSystem.JUMP_ATTACKING
                ))))
                .add(AnimationComponent(hashMapOf(
                        StateSystem.RUNNING to AnimationCreator.create("Run", 10, .075f, playerAtlas),
                        StateSystem.JUMPING to AnimationCreator.create("Jump", 10, .15f, playerAtlas),
                        StateSystem.STANDING to AnimationCreator.create("Idle", 10, .1f, playerAtlas),
                        StateSystem.DEAD to AnimationCreator.create("Dead", 10, .1f, playerAtlas, Animation.PlayMode.NORMAL),
                        StateSystem.ATTACKING to AnimationCreator.create("Attack", 7, .075f, playerAtlas),
                        StateSystem.WALKING to AnimationCreator.create("Walk", 10, .075f, playerAtlas),
                        StateSystem.JUMP_ATTACKING to AnimationCreator.create("JumpAttack", 10, .075f, playerAtlas)
                )))
                .add(AlwaysRenderingMarkerComponent())
                .add(AttackComponent(strength = 15, knockback = Vector2(1.5f, 1f)))
                .add((TranslateComponent()))

        body[player].body.userData = player
        return player

    }

    fun createGroundEntity(world: World, obj: MapObject, bit: Short): Entity{
        return Entity()
                .add(when (obj) {
                    is RectangleMapObject -> {
                        BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                obj.rectangle,
                                BodyDef.BodyType.StaticBody,
                                obj.rectangle.width / PIX_PER_M,
                                obj.rectangle.height / PIX_PER_M,
                                bit))
                    }
                    is PolylineMapObject -> {
                        BodyComponent(BodyCreator.definePolylineMapObjectBody(
                                world,
                                obj,
                                BodyDef.BodyType.StaticBody,
                                bit))
                    }
                    else -> throw Exception()
                })

    }

    fun createLootentity(world: World,
                         atlas: TextureAtlas,
                         playerEntity: Entity,
                         r: Int,
                         x: Float,
                         y: Float,
                         roomId: Int): Entity{
        val loot = Entity()

                .add(SizeComponent(Vector2(30 / PIX_PER_M, 100 / PIX_PER_M), 1.5f))
                .add(TextureComponent(atlas.findRegion("sword1")))
                .add((TranslateComponent(x - 15 / PIX_PER_M, y)))
                .add(LootComponent(arrayListOf(
                        WeaponComponent(
                                type = WeaponSystem.SWING,

                                entityLeft = createSwingWeaponEntity(
                                        world = world,
                                        width = 30,
                                        height = 100,
                                        mainBody = body[playerEntity].body,
                                        anchorA = Vector2(0f, -.2f),
                                        anchorB = Vector2(0f, -1f),
                                        speed = 5f,
                                        maxSpeed = 3f)
                                        .add(AlwaysRenderingMarkerComponent())
                                        .add((TranslateComponent()))
                                        .add(SizeComponent(Vector2(5 / PIX_PER_M, 100 / PIX_PER_M), 2f))
                                        .add(TextureComponent(atlas.findRegion("sword1"))),

                                entityRight = createSwingWeaponEntity(
                                        world = world,
                                        width = 30,
                                        height = 100,
                                        mainBody = body[playerEntity].body,
                                        anchorA = Vector2(0f, -.2f),
                                        anchorB = Vector2(0f, -.8f),
                                        speed = -5f,
                                        maxSpeed = 3f)
                                        .add(AlwaysRenderingMarkerComponent())
                                        .add((TranslateComponent()))
                                        .add(TextureComponent(atlas.findRegion("sword1")))
                                        .add(SizeComponent(Vector2(5 / PIX_PER_M, 100 / PIX_PER_M), 2f))
                        ),
                        AttackComponent(strength = 30, knockback = Vector2(2f, 2f))

                )))

        loot.add(RoomIdComponent(roomId))
//        loot.add(ShaderComponent(ShaderProgram(FileHandle("Shaders/bleak.vert"), FileHandle("Shaders/bleak.frag"))))
        return loot
    }

    fun loadIdEntity(world: World,
                     assets: AssetManager,
                     roomId: Int,
                     rect: Rectangle,
                     objectPath: String,
                     atlas: TextureAtlas,
                     r: Int,
                     playerEntity: Entity): Boolean {

        val e = when(objectPath){

            "vase" -> {
                val size = when (r) {
                    1 -> Pair(132, 171)
                    2 -> Pair(65, 106)
                    3 -> Pair(120, 162)
                    4 -> Pair(98, 72)
                    else -> return false
                }

                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                size.first / PIX_PER_M,
                                size.second / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(SizeComponent(Vector2(size.first / PIX_PER_M, size.second / PIX_PER_M)))
                        .add(TextureComponent(atlas.findRegion("Vase ($r)")))
            }

            "window" -> {
                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                86 / PIX_PER_M,
                                169 / PIX_PER_M,
                                STATIC_BIT,
                                WEAPON_BIT)))
                        .add(SizeComponent(Vector2(86 / PIX_PER_M, 169 / PIX_PER_M)))
                        .add(TextureComponent(atlas.findRegion("Window Small ($r)")))
            }

            "chair1" -> {
                if (r == 2) return false
                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                70 / PIX_PER_M,
                                128 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(SizeComponent(Vector2(70 / PIX_PER_M, 128 / PIX_PER_M)))
                        .add(TextureComponent(atlas.findRegion("Chair (1)")))
            }

            "chair2" -> {
                if (r == 2) return false
                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                70 / PIX_PER_M,
                                128 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(SizeComponent(Vector2(70 / PIX_PER_M, 128 / PIX_PER_M)))
                        .add(TextureComponent(atlas.findRegion("Chair (2)")))
            }

            "table" -> {
                if (r == 2) return false
                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                137 / PIX_PER_M,
                                69 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(SizeComponent(Vector2(137 / PIX_PER_M, 69 / PIX_PER_M)))
                        .add(TextureComponent(atlas.findRegion("Table")))
            }

            "chandelier" -> {
                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                243 / PIX_PER_M,
                                120 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(AnimationComponent(hashMapOf(StateSystem.STANDING to
                                AnimationCreator.create("Chandelier", 4, .125f, atlas))))
                        .add(StateComponent(
                                ImmutableArray(Array.with(StateSystem.STANDING)),
                                MathUtils.random()
                        ))
                        .add(SizeComponent(Vector2(243 / PIX_PER_M, 120 / PIX_PER_M)))
                        .add(TextureComponent())
            }

            "candle" -> {
                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                178 / PIX_PER_M,
                                208 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(AnimationComponent(hashMapOf(StateSystem.STANDING to
                                AnimationCreator.create("Candle", 4, .125f, atlas))))
                        .add(StateComponent(
                                ImmutableArray(Array.with(StateSystem.STANDING)),
                                MathUtils.random()
                        ))
                        .add(SizeComponent(Vector2(178 / PIX_PER_M, 208 / PIX_PER_M)))
                        .add(TextureComponent())
            }

            "smallBanner" -> {

                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                126 / PIX_PER_M,
                                180 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(SizeComponent(Vector2(126 / PIX_PER_M, 180 / PIX_PER_M)))
                        .add(HealthComponent(5))
                        .add(TextureComponent(atlas.findRegion("Banner ($r)")))
            }

            "chest" -> {
                if (r == 2) return false

                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                96 / PIX_PER_M,
                                96 / PIX_PER_M,
                                KEY_OPEN_BIT,
                                PLAYER_BIT)))
                        .add(SizeComponent(Vector2(96 / PIX_PER_M, 96 / PIX_PER_M)))
                        .add(AnimationComponent(hashMapOf(
                                StateSystem.STANDING to
                                        AnimationCreator.create("Chest", 1, .125f, atlas),
                                StateSystem.OPENING to
                                        AnimationCreator.create("Chest", 4, .125f, atlas))))
                        .add(StateComponent(
                                ImmutableArray(Array.with(StateSystem.STANDING, StateSystem.OPENING)),
                                0f
                        ))
                        .add(TextureComponent(atlas.findRegion("Chest (1)")))
                        .add(OpenComponent())
            }

            "door" -> {

                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                170 / PIX_PER_M,
                                244 / PIX_PER_M,
                                KEY_OPEN_BIT,
                                PLAYER_BIT)))
                        .add(SizeComponent(Vector2(170 / PIX_PER_M, 244 / PIX_PER_M)))
                        .add(AnimationComponent(
                                if (r == 1) {
                                    hashMapOf(
                                            StateSystem.STANDING to
                                                    AnimationCreator.create("IronDoor", 1, .125f, atlas),
                                            StateSystem.OPENING to
                                                    AnimationCreator.create("IronDoor", 4, .125f, atlas)
                                    )
                                }
                                else {
                                    hashMapOf(
                                            StateSystem.STANDING to
                                                    AnimationCreator.create("Wood Door", 1, .125f, atlas),
                                            StateSystem.OPENING to
                                                    AnimationCreator.create("Wood Door", 4, .125f, atlas)
                                    )
                                }
                        ))
                        .add(StateComponent(
                                ImmutableArray(Array.with(StateSystem.STANDING, StateSystem.OPENING)),
                                0f
                        ))
                        .add(TextureComponent())
                        .add(OpenComponent())
                        .add(DoorComponent())

            }

            "crateBarrel" -> {
                if (r == 3) return false

                Entity()
                        .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.StaticBody,
                                if (r == 1) 106 / PIX_PER_M else 119 / PIX_PER_M,
                                if (r == 1) 106 / PIX_PER_M else 133 / PIX_PER_M,
                                BREAKABLE_BIT,
                                WEAPON_BIT)))
                        .add(HealthComponent(5))
                        .add(SizeComponent(Vector2(
                                if (r == 1) 106 / PIX_PER_M else 119 / PIX_PER_M,
                                if (r == 1) 106 / PIX_PER_M else 133 / PIX_PER_M)))
                        .add(TextureComponent(atlas.findRegion(if (r == 1) "Crate" else "Barrel")))


            }

            "flyingEnemy" -> {

                when (r) {
                    1 -> {

                        Entity()
                                .add(BodyComponent(BodyCreator.defineRectangleMapObjectBody(
                                        world,
                                        rect,
                                        BodyDef.BodyType.DynamicBody,
                                        64 / PIX_PER_M,
                                        64 / PIX_PER_M,
                                        AI_BIT,
                                        PLAYER_BIT or WEAPON_BIT,
                                        true,
                                        0f)))
                                .add(HealthComponent(roomId + 20))
                                .add(AnimationComponent(hashMapOf(
                                        StateSystem.STANDING to Animation(
                                                .1f,
                                                assets["Packs/Enemies/MiniDragon.pack", TextureAtlas::class.java]
                                                        .findRegions("miniDragon"),
                                                Animation.PlayMode.LOOP)
                                )))
                                .add(StateComponent(ImmutableArray(Array.with(StateSystem.STANDING))))
                                .add(AIComponent(refreshSpeed = MathUtils.random(.2f, .3f), attackDistance = 1f, entityTarget = playerEntity))
                                .add(SizeComponent(Vector2(64 / PIX_PER_M, 64 / PIX_PER_M), .15f))
                                .add(AttackComponent(strength = roomId + 15, knockback = Vector2(2f, 2f)))
                                .add(TextureComponent())
                                .add(FlyComponent(.005f))
                    }

                    else -> return false
                }
            }

            "groundEnemy" -> {

                when (r) {
                    1 -> {

                        val skeletonAtlas = assets["Packs/Enemies/Skeleton.pack", TextureAtlas::class.java]
                        val body = BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.DynamicBody,
                                85 / PIX_PER_M,
                                192 / PIX_PER_M,
                                AI_BIT,
                                WEAPON_BIT or GROUND_BIT or PLATFORM_BIT)

                        Entity()
                                .add(BodyComponent(body))
                                .add(HealthComponent(roomId + 40))
                                .add(AnimationComponent(hashMapOf(
                                        StateSystem.STANDING to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("idle"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.ATTACKING to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("hit"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.DEAD to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("die"),
                                                Animation.PlayMode.NORMAL),
                                        StateSystem.APPEARING to Animation(
                                                .1f,
                                                skeletonAtlas.findRegions("appear"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.WALKING to Animation(
                                                .125f,
                                                skeletonAtlas.findRegions("go"),
                                                Animation.PlayMode.LOOP))
                                ))
                                .add(StateComponent(
                                        ImmutableArray(Array.with(
                                                StateSystem.STANDING,
                                                StateSystem.ATTACKING,
                                                StateSystem.DEAD,
                                                StateSystem.APPEARING,
                                                StateSystem.WALKING))
                                ))
                                .add(RunComponent(.25f, 1f))
                                .add(AIComponent(refreshSpeed = MathUtils.random(.2f, .3f), attackDistance = 1f, entityTarget = playerEntity))
                                .add(SizeComponent(Vector2(180 / PIX_PER_M, 230 / PIX_PER_M), 1f))
                                .add(WeaponComponent(
                                        type = WeaponSystem.SWING,
                                        entityLeft = createSwingWeaponEntity(
                                                world,
                                                35,
                                                135,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -.5f),
                                                speed = 4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId)),
                                        entityRight = createSwingWeaponEntity(
                                                world,
                                                35,
                                                135,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -.5f),
                                                speed = -4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId))))
                                .add(AttackComponent(strength = roomId + 15, knockback = Vector2(2.5f, 2.5f)))
                                .add(TextureComponent())


                    }

                    2 -> {

                        val golemAtlas = assets["Packs/Enemies/Golem.pack", TextureAtlas::class.java]
                        val body = BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.DynamicBody,
                                230 / PIX_PER_M,
                                230 / PIX_PER_M,
                                AI_BIT,
                                WEAPON_BIT or GROUND_BIT or PLATFORM_BIT)

                        Entity()
                                .add(BodyComponent(body))
                                .add(HealthComponent(roomId + 80))
                                .add(AnimationComponent(hashMapOf(
                                        StateSystem.STANDING to Animation(
                                                .125f,
                                                golemAtlas.findRegions("idle"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.ATTACKING to Animation(
                                                .125f,
                                                golemAtlas.findRegions("hit"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.DEAD to Animation(
                                                .15f,
                                                golemAtlas.findRegions("die"),
                                                Animation.PlayMode.NORMAL),
                                        StateSystem.APPEARING to Animation(
                                                .1f,
                                                golemAtlas.findRegions("appear"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.WALKING to Animation(
                                                .1f,
                                                golemAtlas.findRegions("idle"),
                                                Animation.PlayMode.LOOP))
                                ))
                                .add(StateComponent(
                                        ImmutableArray(Array.with(
                                                StateSystem.STANDING,
                                                StateSystem.ATTACKING,
                                                StateSystem.DEAD,
                                                StateSystem.APPEARING,
                                                StateSystem.WALKING))
                                ))
                                .add(RunComponent(.5f, .5f))
                                .add(AIComponent(refreshSpeed = MathUtils.random(.45f, .55f), attackDistance = 2f, entityTarget = playerEntity))
                                .add(SizeComponent(Vector2(230 / PIX_PER_M, 230 / PIX_PER_M), 1f))
                                .add(WeaponComponent(
                                        type = WeaponSystem.SWING,
                                        entityLeft = createSwingWeaponEntity(
                                                world,
                                                50,
                                                215,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -1f),
                                                speed = 4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId)),
                                        entityRight = createSwingWeaponEntity(
                                                world,
                                                50,
                                                215,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -1f),
                                                speed = -4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId))))
                                .add(AttackComponent(strength = roomId + 25, knockback = Vector2(3.5f, 3.5f)))
                                .add(TextureComponent())


                    }

                    3 -> {
                        val zombieAtlas = assets["Packs/Enemies/Zombie.pack", TextureAtlas::class.java]
                        val body = BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.DynamicBody,
                                85 / PIX_PER_M,
                                192 / PIX_PER_M,
                                AI_BIT,
                                WEAPON_BIT or GROUND_BIT or PLATFORM_BIT or SHARP_BIT)

                        Entity()
                                .add(BodyComponent(body))
                                .add(HealthComponent(roomId + 30))
                                .add(AnimationComponent(hashMapOf(
                                        StateSystem.STANDING to Animation(
                                                .125f,
                                                zombieAtlas.findRegions("idle"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.ATTACKING to Animation(
                                                .125f,
                                                zombieAtlas.findRegions("hit"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.DEAD to Animation(
                                                .125f,
                                                zombieAtlas.findRegions("die"),
                                                Animation.PlayMode.NORMAL),
                                        StateSystem.APPEARING to Animation(
                                                .1f,
                                                zombieAtlas.findRegions("appear"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.WALKING to Animation(
                                                .075f,
                                                zombieAtlas.findRegions("go"),
                                                Animation.PlayMode.LOOP))
                                ))
                                .add(StateComponent(
                                        ImmutableArray(Array.with(
                                                StateSystem.STANDING,
                                                StateSystem.ATTACKING,
                                                StateSystem.DEAD,
                                                StateSystem.APPEARING,
                                                StateSystem.WALKING))
                                ))
                                .add(RunComponent(.25f, 2f))
                                .add(AIComponent(refreshSpeed = MathUtils.random(.1f, .2f), attackDistance = 1f, entityTarget = playerEntity))
                                .add(SizeComponent(Vector2(125 / PIX_PER_M, 202 / PIX_PER_M), 1f))
                                .add(WeaponComponent(
                                        type = WeaponSystem.SWING,
                                        entityLeft = createSwingWeaponEntity(
                                                world,
                                                35,
                                                110,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -.5f),
                                                speed = 4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId)),
                                        entityRight = createSwingWeaponEntity(
                                                world,
                                                35,
                                                110,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -.5f),
                                                speed = -4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId))))
                                .add(AttackComponent(strength = roomId + 10, knockback = Vector2(2.5f, 2.5f)))
                                .add(TextureComponent())


                    }

                    4 -> {
                        val vampAtlas = assets["Packs/Enemies/Vamp.pack", TextureAtlas::class.java]
                        val body = BodyCreator.defineRectangleMapObjectBody(
                                world,
                                rect,
                                BodyDef.BodyType.DynamicBody,
                                85 / PIX_PER_M,
                                192 / PIX_PER_M,
                                AI_BIT,
                                WEAPON_BIT or GROUND_BIT or PLATFORM_BIT or SHARP_BIT)

                        Entity()
                                .add(BodyComponent(body))
                                .add(HealthComponent(roomId + 20))
                                .add(AnimationComponent(hashMapOf(
                                        StateSystem.STANDING to Animation(
                                                .125f,
                                                vampAtlas.findRegions("go"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.ATTACKING to Animation(
                                                .125f,
                                                vampAtlas.findRegions("hit"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.DEAD to Animation(
                                                .125f,
                                                vampAtlas.findRegions("appear"),
                                                Animation.PlayMode.NORMAL),
                                        StateSystem.APPEARING to Animation(
                                                .1f,
                                                vampAtlas.findRegions("appear").apply { reverse() },
                                                Animation.PlayMode.LOOP),
                                        StateSystem.WALKING to Animation(
                                                .1f,
                                                vampAtlas.findRegions("go"),
                                                Animation.PlayMode.LOOP),
                                        StateSystem.DISAPPEARING to Animation(
                                                .1f,
                                                vampAtlas.findRegions("appear"),
                                                Animation.PlayMode.LOOP))
                                ))
                                .add(StateComponent(
                                        ImmutableArray(Array.with(
                                                StateSystem.STANDING,
                                                StateSystem.ATTACKING,
                                                StateSystem.DEAD,
                                                StateSystem.APPEARING,
                                                StateSystem.WALKING,
                                                StateSystem.DISAPPEARING))
                                ))
                                .add(RunComponent(.225f, .75f))
                                .add(AIComponent(refreshSpeed = MathUtils.random(.7f, 1f), attackDistance = 2f, entityTarget = playerEntity))
                                .add(SizeComponent(Vector2(85 / PIX_PER_M, 202 / PIX_PER_M), 1f))
                                .add(WeaponComponent(
                                        type = WeaponSystem.SWING,
                                        entityLeft = createSwingWeaponEntity(
                                                world,
                                                35,
                                                220,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -1f),
                                                speed = 4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId)),
                                        entityRight = createSwingWeaponEntity(
                                                world,
                                                35,
                                                220,
                                                body,
                                                Vector2(0f, -.3f),
                                                Vector2(0f, -1f),
                                                speed = -4f,
                                                maxSpeed = 2f)
                                                .add(RoomIdComponent(roomId))))
                                .add(TeleportComponent())
                                .add(AttackComponent(strength = roomId + 15, knockback = Vector2(1.5f, 1.5f)))
                                .add(TextureComponent())


                    }

                    else -> return false
                }
            }

            else -> throw Exception("NO SUCH CLASS: $objectPath")
        }

        e.add(TranslateComponent())
        e.add(RoomIdComponent(roomId))

        body[e].body.userData = e
        return true
    }

}
