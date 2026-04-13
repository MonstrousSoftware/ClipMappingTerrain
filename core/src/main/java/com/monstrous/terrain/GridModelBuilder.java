package com.monstrous.terrain;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class GridModelBuilder {


    /** Make a Model consisting of a square 2D grid of NxN vertices */
    public Model makeGridModel( int N, int primitive, Material material) {
        return makeGridModel( N, N, primitive, material);
    }

    /** Make a Model consisting of a rectangular grid of size NxM vertices */
   public Model makeGridModel(int N, int M, int primitive, Material material) {

        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshBuilder meshBuilder = (MeshBuilder) modelBuilder.part("face", primitive, attr, material);
        final int numVerts = N * M;
        final int numTris = 2 * (N-1) * (M-1);
        Vector3[] vertices = new Vector3[numVerts];

        meshBuilder.ensureVertices(numVerts);
        meshBuilder.ensureTriangleIndices(numTris);


        Vector3 pos = new Vector3();
        float posz;

        for (int y = 0; y < M; y++) {
            float posy = y; //((float) y / (float) N) - 0.5f;        // y in [-0.5f .. 0.5f]
            for (int x = 0; x < N; x++) {
                float posx = x; //((float) x / (float) N - 0.5f);        // x in [-0.5f .. 0.5f]

                posz = 0f;  // this will be filled in by the vertex shader
                pos.set(posx , posz, posy );            // swapping z,y to orient horizontally


                vertices[y * N + x] = new Vector3(pos);
                //normals[y * N + x] = new Vector3(0, 0, 0);


            }
            if (y >= 1) {
                // add to index list to make a row of triangles using vertices at y and y-1
                short v0 = (short) ((y - 1) * N);    // vertex number at top left of this row
                for (short t = 0; t < N-1; t++) {
                    // counter-clockwise winding
                    addTriangle(meshBuilder, vertices,  N, v0, (short) (v0 + N), (short) (v0 + 1));
                    addTriangle(meshBuilder, vertices,  N, (short) (v0 + 1), (short) (v0 + N), (short) (v0 + N + 1));
                    v0++;                // next column
                }
            }
        }

        // now normalize each normal (which is the sum of the attached triangle normals)
        // and pass vertex to meshBuilder
        MeshPartBuilder.VertexInfo vert = new MeshPartBuilder.VertexInfo();
        vert.hasColor = false;
        vert.hasNormal = true;
        vert.hasPosition = true;
        vert.hasUV = true;

        for (int i = 0; i < numVerts; i++) {
            int x = i % N;    // e.g. in [0 .. 3] if N == 4
            int y = i / N;
            float reps = 16;
            float u =  (x * reps) / (float) N;
            float v =  (y * reps) / (float) N;
            vert.position.set(vertices[i]);
            vert.uv.x = u;                    // texture needs to have repeat wrapping enables to handle u,v > 1
            vert.uv.y = v;
            meshBuilder.vertex(vert);
        }

        return modelBuilder.end();
    }



    private void addTriangle(MeshBuilder meshBuilder, final Vector3[] vertices, int N, short v0, short v1, short v2) {
        meshBuilder.triangle(v0, v1, v2);
    }
}
