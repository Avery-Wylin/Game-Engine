package entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.*;
import java.util.ArrayList;
import math.OpenSimplex2F;
import math.TransformMatrix;
import math.Vec3;
import meshes.Mesh;
import static org.lwjgl.opengl.GL30.*;
import shaders.TerrainShader;

public class Terrain {

    static int RENDER_COUNT = 200;
    public static int SIDE_SQUARE_COUNT = 300;
    public static float SCALE = 300f;
    public static int TOTAL_SQUARE_COUNT = SIDE_SQUARE_COUNT * SIDE_SQUARE_COUNT;
    public static int HEIGHT_COUNT = (SIDE_SQUARE_COUNT + 1) * (SIDE_SQUARE_COUNT + 1);
    public static float SQUARE_SCALE = 1f / (SIDE_SQUARE_COUNT + 1);
    public static Mesh terrainMesh = new Mesh();
    public static TerrainShader shader = new TerrainShader();
    static int x = 0;
    static int z = 0;

    public static OpenSimplex2F simplex = new OpenSimplex2F(123456);
    private float[] heights;

    public Terrain() {
        heights = new float[HEIGHT_COUNT];
//        for (int i = 0; i < HEIGHT_COUNT; i++) {
//            heights[i] = 0;//generativeFunction((float) i % (SIDE_SQUARE_COUNT + 1) * SQUARE_SCALE, (float) i / (SIDE_SQUARE_COUNT + 1) * SQUARE_SCALE) / SCALE;
//            heights[i] *= SCALE;
//        }
        loadDataFromObj("1");
//        generateMesh(heights,terrainMesh);
        generateSubMesh(heights, x, z, RENDER_COUNT, terrainMesh);
    }

    public float[] getHeights() {
        return heights;
    }

    public static float generativeFunction(float x, float z) {
        float height = 0;
        height += pow(floor(simplex.noise2(x * 2, z * 2) * 10f) / 10f, 1) / 20f;
        height += (simplex.noise2(x * 1, z * 1)) / 500f;
        return height;
    }

    public static void generateSubMesh(float[] y, int cx, int cz, int rowCount, Mesh mesh) {
        int vertexCount = (rowCount + 1) * (rowCount + 1);
        int totalCount = (rowCount * rowCount);

        float[] pos = new float[3 * vertexCount];
        int[] order = new int[6 * totalCount];
        float[] uv = new float[2 * vertexCount];

        //generate positions and uvs
        int uvIndex = 0;
        int index = 0;
        int mappedX;
        int mappedZ;
        for (int r = 0; r < rowCount + 1; r++) {
            for (int c = 0; c < rowCount + 1; c++) {
                mappedX = (cx + c) % (SIDE_SQUARE_COUNT + 1);
                mappedZ = (cz + r) % (SIDE_SQUARE_COUNT + 1) * (SIDE_SQUARE_COUNT + 1);
                mappedX += mappedX < 0 ? SIDE_SQUARE_COUNT + 1 : 0;
                mappedZ += mappedZ < 0 ? (SIDE_SQUARE_COUNT + 1) * (SIDE_SQUARE_COUNT + 1) : 0;

                pos[index] = c * SQUARE_SCALE;
                pos[index + 1] = y[mappedX + mappedZ];
                pos[index + 2] = r * SQUARE_SCALE;

                //create uvs using created positions
                uv[uvIndex++] = pos[index] + cx * SQUARE_SCALE;
                uv[uvIndex++] = (1 - pos[index + 2] - cz * SQUARE_SCALE);
                index += 3;
            }
        }

        //generate order
        index = 0;
        for (int i = 0; i < totalCount; i++) {
            int tl = i + i / (rowCount);
            int tr = tl + 1;
            int bl = tl + rowCount + 1;
            int br = bl + 1;

            //tris must draw counter clockwise
            if (abs(pos[3 * bl + 1] - pos[3 * tr + 1]) > abs(pos[3 * tl + 1] - pos[3 * br + 1])) {
                //split sloping down (tl to br)
                //tri 1
                order[index++] = tl;
                order[index++] = bl;
                order[index++] = br;
                //tri 2
                order[index++] = tr;
                order[index++] = tl;
                order[index++] = br;
            } else {
                //split sloping up (bl to tr)
                //tri 1
                order[index++] = tl;
                order[index++] = bl;
                order[index++] = tr;
                //tri 2
                order[index++] = tr;
                order[index++] = bl;
                order[index++] = br;
            }

        }
        mesh.load(pos, order, uv, Mesh.createSmoothNormals(pos, order));
    }

