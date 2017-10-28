package ru.icarumbas.bagel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;


public class StackOverflowTests {

    Body body = null;

    public StackOverflowTests() {
    }

    private void playerMovement() {

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                body.setLinearVelocity(new Vector2(-30f, 30f));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                body.setLinearVelocity(new Vector2(30f, 30f));
            } else
                body.setLinearVelocity(new Vector2(0f, 30f));
        } else
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    body.setLinearVelocity(new Vector2(-30f, -30f));
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    body.setLinearVelocity(new Vector2(30f, -30f));
                } else
                    body.setLinearVelocity(new Vector2(0f, -30f));
            } else
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                        body.setLinearVelocity(new Vector2(-30f, 0f));
                } else
                    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                        body.setLinearVelocity(new Vector2(0f, 30f));
                    } else
                        body.setLinearVelocity(new Vector2(0f, 0f));
    }
}
