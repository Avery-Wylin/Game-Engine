package shaders;

import entities.Terrain;
import static java.lang.Math.pow;
import math.TransformMatrix;
import math.Vec3;
import meshes.Mesh;
import static org.lwjgl.opengl.GL30.*;

/**
 * 
 * Class used to render and manage terrains.
 * This class manages the loading of terrain meshes given terrain information.
 * This class also stores the shader for terrains and their settings.
 */
public class TerrainRenderManager {
    public static final int BUFFER_DIMENSION = 3;//must be odd
    public static final int BUFFER_SIZE = BUFFER_DIMENSION * BUFFER_DIMENSION;
    public static final int SQUARE_COUNT = 50;
    private static final int NON_DUPLICATED_VERTEX_COUNT = (int) pow(SQUARE_COUNT + 1, 2) + (int) pow(SQUARE_COUNT, 2);
    public static final int VERTEX_COUNT = NON_DUPLICATED_VERTEX_COUNT + 2 * (SQUARE_COUNT * (SQUARE_COUNT - 1));
    public static final float TERRAIN_SCALE = 40;
    
    private static TerrainShader shader = new TerrainShader();
    public static Vec3 diffuse = new Vec3(.5f,.5f,.5f);
    
    protected static Mesh[] terrainMeshBuffer = new Mesh[BUFFER_SIZE];
    protected static Terrain[] terrainBuffer = new Terrain[BUFFER_SIZE];
    
    
    private static void loadShaderSettings(){
        shader.start();
        glEnable(GL_CULL_FACE);
        //set texture
        glBindTexture(GL_TEXTURE_2D, 0);
        //set diffuse colour
        shader.loadDiffuseColour(diffuse);
    }
    
    /**
     * Draws all terrain buffers
     */
    public static void drawAllBuffers(){
        TransformMatrix transform = new TransformMatrix();
        loadShaderSettings();
        for(int i=0;i<terrainMeshBuffer.length;i++){
            //bind VAO 
            terrainMeshBuffer[i].bindVAO();
            //translate the terrain
            transform.setIdentity();
            transform.scale(TERRAIN_SCALE, TERRAIN_SCALE, TERRAIN_SCALE);
            transform.translate(TERRAIN_SCALE*terrainBuffer[i].x, 0, TERRAIN_SCALE*terrainBuffer[i].z);
            shader.loadTransformationMatrix(transform);
            //draw
            glDrawElements(GL_TRIANGLES, terrainMeshBuffer[i].getVertexCount(),GL_UNSIGNED_INT,0);
            //unbind VAO
            terrainMeshBuffer[i].unbindVAO();
        }
        
    }
    
    public void generateTerrainFlat(Mesh bufferMesh, Terrain terrain) {
        float[] pos = new float[VERTEX_COUNT * 3];
        //4 triangle * 3 points per tri = 12 indices per sqaure
        int[] order = new int[12 * SQUARE_COUNT * SQUARE_COUNT];

        //generate the positions
        int index = 0;
        int duplicateIndex = 0;
        float size = 1f / SQUARE_COUNT;

        for (int r = 0; r < SQUARE_COUNT; r++) {
            //place tops
            for (int c = 0; c < SQUARE_COUNT + 1; c++) {
                pos[index] = c * size;
                pos[index + 2] = r * size;
                pos[index + 1] = terrain.getHeights()[index / 3];

                //top left needs duplicate if not first or last column
                if (c > 0 && c<SQUARE_COUNT) {
                    pos[duplicateIndex + 3*NON_DUPLICATED_VERTEX_COUNT] = pos[index];
                    pos[duplicateIndex + 3*NON_DUPLICATED_VERTEX_COUNT + 1] = pos[index + 1];
                    pos[duplicateIndex + 3*NON_DUPLICATED_VERTEX_COUNT + 2] = pos[index + 2];
                    duplicateIndex += 3;
                }
                //top right needs duplicate if not first row and not first column
                if (r > 0 && r<SQUARE_COUNT && c>0) {
                    pos[duplicateIndex + 3*NON_DUPLICATED_VERTEX_COUNT] = pos[index];
                    pos[duplicateIndex + 3*NON_DUPLICATED_VERTEX_COUNT + 1] = pos[index + 1];
                    pos[duplicateIndex + 3*NON_DUPLICATED_VERTEX_COUNT + 2] = pos[index + 2];
                    duplicateIndex += 3;
                }

                index += 3;
            }

            //place centers
            for (int c = 0; c < SQUARE_COUNT; c++) {
                pos[index] = c * size + (size / 2);
                pos[index + 2] = r * size + (size / 2);
                pos[index + 1] = terrain.getHeights()[index / 3];
                index += 3;
            }
        }
        //place bottom row
        for (int c = 0; c < SQUARE_COUNT + 1; c++) {
            pos[index] = c * size;
            pos[index + 2] = (SQUARE_COUNT) * size;
            pos[index + 1] = terrain.getHeights()[index / 3];
            index += 3;
        }

        index = 0;
        duplicateIndex=0;
        int tl;
        int tr;
        int bl;
        int br;
        int cen;

        for (int r = 0; r < SQUARE_COUNT; r++) {
            for (int c = 0; c < SQUARE_COUNT; c++) {
                
                
                
                tl = r * (SQUARE_COUNT * 2 + 1) + c;
                tr = tl + 1;
                bl = tl + (SQUARE_COUNT * 2 + 1);
                br = bl + 1;
                cen = tl + (SQUARE_COUNT + 1);
                
            //put in order
            //top
                order[index++] = c>0?NON_DUPLICATED_VERTEX_COUNT+(duplicateIndex++):tl;
                order[index++] = cen;
                order[index++] = tr;
                
            //right
                order[index++] = r>0?NON_DUPLICATED_VERTEX_COUNT+(duplicateIndex++):tr;
                order[index++] = cen;
                order[index++] = br;
                
            //bottom
                order[index++] = br;
                order[index++] = cen;
                order[index++] = bl;
                
            //left
                order[index++] = cen;
                order[index++] = tl;
                order[index++] = bl;

            }
        }
        
        //place uvs
        float[] uv = new float[VERTEX_COUNT*2];
        index=0;
        for(int i=0;i<pos.length;i+=3){
            uv[index++]=TERRAIN_SCALE/2*(pos[i]);
            uv[index++]=TERRAIN_SCALE/2*(1-pos[i+2]);
        }

        //load mesh
           bufferMesh.load(pos, order, uv, Mesh.createFlatNormals(pos,order));

    }
    
