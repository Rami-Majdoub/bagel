package ru.icarumbas.bagel.creators

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.components.other.AttackComponent
import ru.icarumbas.bagel.components.other.LootComponent
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.physics.WeaponComponent
import ru.icarumbas.bagel.components.rendering.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.rendering.TextureComponent
import ru.icarumbas.bagel.components.rendering.TranslateComponent
import ru.icarumbas.bagel.systems.physics.WeaponSystem
import ru.icarumbas.bagel.utils.Mappers.Mappers.body


class LootCreator(private val entityCreator: EntityCreator,
                  private val b2DWorldCreator: B2DWorldCreator,
                  private val atlas: TextureAtlas,
                  private val playerEntity: Entity){

    private val itemsTotal = 1

    fun createLoot(x: Float, y: Float, roomId: Int): Entity{
        return choose(MathUtils.random(0, itemsTotal - 1), x, y, roomId)
    }

    private fun choose(r: Int, x: Float, y: Float, roomId: Int): Entity{
        val loot = Entity()

                    .add(SizeComponent(Vector2(30 / PIX_PER_M, 100 / PIX_PER_M), 1.5f))
                    .add(TextureComponent(atlas.findRegion("sword1")))
                    .add((TranslateComponent(x - 15 / PIX_PER_M, y)))
                    .add(LootComponent(arrayListOf(
                            WeaponComponent(
                                    type = WeaponSystem.SWING,

                                    entityLeft = entityCreator.createSwingWeaponEntity(
                                            width = 30,
                                            height = 100,
                                            mainBody = body[playerEntity].body,
                                            b2DWorldCreator = b2DWorldCreator,
                                            anchorA = Vector2(0f, -.2f),
                                            anchorB = Vector2(0f, -1f),
                                            speed = 5f,
                                            maxSpeed = 3f)
                                            .add(AlwaysRenderingMarkerComponent())
                                            .add((TranslateComponent()))
                                            .add(SizeComponent(Vector2(5 / PIX_PER_M, 100 / PIX_PER_M), 2f))
                                            .add(TextureComponent(atlas.findRegion("sword1"))),

                                    entityRight = entityCreator.createSwingWeaponEntity(
                                            width = 30,
                                            height = 100,
                                            mainBody = body[playerEntity].body,
                                            b2DWorldCreator = b2DWorldCreator,
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
}