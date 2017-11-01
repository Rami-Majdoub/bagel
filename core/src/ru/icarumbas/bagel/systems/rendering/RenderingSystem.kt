package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ru.icarumbas.PIX_PER_M
import ru.icarumbas.bagel.RoomManager
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.components.rendering.TextureComponent
import ru.icarumbas.bagel.components.rendering.TranslateComponent
import ru.icarumbas.bagel.utils.Mappers.Mappers.body
import ru.icarumbas.bagel.utils.Mappers.Mappers.shader
import ru.icarumbas.bagel.utils.Mappers.Mappers.size
import ru.icarumbas.bagel.utils.Mappers.Mappers.texture
import ru.icarumbas.bagel.utils.Mappers.Mappers.translate
import ru.icarumbas.bagel.utils.RenderingComparator
import ru.icarumbas.bagel.utils.inView


class RenderingSystem : SortedIteratingSystem {

    private val rm: RoomManager
    private val batch: Batch
    private val entities = ArrayList<Entity>()


    constructor(rm: RoomManager, batch: Batch) : super(Family.all(
                    SizeComponent::class.java,
                    TranslateComponent::class.java,
                    TextureComponent::class.java)
            .one(
                    AlwaysRenderingMarkerComponent::class.java,
                    RoomIdComponent::class.java,
                    StaticComponent::class.java
            ).get(), RenderingComparator()) {

        this.rm = rm
        this.batch = batch
    }

    fun draw(e: Entity){
        batch.draw(
                texture[e].tex,
                translate[e].x,
                translate[e].y,
                size[e].spriteSize.x / 2,
                size[e].spriteSize.y / 2,
                size[e].spriteSize.x,
                size[e].spriteSize.y,
                1f,
                1f,
                translate[e].angle)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.filter { it.inView(rm) && !(body.has(it) && !body[it].body.isActive) && texture[it].tex != null}.forEach {

            size[it].spriteSize.y = texture[it].tex!!.regionHeight / PIX_PER_M * size[it].scale
            size[it].spriteSize.x = texture[it].tex!!.regionWidth / PIX_PER_M * size[it].scale

            if (texture[it].color != Color.WHITE)
            batch.color = texture[it].color

            batch.begin()

            draw(it)

            if (shader.has(it)) {
                batch.shader = shader[it].shaderProgram
                draw(it)
                batch.shader = null
            }

            batch.end()

            if (batch.color != Color.WHITE)
            batch.color = Color.WHITE
        }

        entities.clear()
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        entities.add(e)
    }


}