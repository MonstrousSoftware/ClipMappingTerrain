package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class CharacterController extends InputAdapter {
    final static float SPEED = 2500f;
    final static float TURN_SPEED = 180f; // degrees/s

    final ModelInstance modelInstance;
    private int keyDown;
    public float angle;

    public CharacterController(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        angle = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        keyDown = keycode;
        //Gdx.app.log("key down", ""+keycode);
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        keyDown = -1;
        return super.keyUp(keycode);
    }

    public void update(float dt){

        switch(keyDown){
            case Input.Keys.A:
                modelInstance.transform.translate(-dt*SPEED, 0, 0);
                break;
            case Input.Keys.D:
                modelInstance.transform.translate(dt*SPEED, 0, 0);
                break;
            case Input.Keys.W:
                modelInstance.transform.translate(0, dt*SPEED, 0);
                break;
            case Input.Keys.S:
                modelInstance.transform.translate(0, -dt*SPEED, 0);
                break;
            case Input.Keys.R:
                modelInstance.transform.translate(0, 0, dt*SPEED);
                break;
            case Input.Keys.F:
                modelInstance.transform.translate(0, 0,-dt*SPEED);
                break;
            case Input.Keys.Q:
                modelInstance.transform.rotate(Vector3.Z, dt*TURN_SPEED);
                angle += dt*TURN_SPEED;
                break;
            case Input.Keys.E:
                modelInstance.transform.rotate(Vector3.Z, -dt*TURN_SPEED);
                angle += -dt*TURN_SPEED;
                break;

        }
    }


}
