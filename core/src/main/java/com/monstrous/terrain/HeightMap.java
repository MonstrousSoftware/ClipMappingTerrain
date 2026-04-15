package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;


public class HeightMap implements Disposable {
    final int PERLIN_GRID_SIZE = 16;

    public int mapSize;
    private float[][] heightMap;
    private Noise noise;
    private Texture heightMapTexture;
    private Pixmap pixmap;


    /** Create height map using Perlin noise */
    public HeightMap(int mapSize) {
        this.mapSize = mapSize;
        noise = new Noise();
        // generate a noise map
        heightMap = noise.generateSmoothedPerlinMap(mapSize, mapSize, 0,0, PERLIN_GRID_SIZE);
    }

    /** Create height map from grey scale texture */
    public HeightMap(FileHandle textureFile) {
        //pixmap = new Pixmap(2048, 2048, Pixmap.Format.RGBA8888);
        pixmap = new Pixmap(textureFile);
        heightMapTexture = new Texture(pixmap, true);
        //heightMapTexture = new Texture(textureFile, true);

        heightMapTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        heightMapTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        mapSize = heightMapTexture.getWidth();  // assumes a square
    }

    public Texture getHeightMapTexture(){
        // create on demand
        if(heightMapTexture == null){
            // copy to a texture (for debug)
            pixmap = noise.generatePixmap(heightMap, mapSize);

            heightMapTexture = new Texture(pixmap);
            heightMapTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            heightMapTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return heightMapTexture;
    }

    public float get(float wx, float wz){
        int x = Math.round(wx * mapSize);
        int z = Math.round(wz*mapSize);
        // todo alpha is 16-bit but getPixel reduces this to 8 bits. Should read from ByteBuffer instead.
        int pixel = pixmap.getPixel(x, z);
        //Gdx.app.log("pixel: ", Integer.toHexString(pixel));
        int a = (pixel) & 0xFF;
        float h = 20000f * ((a/255.0f)-0.5f);
        return h; //heightMap[z][x];
    }

    @Override
    public void dispose() {
        if(heightMapTexture != null)
            heightMapTexture.dispose();
    }

}
