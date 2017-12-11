package ru.icarumbas.bagel.engine.entities

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.engine.entities.factories.EntityFactory
import ru.icarumbas.bagel.engine.io.SerializedMapObject
import ru.icarumbas.bagel.engine.resources.ResourceManager


class EntityFromLayerLoader (

        private val entityFactory: EntityFactory,
        private val assets: ResourceManager,
        private val entitiesWorld: EntitiesWorld
) {

    fun loadStaticEntitiesFromLayer(roomPath: String, objectPath: String) {
        assets.getTiledMap(roomPath).layers[objectPath].objects.forEach {
            entitiesWorld.engine.addEntity(
                    entityFactory.staticMapObjectEntity(
                            roomPath,
                            objectPath,
                            assets.getTextureAtlas("Packs/items.pack"),
                            it)
            )
        }
    }

    fun createIdEntitiesFromLayer(roomPath: String, objectPath: String, id: Int, randomRange: Int){

        assets.getTiledMap(roomPath).layers[objectPath].objects
                .filterIsInstance<RectangleMapObject>()
                .forEach { obj ->

                    val rand = MathUtils.random(1, randomRange)
                    entityFactory.idMapObjectEntity(id, obj.rectangle, objectPath, rand, entitiesWorld.playerEntity).let {
                        entitiesWorld.engine.addEntity(it)
                        entitiesWorld.saveEntityForSerialization(SerializedMapObject(id, obj.rectangle, objectPath, rand))
                    }
                }
    }
}