package ru.icarumbas.bagel.systems.rendering
/*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import ru.icarumbas.bagel.components.other.AiComponent
import ru.icarumbas.bagel.components.other.PlayerComponent
import ru.icarumbas.bagel.components.other.WeaponComponent
import ru.icarumbas.bagel.utils.Mappers
import ru.icarumbas.bagel.utils.rotatedRight


class WeaponRenderingSystem : IteratingSystem{

    private val weapon = Mappers.weapon
    private val batch: Batch

    constructor(batch: Batch) : super(Family.all(WeaponComponent::class.java).one(
                    PlayerComponent::class.java,
                    AiComponent::class.java).get()) {
        this.batch = batch
    }

    override fun processEntity(e: Entity, deltaTime: Float) {

        var weaponTex :TextureRegion? = null

        if (e.rotatedRight() && weapon[e].weaponBodyRight?.userData != null) {
            weaponTex = weapon[e].weaponBodyRight?.userData as TextureRegion
        } else {
            if (weapon[e].weaponBodyLeft?.userData != null)
            weaponTex = weapon[e].weaponBodyLeft?.userData as TextureRegion
        }

        batch.begin()

        if (weaponTex != null) {
            if (weapon[e].weaponBodyRight?.isActive!!) {
                batch.draw(
                        weaponTex,
                        weapon[e].weaponBodyRight!!.position.x - weaponTex.regionWidth / 100f / 2f,
                        weapon[e].weaponBodyRight!!.position.y - weaponTex.regionHeight / 100f / 2f,
                        weaponTex.regionWidth / 100f / 2f,
                        weaponTex.regionHeight / 100f / 2f,
                        weaponTex.regionWidth.toFloat(),
                        weaponTex.regionHeight.toFloat(),
                        .01f,
                        .01f,
                        weapon[e].weaponBodyRight!!.angle * MathUtils.radiansToDegrees
                )
            }
            if (weapon[e].weaponBodyLeft?.isActive!!) {
                batch.draw(
                        weaponTex,
                        weapon[e].weaponBodyLeft!!.position.x - weaponTex.regionWidth / 100f / 2f,
                        weapon[e].weaponBodyLeft!!.position.y - weaponTex.regionHeight / 100f / 2f,
                        weaponTex.regionWidth / 100f / 2f,
                        weaponTex.regionHeight / 100f / 2f,
                        weaponTex.regionWidth.toFloat(),
                        weaponTex.regionHeight.toFloat(),
                        .01f,
                        .01f,
                        weapon[e].weaponBodyLeft!!.angle * MathUtils.radiansToDegrees
                )
            }
        }

        if (weapon[e].bulletBodies != null) {
            weapon[e].bulletBodies!!.forEach {
                val bulletTex = it.userData as TextureRegion

                batch.draw(
                        bulletTex,
                        it.position.x - bulletTex.regionWidth / 100f / 2,
                        it.position.y - bulletTex.regionHeight / 100f / 2,
                        bulletTex.regionWidth / 100f,
                        bulletTex.regionHeight / 100f

                )
            }
        }

        batch.end()


    }*/

//}