    public void generateTerrainSmooth(Mesh bufferMesh, Terrain terrain){
        float[] pos = new float[VERTEX_COUNT * 3];
        //4 triangle * 3 points per tri = 12 indices per sqaure
        int[] order = new int[12 * SQUARE_COUNT * SQUARE_COUNT];

        //generate the positions
        int index = 0;
        int duplicateIndex = 0;
        float size = 1f / SQUARE_COUNT;

        for (int r = 0; r < SQUARE_COUNT; r++) {
            //place tops
            for (int c = 0; c < SQUARE_COUNT + 1; c++) {
                pos[index] = c * size;
                pos[index + 2] = r * size;
                pos[index + 1] = terrain.getHeights()[index / 3];;
                index += 3;
            }

            //place centers
            for (int c = 0; c < SQUARE_COUNT; c++) {
                pos[index] = c * size + (size / 2);
                pos[index + 2] = r * size + (size / 2);
                pos[index + 1] = terrain.getHeights()[index / 3];;
                index += 3;
            }
        }
        //place bottom row
        for (int c = 0; c < SQUARE_COUNT + 1; c++) {
            pos[index] = c * size;
            pos[index + 2] = (SQUARE_COUNT) * size;
            pos[index + 1] = terrain.getHeights()[index / 3];;
            index += 3;
        }

        index = 0;
        int tl;
        int tr;
        int bl;
        int br;
        int cen;

        for (int r = 0; r < SQUARE_COUNT; r++) {
            for (int c = 0; c < SQUARE_COUNT; c++) {
                
                
                
                tl = r * (SQUARE_COUNT * 2 + 1) + c;
                tr = tl + 1;
                bl = tl + (SQUARE_COUNT * 2 + 1);
                br = bl + 1;
                cen = tl + (SQUARE_COUNT + 1);
                
            //put in order
            //top
                order[index++] = tl;
                order[index++] = cen;
                order[index++] = tr;
                
            //right
                order[index++] = tr;
                order[index++] = cen;
                order[index++] = br;
                
            //bottom
                order[index++] = br;
                order[index++] = cen;
                order[index++] = bl;
                
            //left
                order[index++] = cen;
                order[index++] = tl;
                order[index++] = bl;

            }
        }
        
        //place uvs
        float[] uv = new float[VERTEX_COUNT*2];
        index=0;
        for(int i=0;i<pos.length;i+=3){
            uv[index++]=TERRAIN_SCALE/2*(pos[i]);
            uv[index++]=TERRAIN_SCALE/2*(1-pos[i+2]);
        }

        
        //load mesh
           bufferMesh.load(pos, order, uv, Mesh.createSmoothNormals(pos,order));
    }
    
}
