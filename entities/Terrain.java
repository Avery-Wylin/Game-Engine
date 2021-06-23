package entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.*;
import java.util.ArrayList;
import math.OpenSimplex2F;
import meshes.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import shaders.TerrainShader;
import textures.TextureManager;

public class Terrain {

    static int RENDER_COUNT = 150;
    public static int SIDE_SQUARE_COUNT = 300;
    public static float SCALE = 300f;
    public static int TOTAL_SQUARE_COUNT = SIDE_SQUARE_COUNT * SIDE_SQUARE_COUNT;
    public static int HEIGHT_COUNT = (SIDE_SQUARE_COUNT) * (SIDE_SQUARE_COUNT);
    public static float SQUARE_SCALE = 1f / (SIDE_SQUARE_COUNT);
    public static Mesh terrainMesh = new Mesh();
    public static TerrainShader shader = new TerrainShader();
    static int x = 0;
    static int z = 0;
    static int textureTop = TextureManager.loadTexture("grass");
    static int textureSide = TextureManager.loadTexture("rock");
      

    public static OpenSimplex2F simplex = new OpenSimplex2F(123456);
    private float[] heights;

    public Terrain() {
        heights = new float[HEIGHT_COUNT];
        loadDataFromObj("yinyang");
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

    public static void generateSubMesh(float[] y, int cx, int cz, int renderSquares, Mesh mesh) {
        //vertex count will include left and bottom edges
        int vertexCount = (renderSquares + 1) * (renderSquares + 1);
        //total count is the number of squares that will be rendered
        int totalRenderSquares = (renderSquares * renderSquares);
        //the number of squares used by the actual terrain height data
        int heightSquares = (int)sqrt(y.length);

        float[] pos = new float[3 * vertexCount];
        int[] order = new int[6 * totalRenderSquares];

        //GENERATE POSITIONS AND UV
        int index = 0;
        int squareX;
        int squareZ;
        //iterate over each square
        for (int r = 0; r < renderSquares + 1; r++) {
            for (int c = 0; c < renderSquares + 1; c++) {
                
                //take top left coordinate and add the column number, modulate it by the square of the heights
                squareX = (cx + c) % heightSquares;
                squareZ = (cz + r) % heightSquares;
                //fix negative modulus
                squareX += squareX < 0 ? heightSquares : 0;
                squareZ += squareZ < 0 ? heightSquares : 0;
                
                //the x coordinate is the current square in world space
                pos[index] = c * SQUARE_SCALE;
                //the y coordinate is the modulated square coordinates in the height data
                pos[index + 1] = y[squareX + squareZ*heightSquares];
                //the z coordinate is the curren square in world space
                pos[index + 2] = r * SQUARE_SCALE;

                index += 3;
            }
        }

        //GENERATE ORDER
        index = 0;
        //iterate over each square
        for (int i = 0; i < totalRenderSquares; i++) {
            
            //top left
            int tl = i + i/(renderSquares);
            //top right 
            int tr = tl + 1;
            //bottom left
            int bl = tl + renderSquares + 1;
            //bottom right
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
        
        //terrains generate their uvs in the vertex shader
        float[] normal = Mesh.createSmoothNormals(pos, order);
        mesh.load(pos, order,null,normal);
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
        ArrayList<Vector3f> inHeights = new ArrayList<>();
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
                    inHeights.add(new Vector3f(
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
            reader.close();
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
        int sideCount = (int) sqrt(heights.length);
        //sort input into place
        for (int i = 0; i < heights.length; i++) {
            int index = round((inHeights.get(i).x * (sideCount-1)) + (inHeights.get(i).z * (sideCount-1) * (sideCount)));
            heights[index] = inHeights.get(i).y;
        }
        
        SIDE_SQUARE_COUNT = sideCount;
        SCALE = SIDE_SQUARE_COUNT*2;
        TOTAL_SQUARE_COUNT = SIDE_SQUARE_COUNT * SIDE_SQUARE_COUNT;
        HEIGHT_COUNT = (SIDE_SQUARE_COUNT) * (SIDE_SQUARE_COUNT);
        SQUARE_SCALE = 1f / (SIDE_SQUARE_COUNT);
    }
    
    /**
     * Returns a vector where x and z represent the normal of the triangle and the y value represents the Barycentric height.
     */
      public float getHeightAndSlope(float wx, float wz,Vector3f slope){
          //modulate the world space
          wx%=SCALE;
          wz%=SCALE;
          //fix the negative modulus
          if(wx<0){
              wx+=SCALE;
          }
          if(wz<0){
              wz+=SCALE;
          }
          //convert into square space
          int squareX = (int)(wx/SCALE*(SIDE_SQUARE_COUNT))%SIDE_SQUARE_COUNT;
          int squareZ = (int)(wz/SCALE*(SIDE_SQUARE_COUNT))%SIDE_SQUARE_COUNT;
          //find the index
          
          float tl =heights[squareX+squareZ*SIDE_SQUARE_COUNT]*SCALE;
          float tr =heights[(squareX+1)%SIDE_SQUARE_COUNT+squareZ*SIDE_SQUARE_COUNT]*SCALE;
          float bl =heights[squareX+(squareZ+1)%SIDE_SQUARE_COUNT*SIDE_SQUARE_COUNT]*SCALE;
          float br =heights[(squareX+1)%SIDE_SQUARE_COUNT+(squareZ+1)%SIDE_SQUARE_COUNT*SIDE_SQUARE_COUNT]*SCALE;
          
          float squareWX = squareX*SQUARE_SCALE*SCALE;
          float squareWZ = squareZ*SQUARE_SCALE*SCALE;
          float squareLength = SCALE*SQUARE_SCALE;
          float insideX = wx - squareWX;
          float insideZ = wz - squareWZ;
          Vector3f a;
          Vector3f b;
          Vector3f c;
          //for some reason the order matters for this barycentric function?
          if(abs(tl-br)>abs(tr-bl)){
              //split positive slope
              if(insideX+insideZ<squareLength){//top
                  a = new Vector3f(0,bl,squareLength);
                  b = new Vector3f(squareLength,tr,0);
                  c = new Vector3f(0,tl,0);
              }
              else{//bottom
                  a = new Vector3f(squareLength,tr,0);
                  b = new Vector3f(0,bl,squareLength);
                  c = new Vector3f(squareLength,br,squareLength);
              }
          }
          else{
              //split negative slope
              if(insideZ<insideX){//top
                  a = new Vector3f(squareLength,tr,0);
                  b = new Vector3f(0,tl,0);
                  c = new Vector3f(squareLength,br,squareLength);
              }
              else{//bottom
                  a = new Vector3f(0,tl,0);
                  b = new Vector3f(0,bl,squareLength);
                  c = new Vector3f(squareLength,br,squareLength);
              }
          }
          float wy = barycentric(a, b, c, insideX, insideZ);
          b.sub(a);
          c.sub(a);
          b.cross(c,a);
          a.normalize();
          slope.set(a);
          return wy;
      }
      
      
      /**
       * Returns the square for the given the world space.
       */
      public Vector2f getSquareAt(float wx, float wz){
          //modulate the world space
          wx%=SCALE;
          wz%=SCALE;
          //fix the negative modulus
          wx+=wx<0?SCALE:0;
          wz+=wz<0?SCALE:0;
          //convert into square space
          return new Vector2f(
          (int)(wx/SCALE*(SIDE_SQUARE_COUNT))%SIDE_SQUARE_COUNT,
          (int)(wz/SCALE*(SIDE_SQUARE_COUNT))%SIDE_SQUARE_COUNT);
          
      }
    
      public static void render() {
        shader.start();
        glEnable(GL_CULL_FACE);
        //set textures
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D,textureSide);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureTop);
        //set diffuse colour
        shader.loadDiffuseColour(new Vector3f(1,1,1));
        Matrix4f transform = new Matrix4f();
        transform.translate(x * SQUARE_SCALE * SCALE, 0, z * SQUARE_SCALE * SCALE);
        transform.scale(SCALE, SCALE, SCALE);
        terrainMesh.bindVAO();
        shader.loadTransformationMatrix(transform);
        glDrawElements(GL_TRIANGLES, terrainMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        
//        transform.translate(0,.1f,0);
//        shader.loadTransformationMatrix(transform);
//          glBindTexture(GL_TEXTURE_2D, 0);
//        glLineWidth(5f);
//        glDrawElements(GL_LINES, terrainMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        terrainMesh.unbindVAO();
    }
      
    public static float barycentric(Vector3f a, Vector3f b, Vector3f c,float x,float z){
        float denom = (b.z-c.z)*(a.x-c.x)+(c.x-b.x)*(a.z-c.z);
        float d0 = ((b.z-c.z)*(x-c.x)+(c.z-b.x)*(z-c.z))/denom;
        float d1 = ((c.z-a.z)*(x-c.x)+(a.x-c.x)*(z-c.z))/denom;
        float d2 = 1f-d0-d1;
        return d0 * a.y + d1 * b.y + d2 * c.y;
    }

}
