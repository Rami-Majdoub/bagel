package ru.icarumbas.bagel.systems.rendering

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.icarumbas.bagel.components.other.WeaponComponent
import ru.icarumbas.bagel.utils.Mappers


class WeaponRenderingSystem : IteratingSystem{

    private val weapon = Mappers.weapon
    private val batch: Batch

    constructor(batch: Batch) : super(Family.one(WeaponComponent::class.java).get()) {
        this.batch = batch
    }

    override fun processEntity(e: Entity, deltaTime: Float) {

        if (weapon[e].weaponBody?.userData != null) {
            val weaponTex = weapon[e].weaponBody?.userData as TextureRegion
            batch.draw(
                    weaponTex,
                    weapon[e].weaponBody!!.position.x - weaponTex.regionWidth / 2,
                    weapon[e].weaponBody!!.position.y - weaponTex.regionHeight / 2,
                    weaponTex.regionWidth / 100f,
                    weaponTex.regionWidth / 100f
            )
        }

        if (weapon[e].bulletBodies != null) {
            weapon[e].bulletBodies!!.forEach {
                val bulletTex = it.userData as TextureRegion

                batch.draw(
                        bulletTex,
                        it.position.x - bulletTex.regionWidth / 2,
                        it.position.y - bulletTex.regionHeight / 2,
                        bulletTex.regionWidth / 100f,
                        bulletTex.regionWidth / 100f

                )
            }
        }


    }

}