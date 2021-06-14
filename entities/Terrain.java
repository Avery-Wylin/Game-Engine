package entities;

import static java.lang.Math.*;
import math.OpenSimplex2F;
import math.TransformMatrix;
import math.Vec3;
import meshes.Mesh;
import static org.lwjgl.opengl.GL30.*;
import shaders.TerrainShader;

public class Terrain {
    public static final int SIDE_SQUARE_COUNT = 10;
    public static final int TOTAL_SQUARE_COUNT = SIDE_SQUARE_COUNT*SIDE_SQUARE_COUNT;
    public static final int HEIGHT_COUNT = (SIDE_SQUARE_COUNT+1)*(SIDE_SQUARE_COUNT+1);
    public static final float SQUARE_SCALE= 1f/SIDE_SQUARE_COUNT;
    public static final float SCALE = 10f;
    public static Mesh terrainMesh = new Mesh();
    public static TerrainShader shader= new TerrainShader();
    static int x= -3;
    static int z = -10;
    static int subSize = 50;
  
    public static OpenSimplex2F simplex = new OpenSimplex2F(123456);
    private float[] heights;

    public Terrain() {
        heights = new float[HEIGHT_COUNT];
        for(int i=0;i<HEIGHT_COUNT;i++){
            heights[i] = generativeFunction((float)i%(SIDE_SQUARE_COUNT+1)*SQUARE_SCALE, (float)i/(SIDE_SQUARE_COUNT+1)*SQUARE_SCALE)/SCALE;
        }
//        generateMesh(heights,terrainMesh);
        generateSubMesh(heights,x,z,subSize,terrainMesh);
    }
    
    public float[] getHeights(){
        return heights;
    }
    
    
    public static float generativeFunction(float x,float z){
        float height = 0;
        height = (float)simplex.noise2(x*5, z*5);
        height += (float)pow(simplex.noise2(x*.5, z*.5)*2.5,3);
        height += (float)pow(simplex.noise2(x*.1, z*.1)*2.5,5)*2;
        return height;
    }
    
    public static void generateSubMesh(float[] y, int cx,int cz,int rowCount, Mesh mesh){
        int vertexCount = (rowCount+1)*(rowCount+1);
        int totalCount = (rowCount*rowCount);
        
        float[] pos = new float[3*vertexCount];
        int[] order = new int[6*totalCount];
        float[] uv = new float[2*vertexCount];
        
        //generate positions and uvs
        int uvIndex = 0;
        int index = 0;
        int iTransformed = cx%(rowCount+1)+(rowCount+1)*cz;
        for(int r=0;r<rowCount+1;r++){
            for(int c=0;c<rowCount+1;c++){
                pos[index]=c*SQUARE_SCALE;
                pos[index+1]=y[abs(cx+c)%(SIDE_SQUARE_COUNT+1)+abs(cz+r)%(SIDE_SQUARE_COUNT+1)*(SIDE_SQUARE_COUNT+1)];
                pos[index+2]=r*SQUARE_SCALE;

                //create uvs using created positions
                uv[uvIndex++]=pos[index]+cx*SQUARE_SCALE;
                uv[uvIndex++]=(1-pos[index+2]-cz*SQUARE_SCALE);
                index+=3;
            }
        }
        
//        for(int i=0;i<pos.length;i+=3){
//            //create positions using grid and heights
//            pos[i]=(index%(rowCount+1))*SQUARE_SCALE;
//            pos[i+1]=y[abs(iTransformed+(index%(rowCount+1))+(index/(rowCount+1))*(SIDE_SQUARE_COUNT+1))%y.length];
//            pos[i+2]=(index/(rowCount+1))*SQUARE_SCALE;
//            
//            //create uvs using created positions
//            uv[index2++]=pos[i]+cx*SQUARE_SCALE;
//            uv[index2++]=(1-pos[i+2]-cz*SQUARE_SCALE);
//            index++;
//        }
//        
        //generate order
        index=0;
        for(int i=0;i<totalCount;i++){
            int tl = i+i/(rowCount);
            int tr = tl+1;
            int bl = tl + rowCount+1;
            int br = bl+1;
            
            //tris must draw counter clockwise
            
            //tri 1
            order[index++]=tl;
            order[index++]=bl;
            order[index++]=tr;
            //tri 2
            order[index++]=tr;
            order[index++]=bl;
            order[index++]=br;
        }
        mesh.load(pos, order, uv, Mesh.createSmoothNormals(pos, order));
    }
    
    
    public static void render(){
        shader.start();
        glEnable(GL_CULL_FACE);
        //set texture
        glBindTexture(GL_TEXTURE_2D, 1);
        //set diffuse colour
        shader.loadDiffuseColour(new Vec3(.0f,.0f,.0f));
        TransformMatrix transform = new TransformMatrix();
        transform.scale(SCALE, SCALE, SCALE);
        transform.translate(x*SQUARE_SCALE*SCALE,0,z*SQUARE_SCALE*SCALE);
        terrainMesh.bindVAO();
        shader.loadTransformationMatrix(transform);
        glDrawElements(GL_TRIANGLES, terrainMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        terrainMesh.unbindVAO();
    }
    
    public void recenter(float wx, float wz, float escapeRadius){
        if(abs(SCALE*SQUARE_SCALE*x-wx)>escapeRadius||abs(SCALE*SQUARE_SCALE*z-wz)>escapeRadius){
            x=(int)(wx/SCALE*SQUARE_SCALE)-subSize/2;
            z=(int)(wz/SCALE*SQUARE_SCALE)-subSize/2;
            generateSubMesh(heights, x, z, subSize, terrainMesh);
        }
    }
    
}
