package ru.icarumbas.bagel.Screens.Scenes

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.StretchViewport

class MiniMap {
    val stage = Stage(StretchViewport(800f, 480f))
    init {
        val table = Table()
        table.background = TextureRegionDrawable(TextureRegion(Texture("backMap.png")))
        table.setBounds(650f, 370f, 100f, 60f)
        stage.addActor(table)


        val univ = Image(Texture("universal.png"))

        table.add(univ)
    }
    fun render(){
        stage.draw()
    }
}
