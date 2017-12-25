package ru.icarumbas.bagel.view.renderer.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ru.icarumbas.bagel.engine.components.other.RoomIdComponent
import ru.icarumbas.bagel.engine.components.physics.StaticComponent
import ru.icarumbas.bagel.engine.world.PIX_PER_M
import ru.icarumbas.bagel.engine.world.RoomWorld
import ru.icarumbas.bagel.utils.*
import ru.icarumbas.bagel.view.renderer.RenderingComparator
import ru.icarumbas.bagel.view.renderer.components.AlwaysRenderingMarkerComponent
import ru.icarumbas.bagel.view.renderer.components.SizeComponent
import ru.icarumbas.bagel.view.renderer.components.TextureComponent
import ru.icarumbas.bagel.view.renderer.components.TranslateComponent


class RenderingSystem(

        private val rm: RoomWorld,
        private val batch: Batch

) : SortedIteratingSystem(Family.all(SizeComponent::class.java,
        TranslateComponent::class.java,
        TextureComponent::class.java)
        .one(AlwaysRenderingMarkerComponent::class.java, RoomIdComponent::class.java,StaticComponent::class.java).get(),
        RenderingComparator()) {

    private val entities = ArrayList<Entity>()

    private fun draw(e: Entity){
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

        entities.filter { it.inView(rm) && !(body.has(it) && !body[it].body.isActive) && texture[it].tex != null }.forEach {

            size[it].spriteSize.y = texture[it].tex!!.regionHeight / PIX_PER_M * size[it].scale
            size[it].spriteSize.x = texture[it].tex!!.regionWidth / PIX_PER_M * size[it].scale

            batch.color = texture[it].color

            batch.begin()

            draw(it)

            if (shader.has(it)) {
                batch.shader = shader[it].shaderProgram
                draw(it)
                batch.shader = null
            }

            batch.end()

            batch.color = Color.WHITE
        }

        entities.clear()
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        entities.add(e)
    }


}