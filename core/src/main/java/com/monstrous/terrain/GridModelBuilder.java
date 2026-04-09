package com.monstrous.terrain;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class GridModelBuilder {

    // todo: in real clipmapping the heightmap is sampled dynamically, not during mesh construction

    /** Make a Model consisting of a square grid of NxN vertices */
    public Model makeGridModel(HeightMap heightMap, int N, int primitive, Material material) {
        return makeGridModel(heightMap, N, N, primitive, material);
    }

    /** Make a Model consisting of a rectangular grid of size NxM vertices */
    // todo find way to scale texture coordinates
   public Model makeGridModel(HeightMap heightMap, int N, int M, int primitive, Material material) {

        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshBuilder meshBuilder = (MeshBuilder) modelBuilder.part("face", primitive, attr, material);
        final int numVerts = N * M;
        final int numTris = 2 * (N-1) * (M-1);
        Vector3[] vertices = new Vector3[numVerts];
        Vector3[] normals = new Vector3[numVerts];


        meshBuilder.ensureVertices(numVerts);
        meshBuilder.ensureTriangleIndices(numTris);


        Vector3 pos = new Vector3();
        float posz;

        for (int y = 0; y < M; y++) {
            float posy = y; //((float) y / (float) N) - 0.5f;        // y in [-0.5f .. 0.5f]
            for (int x = 0; x < N; x++) {
                float posx = x; //((float) x / (float) N - 0.5f);        // x in [-0.5f .. 0.5f]

                posz = getHeight(heightMap, x, y);
                pos.set(posx , posz, posy );            // swapping z,y to orient horizontally


                vertices[y * N + x] = new Vector3(pos);
                normals[y * N + x] = new Vector3(0, 0, 0);


            }
            if (y >= 1) {
                // add to index list to make a row of triangles using vertices at y and y-1
                short v0 = (short) ((y - 1) * N);    // vertex number at top left of this row
                for (short t = 0; t < N-1; t++) {
                    // counter-clockwise winding
                    addTriangle(meshBuilder, vertices, normals, N, v0, (short) (v0 + N), (short) (v0 + 1));
                    addTriangle(meshBuilder, vertices, normals, N, (short) (v0 + 1), (short) (v0 + N), (short) (v0 + N + 1));
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

        Vector3 normal = new Vector3();
        for (int i = 0; i < numVerts; i++) {
            normal.set(normals[i]);
            normal.nor();


            int x = i % N;    // e.g. in [0 .. 3] if N == 4
            int y = i / N;
            float reps = 16;
            float u =  (x * reps) / (float) N;
            float v =  (y * reps) / (float) N;
            vert.position.set(vertices[i]);
            vert.normal.set(normal);
            vert.uv.x = u;                    // texture needs to have repeat wrapping enables to handle u,v > 1
            vert.uv.y = v;
            meshBuilder.vertex(vert);
        }

        return modelBuilder.end();
    }



    private void addTriangle(MeshBuilder meshBuilder, final Vector3[] vertices, Vector3[] normals, int N, short v0, short v1, short v2) {
        meshBuilder.triangle(v0, v1, v2);
        calcNormal(vertices, normals, v0, v1, v2);
    }

    /*
     * Calculate the normal
     */
    private void calcNormal(final Vector3[] vertices, Vector3[] normals, short v0, short v1, short v2) {

        Vector3 p0 = vertices[v0];
        Vector3 p1 = vertices[v1];
        Vector3 p2 = vertices[v2];

        Vector3 u = new Vector3();
        u.set(p2);
        u.sub(p0);

        Vector3 v = new Vector3();
        v.set(p1);
        v.sub(p0);

        Vector3 n = new Vector3();
        n.set(v);
        n.crs(u);
        n.nor();

        normals[v0].add(n);
        normals[v1].add(n);
        normals[v2].add(n);
    }

    private float getHeight(HeightMap map, int x, int y) {

        //return gui.maxHeight * (map.get(x,y) - 0.5f);
        // todo make amplitude adjustable
        return 0f; //600f * (map.get(x,y) - 0.5f);
    }
}
