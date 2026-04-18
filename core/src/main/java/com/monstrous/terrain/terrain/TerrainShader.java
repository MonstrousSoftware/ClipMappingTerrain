package com.monstrous.terrain.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

/** By creating a dedicated TerrainShade class we can add some relevant uniforms */
public class TerrainShader extends DefaultShader {

    // uniform locations
    private int u_heightMapSize;
    private int u_scale;
    private int u_amplitude;
    private int heightMapSize;
    private float scale;
    private float amplitude;

    /** Simple constructor that uses some default settings. Use setXXX() to
     * set terrain parameters.
     */
    public TerrainShader(Renderable renderable) {
        this(renderable, 2048, 64, 25600);
    }

    /** Constructor that includes the terrain parameters. */
    // todo can be changed when?
    public TerrainShader(Renderable renderable, int heightMapSize, float scale, float amplitude) {
        super(renderable, new DefaultShader.Config(
            Gdx.files.internal("shaders/terrain.vertex.glsl").readString(),
            Gdx.files.internal("shaders/terrain.fragment.glsl").readString() ) );
        setHeightMapSize(heightMapSize);
        setScale(scale);
        setAmplitude(amplitude);
    }


    public void setHeightMapSize(int verticesPerSide){
        heightMapSize = verticesPerSide;
    }

    public void setScale(float horizontalScale){
        scale = horizontalScale;
    }

    public void setAmplitude(float amplitude){
        this.amplitude = amplitude;
    }

    public float getAmplitude() {
        return amplitude;
    }

    // assumes the shader is only ever called for terrain renderables
    @Override
    public boolean canRender(Renderable renderable) {
        return true;
    }


    // called once
    @Override
    public void init() {
        Gdx.app.log("terrainshader", "init");
        super.init();

        // get locations of specific uniforms to use later
        u_heightMapSize = program.getUniformLocation("u_heightMapSize");
        u_scale = program.getUniformLocation("u_scale");
        u_amplitude = program.getUniformLocation("u_amplitude");
    }

    // called per frame
    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        // set uniforms
        program.setUniformi(u_heightMapSize, heightMapSize);
        program.setUniformf(u_scale, scale);
        program.setUniformf(u_amplitude, amplitude);
    }
}
