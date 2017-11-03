package ru.icarumbas.bagel;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

public class StackOverflowTests<T> {

    T[] arr;
    Body body = null;

    public StackOverflowTests(Array<? extends T> keyFrames) {
        Class arrayType = keyFrames.items.getClass().getComponentType();
        T[] frames = (T[]) ArrayReflection.newInstance(arrayType, keyFrames.size);
        for (int i = 0, n = keyFrames.size; i < n; i++) {
            frames[i] = keyFrames.get(i);
        }
    }
}
