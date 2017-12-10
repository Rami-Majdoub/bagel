package ru.icarumbas.bagel.view.renderer.systems


/*
class ShaderSystem : IteratingSystem{
    private val rm: RoomWorld

    constructor(rm: RoomWorld) : super(Family.all(ShaderComponent::class.java).get()) {
        this.rm = rm
        ShaderProgram.pedantic = false
    }

    override fun processEntity(e: Entity, deltaTime: Float) {
        if (e.inView(rm)){

            if (!shader[e].shaderProgram.isCompiled) {
                println(shader[e].shaderProgram.log)
                throw Exception()
            }

            shader[e].shaderProgram.begin()

            shader[e].shaderProgram.setUniformf("u_viewportInverse", Vector2(1f / size[e].rectSize.x, 1f / size[e].rectSize.y))
            shader[e].shaderProgram.setUniformf("u_offset", .001f)
            shader[e].shaderProgram.setUniformf("u_step", Math.min(.001f, size[e].rectSize.x / 70f))
            shader[e].shaderProgram.setUniformf("u_color", Vector3(1f, 0f, 0f))

            shader[e].shaderProgram.end()
        }
    }
}*/
