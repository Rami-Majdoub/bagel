package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.RoomIdComponent
import ru.icarumbas.bagel.components.physics.BodyComponent
import ru.icarumbas.bagel.components.physics.StaticComponent
import ru.icarumbas.bagel.components.rendering.SizeComponent
import ru.icarumbas.bagel.screens.GameScreen
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.inView


class RenderingSystem : IteratingSystem {

    private val size = Mappers.size
    private val body = Mappers.body

    private val gs : GameScreen
    private val batch: Batch
    private val entities = ArrayList<Entity>()




    constructor(gs: GameScreen, batch: Batch) : super(Family.all(
            SizeComponent::class.java,
            BodyComponent::class.java)
            .one(
                    PlayerComponent::class.java,
                    RoomIdComponent::class.java,
                    StaticComponent::class.java
            ).get()) {

        this.gs = gs
        this.batch = batch
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.filter { it.inView(gs.currentMapId, gs.rooms) }.forEach {

            batch.begin()

            batch.draw(
                    (body[it].body.userData as TextureRegion),
                    body[it].body.position.x - size[it].width/2,
                    body[it].body.position.y - size[it].height/2,
                    size[it].width,
                    size[it].height)

            batch.end()
        }

        entities.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entities.add(entity)
    }


}