    public static void render() {
        shader.start();
        glEnable(GL_CULL_FACE);
        //set texture
        glBindTexture(GL_TEXTURE_2D, 0);
        //set diffuse colour
        shader.loadDiffuseColour(new Vec3(0,0,0));
        TransformMatrix transform = new TransformMatrix();
        transform.scale(SCALE, SCALE, SCALE);
        transform.translate(x * SQUARE_SCALE * SCALE, 0, z * SQUARE_SCALE * SCALE);
        terrainMesh.bindVAO();
        shader.loadTransformationMatrix(transform);
        glDrawElements(GL_TRIANGLES, terrainMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        terrainMesh.unbindVAO();
    }

    public void recenter(float wx, float wz, float escapeRadius) {
        escapeRadius *= (float) RENDER_COUNT / (SIDE_SQUARE_COUNT * 2);
        wx = (wx / SCALE);
        wz = (wz / SCALE);
        float ox = (x + RENDER_COUNT / 2) * SQUARE_SCALE;
        float oz = (z + RENDER_COUNT / 2) * SQUARE_SCALE;
        if (wx < ox - escapeRadius || wx > ox + escapeRadius || wz < oz - escapeRadius || wz > oz + escapeRadius) {
            x = (int) (wx / SQUARE_SCALE) - RENDER_COUNT / 2;
            z = (int) (wz / SQUARE_SCALE) - RENDER_COUNT / 2;
            generateSubMesh(heights, x, z, RENDER_COUNT, terrainMesh);
        }
    }

    public void loadDataFromObj(String file) {
        BufferedReader reader = null;
        ArrayList<Vec3> inHeights = new ArrayList<>();
        float maxX = 0;
        float maxZ = 0;
        file = "assets/terrainData/" + file + ".obj";
        try {
            reader = new BufferedReader(new FileReader(file));
            String lineIn = "";
            boolean started = false;
            while (true) {
                lineIn = reader.readLine();
                if(lineIn.startsWith("v ")) {
                    String[] contents = lineIn.split(" ");
                    inHeights.add(new Vec3(
                            Float.parseFloat(contents[1]),
                            Float.parseFloat(contents[2]),
                            Float.parseFloat(contents[3])
                    ));
                    if (inHeights.get(inHeights.size() - 1).x > maxX) {
                        maxX = inHeights.get(inHeights.size() - 1).x;
                    }
                    if (inHeights.get(inHeights.size() - 1).z > maxZ) {
                        maxZ = inHeights.get(inHeights.size() - 1).z;
                    }
                    started=true;
                } 
                else if(started){
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.print(file + ".obj was not found.");
            System.exit(0);
        } catch (IOException ex) {
            System.err.print(file + ".obj could not load.");
            System.exit(0);
        }

        //check if the dimensions are correct
        if (maxX != maxZ) {
            System.err.print("Terrain " + file + ".obj is not a sqaure.");
            System.exit(0);
        }
        if (maxX > 1 || maxZ > 1) {
            System.err.print("Terrain " + file + ".obj is greater than 1");
            System.exit(0);
        }

        heights = new float[inHeights.size()];
        int vCount = (int) sqrt(heights.length);
        //sort input into place
        for (int i = 0; i < heights.length; i++) {
            int index = round((inHeights.get(i).x * (vCount-1)) + (inHeights.get(i).z * (vCount-1) * (vCount)));
            heights[index] = inHeights.get(i).y;
        }
        SIDE_SQUARE_COUNT = vCount-1;
        SCALE = SIDE_SQUARE_COUNT*2f;
        TOTAL_SQUARE_COUNT = SIDE_SQUARE_COUNT * SIDE_SQUARE_COUNT;
        HEIGHT_COUNT = (SIDE_SQUARE_COUNT + 1) * (SIDE_SQUARE_COUNT + 1);
        SQUARE_SCALE = 1f / (SIDE_SQUARE_COUNT + 1);
    }

}
