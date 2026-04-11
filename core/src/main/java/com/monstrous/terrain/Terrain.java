package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Terrain implements Disposable {
    public GUI gui;
    public final int clipMapSize;   // should be 2^N-1, e.g. 127 or 63 (255 is too large for the indexing) = vertices per side
    public final int numLevels;
    public final float tileSize;
    public HeightMap heightMap;
    public Texture grassTexture;
    public Array<TerrainElement> elements = new Array<>();
    private final GridModelBuilder gridBuilder;
    private final Model gridModel;
    private final Model squareMxM;
    private final Model fillerMX3;
    private final Model filler3XM;
    private final Model horizontalTrim;
    private final Model verticalTrim;
    private Vector3 focus;
    public boolean frustumCulling = false;

    /** Construct terrain.
     *
     * @param gui
     * @param clipMapSize size of each LoD level's grid (in vertices). Should be power of two minus one, e.g. 63
     * @param numLevels number of LoD levels, i.e. concentric rings
     * @param tileSize size of a single tile in world units
     */
    public Terrain(GUI gui, int clipMapSize, int numLevels, float tileSize) {
        this.gui = gui;
        this.clipMapSize = clipMapSize;
        this.numLevels = numLevels;
        this.tileSize = tileSize;
        heightMap = new HeightMap(256); //clipMapSize+1);
        gridBuilder = new GridModelBuilder();
        final int N = clipMapSize;
        final int M = (N+1)/4;



        // get ground texture, use mip mapping and allow repeat wrapping
        grassTexture = new Texture(Gdx.files.internal("Grass.png"), true);
        grassTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        grassTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        int primitive = GL20.GL_LINES;
        //Texture texture = heightMap.getHeightMapTexture();
        Texture texture  = new Texture(Gdx.files.internal("terrain/Rugged Terrain Height Map PNG.png"), true);

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        Material mat = new Material(ColorAttribute.createDiffuse(Color.FIREBRICK), TextureAttribute.createDiffuse(texture));


//        primitive = GL20.GL_TRIANGLES;
//        mat = new Material(TextureAttribute.createDiffuse(grassTexture));

        // NxN center grid
        gridModel = gridBuilder.makeGridModel(heightMap,  N, primitive, mat);
        // vertex positions range is [0..M][0..M]
        squareMxM = gridBuilder.makeGridModel(heightMap,  M, M, primitive, mat);
        // vertical filler blocks to close the ring
        filler3XM = gridBuilder.makeGridModel(heightMap,  3, M, primitive, mat);
        // horizontal filler blocks to close the ring
        fillerMX3 = gridBuilder.makeGridModel(heightMap,  M, 3, primitive, mat);

        // top/bottom trim
        horizontalTrim = gridBuilder.makeGridModel(heightMap,  2*M+1, 2, primitive, mat);

        // left/right trim
        verticalTrim = gridBuilder.makeGridModel(heightMap,  2, 2*M, primitive, mat);

        focus = new Vector3();

        buildTerrain();
        Gdx.app.log("instances", ""+ elements.size);
    }



    /** update terrain to have the highest level of detail near the focal instance */
    public void update(ModelInstance focalInstance){
        focalInstance.transform.getTranslation(focus);
        buildTerrain();
    }

    public void render(Camera camera, ModelBatch modelBatch, Environment environment) {


        if(frustumCulling) {
            int count = 0;
            for (TerrainElement element : elements) {
                if (camera.frustum.boundsInFrustum(element.bbox)) {
                    modelBatch.render(element.modelInstance, environment);
                    count++;
                }
            }
        } else {
            for (TerrainElement element : elements)
                modelBatch.render(element.modelInstance, environment);
        }
        //Gdx.app.log("after culling", ""+count);
    }

    public Texture getHeightMapTexture(){
        return heightMap.getHeightMapTexture();
    }

    private void buildTerrain(){
        elements.clear();
        float scale = this.tileSize;
        for(int level = 0; level < this.numLevels; level++) {
            makeTerrainLevel(level, scale );
            scale *= 2f;
        }
    }
    /** Make one of the terrain levels. level 0 is smallest and finest level, level 1 is half the resolution, etc. */
    private void makeTerrainLevel(int level, float scale) {

        // todo heightmap should be sampled according to scale and position

        if(gui.showTerrainTexture) {
            addTexturedSquare(elements, scale);
            // todo ring
        }


        // line raster for demonstration purposes
        if (gui.showGrid) {
            if(level == 0)  // central square grid
                addDebugSquare(elements, scale);
            else // surrounding ring
                addDebugRing(elements, scale);
            // fill the gap to the next level
            addTrim(elements, scale);
        }

    }



    private void addTexturedSquare(Array<TerrainElement> elements, float scale){
        float offset = scale/clipMapSize;   // world size of one tile
        Model model = gridBuilder.makeGridModel(heightMap,  clipMapSize, GL20.GL_TRIANGLES, new Material(TextureAttribute.createDiffuse(grassTexture)));
        ModelInstance instance = new ModelInstance(model, new Vector3(-offset, 0, -offset));
        instance.transform.scale(scale, 1f, scale);
        //elements.add(new TerrainElement(instance));
    }

    private void addDebugSquare(Array<TerrainElement> elements, float scale){
        final int N = clipMapSize;
        float xf = -(float)  (N-1) * scale/2f;
        float zf = -(float)  (N-1) * scale/2f;
        // snap to multiple of 2 tiles
        xf = 2 * scale * Math.round((focus.x+xf) / (2*scale));
        zf = 2 * scale * Math.round((focus.z+zf) / (2*scale));

        addSquare(elements, gridModel, scale, N, N, xf, zf,  0, 0);
    }


    // scale is the size in world units of one tile at this level
    private void addDebugRing(Array<TerrainElement> elements, float scale){
        final int N = clipMapSize;
        final int M = (N+1)/4;
        // offset for corner of ring
        float xf = -(float)  (N-1) * scale/2f;
        float zf = -(float)  (N-1) * scale/2f;

        // snap to multiple of 2 tiles
        xf = 2 * scale * Math.round((focus.x+xf) / (2*scale));
        zf = 2 * scale * Math.round((focus.z+zf) / (2*scale));


        // add 12 blocks of size MxM
        addSquare(elements, squareMxM, scale, M, M, xf, zf,  0, 0);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, M-1, 0);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, 0, M-1);

        addSquare(elements, squareMxM, scale, M, M, xf, zf, N-M, 0);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, N-2*M+1, 0);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, N-M, M-1);

        addSquare(elements, squareMxM, scale, M, M, xf, zf, 0, N-M);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, M-1, N-M);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, 0, N-2*M+1);

        addSquare(elements, squareMxM, scale, M, M, xf, zf, N-M, N-M);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, N-2*M+1, N-M);
        addSquare(elements, squareMxM, scale, M, M, xf, zf, N-M, N-2*M+1);

        // vertical filler blocks to close the ring
        addSquare(elements, filler3XM, scale,3, M,  xf, zf, 2*(M-1), 0);
        addSquare(elements, filler3XM, scale, 3, M, xf, zf, 2*(M-1), N-M);

        // horizontal filler blocks to close the ring
        addSquare(elements, fillerMX3, scale, M, 3, xf, zf, 0, 2*(M-1));
        addSquare(elements, fillerMX3, scale, M, 3, xf, zf, N-M, 2*(M-1));
    }

    /** Add L shaped trim around the level to fill in the gap with the next larger level.
     * The trim is same resolution as the surrounding level, i.e. half the resolution of the enclosed level. */
    private void addTrim(Array<TerrainElement> elements, float scale) {
        final int N = clipMapSize;
        final int M = (N+1)/4;

        // offset for corner of ring
        float xf = -(float) (N - 1) * scale/2f ;
        float zf = -(float) (N - 1) * scale/2f ;

        int xc = Math.round((focus.x + xf) / ( scale*2));
        int zc = Math.round((focus.z + zf) / ( scale*2));

        // snap to multiple of 2 tiles
        xf =  scale*2 * xc;
        zf =  scale*2 * zc;

        if(zc % 2 == 0)
            addSquare(elements, horizontalTrim, scale*2, 2*M+1, 3, xf, zf, (xc % 2 == 0 ? -1: 0), -1); // top trim
        else
            addSquare(elements, horizontalTrim, scale*2, 2*M+1, 2, xf, zf, (xc % 2 == 0 ? -1 : 0), (N-1)/2); // bottom trim

        if(xc % 2 == 0)
            addSquare(elements, verticalTrim, scale*2, 2, 2*M, xf, zf, -1, 0); // left trim
        else
            addSquare(elements, verticalTrim, scale*2, 2, 2*M, xf, zf, (N-1)/2, 0); // right trim
    }

    private final Vector3 min = new Vector3();
    private final Vector3 max = new Vector3();

    /** add a terrain element
     * xo,zo: position of level (bottom left corner)
     * x,z: position of this element (in tiles)
     * */
    private void addSquare(Array<TerrainElement> elements, Model model, float scale, int w, int h, float xo, float zo, int x, int z){
        ModelInstance instance = new ModelInstance(model);
        instance.transform.translate(xo + x * scale, 0, zo + z*scale);
        instance.transform.scale(scale, 1f, scale);
        BoundingBox bbox = new BoundingBox();
        min.set(xo + x * scale, -100f, zo + z*scale);
        max.set(min);
        max.add(scale * (w-1), 100f, scale*(h-1));
        bbox.set(min, max);
        elements.add(new TerrainElement(instance, bbox));
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
