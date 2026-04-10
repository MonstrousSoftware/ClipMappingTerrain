package com.monstrous.terrain;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/** A rectangular part of the terrain */
public class TerrainElement {
    public final ModelInstance modelInstance;
    public final BoundingBox bbox;

    public TerrainElement(ModelInstance instance) {
        this.modelInstance = instance;
        bbox = new BoundingBox();

        // beware: the following is costly and should not be done per frame
        instance.calculateBoundingBox(bbox);
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        bbox.getMin(min);
        bbox.getMax(max);
        Vector3 pos = new Vector3();
        modelInstance.transform.getTranslation(pos);
        float scale = modelInstance.transform.getScaleX();
        min.scl(scale).add(pos);
        max.scl(scale).add(pos);
        bbox.set(min, max);
        float x = bbox.getCenterX();
    }
}
