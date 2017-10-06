package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.RenderingComparator
import ru.icarumbas.bagel.utils.inView
import ru.icarumbas.bagel.utils.rotatedRight


class RenderingSystem : SortedIteratingSystem {

    private val size = Mappers.size
    private val body = Mappers.body

    private val rm: RoomManager
    private val batch: Batch
    private val entities = ArrayList<Entity>()


    constructor(rm: RoomManager, batch: Batch) : super(Family.all(
                    SizeComponent::class.java,
                    BodyComponent::class.java)
            .one(
                    AlwaysRenderingMarkerComponent::class.java,
                    RoomIdComponent::class.java,
                    StaticComponent::class.java
            ).get(), RenderingComparator()) {

        this.rm = rm
        this.batch = batch
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.filter { it.inView(rm) && body[it].body.isActive && body[it].body.userData != null}.forEach {

            if (body[it].body.userData != null) {
                size[it].spriteSize.y = (body[it].body.userData as TextureRegion).regionHeight / PIX_PER_M * size[it].scale
                size[it].spriteSize.x = (body[it].body.userData as TextureRegion).regionWidth / PIX_PER_M * size[it].scale
            }

            batch.begin()

            batch.draw(
                    (body[it].body.userData as TextureRegion),
                    body[it].body.position.x - if (it.rotatedRight()) size[it].rectSize.x / 2 else size[it].spriteSize.x - size[it].rectSize.x / 2,
                    body[it].body.position.y - size[it].rectSize.y / 2,
                    size[it].spriteSize.x / 2,
                    size[it].spriteSize.y / 2,
                    size[it].spriteSize.x,
                    size[it].spriteSize.y,
                    1f,
                    1f,
                    body[it].body.angle * MathUtils.radiansToDegrees)

            batch.end()
        }

        entities.clear()
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        entities.add(e)
    }


}