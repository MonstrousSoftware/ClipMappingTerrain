package com.monstrous.terrain;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class HeightMap implements Disposable {
    final int PERLIN_GRID_SIZE = 16;

    final public int mapSize;
    private final Noise noise = new Noise();
    private final float[][] heightMap;
    private Texture heightMapTexture;


    public HeightMap(int mapSize) {
        this.mapSize = mapSize;

        // generate a noise map
        heightMap = noise.generateSmoothedPerlinMap(mapSize, mapSize, 0,0, PERLIN_GRID_SIZE);
    }

    public Texture getHeightMapTexture(){
        // create on demand
        if(heightMapTexture == null){
            // copy to a texture (for debug)
            Pixmap pixmap = noise.generatePixmap(heightMap, mapSize);
            heightMapTexture = new Texture(pixmap);
        }
        return heightMapTexture;
    }

    public float get(int x, int z){
        return 0; //heightMap[z][x];
    }

    @Override
    public void dispose() {
        if(heightMapTexture != null)
            heightMapTexture.dispose();
    }

}
