package shaders;

import math.Vec3;
import shaders.LightShader;
import shaders.GLSLShader;

public class Light {
    public Vec3 pos;
    public Vec3 col;
    public Vec3 attenuation;

    public Light(Vec3 pos, Vec3 col, Vec3 attenuation){
        this.pos = pos;
        this.col=col;
        this.attenuation = attenuation;
    }
    
}
