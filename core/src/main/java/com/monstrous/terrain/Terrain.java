package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Terrain implements Disposable {
    public GUI gui;
    public final int clipMapSize;   // should be 2^N-1, e.g. 127 or 63 (255 is too large for the indexing) = vertices per side
    public HeightMap heightMap;
    public Texture grassTexture;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    private GridModelBuilder gridBuilder;
    private final Model gridModel;
    private final Model squareMxM;
    private final Model fillerMX3;
    private final Model filler3XM;
    private final Model horizontalTrim;
    private final Model verticalTrim;
    private Vector3 focus;


    public Terrain(GUI gui, int clipMapSize) {
        this.gui = gui;
        this.clipMapSize = clipMapSize;
        heightMap = new HeightMap(clipMapSize+1);
        gridBuilder = new GridModelBuilder();
        final int N = clipMapSize;
        final int M = (N+1)/4;

        // NxN center grid
        gridModel = gridBuilder.makeGridModel(heightMap,  N, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        // vertex positions range is [0..M][0..M]
        squareMxM = gridBuilder.makeGridModel(heightMap,  M, M, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        // vertical filler blocks to close the ring
        filler3XM = gridBuilder.makeGridModel(heightMap,  3, M, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        // horizontal filler blocks to close the ring
        fillerMX3 = gridBuilder.makeGridModel(heightMap,  M, 3, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)));

        // top/bottom trim
        horizontalTrim = gridBuilder.makeGridModel(heightMap,  4*M+1, 3, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)));

        // left/right trim
        verticalTrim = gridBuilder.makeGridModel(heightMap,  3, 4*M-1, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.WHITE)));


        // get ground texture, use mip mapping and allow repeat wrapping
        grassTexture = new Texture(Gdx.files.internal("Grass.png"), true);
        grassTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        grassTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);


        focus = new Vector3();
    }

    /** update terrain to have the highest level of detail near the focal instance */
    public void update(ModelInstance focalInstance){
        focalInstance.transform.getTranslation(focus);
        makeTerrain();

    }

    public void render(ModelBatch modelBatch, Environment environment) {
   		modelBatch.render(instances, environment);
    }

    public Texture getHeightMapTexture(){
        return heightMap.getHeightMapTexture();
    }

    public void makeTerrain() {
        instances.clear();
        float scale = 8f;
        int numLevels = 10;
        for(int level = 0; level < numLevels; level++) {
            makeTerrainLevel(level, scale );
            scale *= 2f;
        }
    }

    /** Make one of the terrain levels. level 0 is smallest and finest level, level 1 is half the resolution, etc. */
    private void makeTerrainLevel(int level, float scale) {



        // todo heightmap should be sampled according to scale and position

        if(gui.showTerrainTexture) {
            addTexturedSquare(instances, scale);
            // todo ring
        }


        // line raster for demonstration purposes
        if (gui.showGrid) {
            if(level == 0)
                addDebugSquare(instances, scale);
            else
                addDebugRing(instances, level, scale);
            addTrim(instances, scale);
        }

    }



    private void addTexturedSquare(Array<ModelInstance> instances, float scale){
        float offset = scale/clipMapSize;   // world size of one tile
        Model model = gridBuilder.makeGridModel(heightMap,  clipMapSize, GL20.GL_TRIANGLES, new Material(TextureAttribute.createDiffuse(grassTexture)));
        ModelInstance instance = new ModelInstance(model, new Vector3(-offset, 0, -offset));
        instance.transform.scale(scale, 1f, scale);
        instances.add(instance);
    }

    private void addDebugSquare(Array<ModelInstance> instances, float scale){
        final int N = clipMapSize;
        float xf = -(float)  (N-1) * scale/2f;
        float zf = -(float)  (N-1) * scale/2f;
        // snap to multiple of 2 tiles
        xf = 2 * scale * Math.round((focus.x+xf) / (2*scale));
        zf = 2 * scale * Math.round((focus.z+zf) / (2*scale));

        addSquare(instances, gridModel, scale, xf, zf,  0, 0);
    }


    // scale is the size in world units of one tile at this level
    private void addDebugRing(Array<ModelInstance> instances, int level, float scale){
        final int N = clipMapSize;
        final int M = (N+1)/4;
        // offset for corner of ring
        float xf = -(float)  (N-1) * scale/2f;
        float zf = -(float)  (N-1) * scale/2f;

        // snap to multiple of 2 tiles
        xf = 2 * scale * Math.round((focus.x+xf) / (2*scale));
        zf = 2 * scale * Math.round((focus.z+zf) / (2*scale));


        // add 12 blocks of size MxM
        addSquare(instances, squareMxM, scale, xf, zf,  0, 0);
        addSquare(instances, squareMxM, scale, xf, zf, M-1, 0);
        addSquare(instances, squareMxM, scale, xf, zf, 0, M-1);

        addSquare(instances, squareMxM, scale, xf, zf, N-M, 0);
        addSquare(instances, squareMxM, scale, xf, zf, N-2*M+1, 0);
        addSquare(instances, squareMxM, scale, xf, zf, N-M, M-1);

        addSquare(instances, squareMxM, scale, xf, zf, 0, N-M);
        addSquare(instances, squareMxM, scale, xf, zf, M-1, N-M);
        addSquare(instances, squareMxM, scale, xf, zf, 0, N-2*M+1);

        addSquare(instances, squareMxM, scale, xf, zf, N-M, N-M);
        addSquare(instances, squareMxM, scale, xf, zf, N-2*M+1, N-M);
        addSquare(instances, squareMxM, scale, xf, zf, N-M, N-2*M+1);

        // vertical filler blocks to close the ring
        addSquare(instances, filler3XM, scale, xf, zf, 2*(M-1), 0);
        addSquare(instances, filler3XM, scale, xf, zf, 2*(M-1), N-M);

        // horizontal filler blocks to close the ring
        addSquare(instances, fillerMX3, scale, xf, zf, 0, 2*(M-1));
        addSquare(instances, fillerMX3, scale, xf, zf, N-M, 2*(M-1));
    }

    /** Add L shaped trim around the level to fill in the gap with the next larger level. */
    private void addTrim(Array<ModelInstance> instances, float scale) {
        final int N = clipMapSize;
        final int M = (N + 1) / 4;
        // offset for corner of ring
        float xf = -(float) (N - 1) * scale / 2f;
        float zf = -(float) (N - 1) * scale / 2f;

        int xc = Math.round((focus.x + xf) / (2 * scale));
        int zc = Math.round((focus.z + zf) / (2 * scale));

        // snap to multiple of 2 tiles
        xf = 2 * scale * xc;
        zf = 2 * scale * zc;

        if(zc % 2 == 0)
            addSquare(instances, horizontalTrim, scale, xf, zf, (xc % 2 == 0 ? -2: 0), -2); // top trim
        else
            addSquare(instances, horizontalTrim, scale, xf, zf, (xc % 2 == 0 ? -2 : 0), N-1); // bottom trim

        if(xc % 2 == 0)
            addSquare(instances, verticalTrim, scale, xf, zf, -2, 0); // left trim
        else
            addSquare(instances, verticalTrim, scale, xf, zf, N-1, 0); // right trim
    }

    private void addSquare(Array<ModelInstance> instances, Model squareMxM, float scale, float xo, float zo, int x, int z){
        ModelInstance instance = new ModelInstance(squareMxM);
        instance.transform.translate(xo + x * scale, 0, zo + z*scale);
        instance.transform.scale(scale, 1f, scale);
        instances.add(instance);
    }

    @Override
    public void dispose() {
        heightMap.dispose();
        squareMxM.dispose();
        gridModel.dispose();
        filler3XM.dispose();
        fillerMX3.dispose();
    }
}
