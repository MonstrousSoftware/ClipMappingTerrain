package com.monstrous.terrain;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class CharacterController extends InputAdapter {

    final ModelInstance modelInstance;
    private int keyDown;

    public CharacterController(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    @Override
    public boolean keyDown(int keycode) {
        keyDown = keycode;
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        keyDown = -1;
        return super.keyUp(keycode);
    }

    public void update(float dt){
        dt *= 100f;
        switch(keyDown){
            case Input.Keys.A:
                modelInstance.transform.translate(-dt, 0, 0);
                break;
            case Input.Keys.D:
                modelInstance.transform.translate(dt, 0, 0);
                break;
            case Input.Keys.W:
                modelInstance.transform.translate(0, 0, dt);
                break;
            case Input.Keys.S:
                modelInstance.transform.translate(0, 0, -dt);
                break;

        }
    }


}
