package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.bagel.utils.Mappers


class WorldCleaner(private val entityDeleteList: ArrayList<Entity>,
                   private val engine: Engine,
                   private val world: World){

    private val weapon = Mappers.weapon
    private val body = Mappers.body


    private fun deleteEntities(){
        entityDeleteList.forEach {
            world.destroyBody(body[it].body)
            if (weapon.has(it)){
                world.destroyBody(body[weapon[it].entityLeft].body)
                world.destroyBody(body[weapon[it].entityRight].body)
            }
            engine.removeEntity(it)
        }
        entityDeleteList.clear()
    }

    fun update(){
        deleteEntities()
    }
}