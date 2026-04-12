package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class CharacterController extends InputAdapter {
    final static float SPEED = 2500f;
    final static float TURN_SPEED = 180f; // degrees/s

    final ModelInstance modelInstance;
    private final Map<Integer, Integer> keys = new HashMap<>();
    public float angle;

    public CharacterController(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        angle = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, 1);
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode);
        return super.keyUp(keycode);
    }

    public void update(float dt){

        if(keys.get(Input.Keys.A) != null)
            modelInstance.transform.translate(-dt*SPEED, 0, 0);
        if(keys.get(Input.Keys.D) != null)
                modelInstance.transform.translate(dt*SPEED, 0, 0);
        if(keys.get(Input.Keys.S) != null)
                modelInstance.transform.translate(0, dt*SPEED, 0);
        if(keys.get(Input.Keys.W) != null)
                modelInstance.transform.translate(0, -dt*SPEED, 0);
        if(keys.get(Input.Keys.R) != null)
                modelInstance.transform.translate(0, 0, dt*SPEED);
        if(keys.get(Input.Keys.F) != null)
                modelInstance.transform.translate(0, 0,-dt*SPEED);
        if(keys.get(Input.Keys.Q) != null) {
            modelInstance.transform.rotate(Vector3.Z, dt * TURN_SPEED);
            angle += dt * TURN_SPEED;
        }
        if(keys.get(Input.Keys.E) != null) {
            modelInstance.transform.rotate(Vector3.Z, -dt * TURN_SPEED);
            angle += -dt * TURN_SPEED;
        }
    }


}
