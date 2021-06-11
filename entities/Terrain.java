package entities;

import static java.lang.Math.*;
import math.OpenSimplex2F;
import math.Vec2;
import shaders.TerrainRenderManager;

public class Terrain {

  
    public static OpenSimplex2F simplex = new OpenSimplex2F(123456);
    public static int x,z;

    public Terrain(int x, int z) {
        this.x=x;
        this.z=z;
        //check if loaded from a file
        //TO BE IMPLEMENTED IF FROM FILE
        //if not, then load terrain from generator
        generateHeights(x,z);
    }
    
    public float[] getHeights(){
        return new float[1];
    }
    
    /**
     * Converts world space into terrain coordinates.
     * @param wx X in world space.
     * @param wz Z in world space.
     * @return Vector representing an integer value of the terrain the given values are in.
     */
    public static Vec2 getTerrainCoordinate(float wx, float wz) {
        return new Vec2((float) floor(wx / TerrainRenderManager.TERRAIN_SCALE), (float) floor(wz / TerrainRenderManager.TERRAIN_SCALE));
    }
    
    public static float generativeFunction(float x,float y){
        float height = 0;
        height = (float)simplex.noise2(x*4, y*8)/100.0f;
        height += (float)pow(simplex.noise2(x*.5, y*.5)*1.5,10)/500.0f;
        return height;
    }
    
    /**
     * Generates an array of heights given terrain coordinates.
     */
    private static void generateHeights(int x, int z){
        
    }
    
    
    
}
