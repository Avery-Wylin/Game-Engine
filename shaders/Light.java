package shaders;

import org.joml.Vector3f;

public class Light {
    public Vector3f pos;
    public Vector3f col;
    public Vector3f attenuation;

    public Light(Vector3f pos, Vector3f col, Vector3f attenuation){
        this.pos = pos;
        this.col=col;
        this.attenuation = attenuation;
    }
    
}
