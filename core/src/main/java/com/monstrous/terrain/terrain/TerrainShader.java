package com.monstrous.terrain.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

public class TerrainShader extends DefaultShader {



    public TerrainShader(Renderable renderable) {
        super(renderable, new DefaultShader.Config(
            Gdx.files.internal("shaders/terrain.vertex.glsl").readString(),
            Gdx.files.internal("shaders/terrain.fragment.glsl").readString() ) );
    }


}
