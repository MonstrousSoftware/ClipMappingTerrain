package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.io.File;

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
        pixmap = new Pixmap(textureFile);
        heightMapTexture = new Texture(pixmap, true);
        //heightMapTexture = new Texture(textureFile, true);

        heightMapTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        heightMapTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        mapSize = heightMapTexture.getWidth();  // assumes a square
    }

    public Texture getHeightMapTexture(){
        // create on demand
        if(heightMapTexture == null){
            // copy to a texture (for debug)
            pixmap = noise.generatePixmap(heightMap, mapSize);

            heightMapTexture = new Texture(pixmap);
            heightMapTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
            heightMapTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return heightMapTexture;
    }

    public float get(float wx, float wz){
        int x = (int) wx;
        int z = (int) wz;
        int pixel = pixmap.getPixel(x, z);
        int red = (pixel >> 24) & 0xFF;
        float h = 64000f * ((red/255.0f)-0.5f);
        return 0; //heightMap[z][x];
    }

    @Override
    public void dispose() {
        if(heightMapTexture != null)
            heightMapTexture.dispose();
    }

}
