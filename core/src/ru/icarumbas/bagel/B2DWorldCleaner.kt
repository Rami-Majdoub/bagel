package ru.icarumbas.bagel

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ru.icarumbas.bagel.utils.Mappers


class B2DWorldCleaner(val entityDeleteList: ArrayList<Entity>,
                      val bodyDeleteList: ArrayList<Body>,
                      val engine: Engine,
                      val world: World){

    private val weapon = Mappers.weapon
    private val body = Mappers.body


    private fun deleteEntities(){
        entityDeleteList.forEach {
            world.destroyBody(body[it].body)
            if (weapon.has(it)){
                world.destroyBody(weapon[it].weaponBodyLeft)
                world.destroyBody(weapon[it].weaponBodyRight)
            }
            engine.removeEntity(it)
        }
        entityDeleteList.clear()
    }

    private fun deleteBodies(){
        bodyDeleteList.forEach {
            world.destroyBody(it)
        }
    }

    fun update(){
        deleteEntities()
        deleteBodies()
    }
}