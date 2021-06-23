package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class Entity {
    
    public Matrix4f transform;
    public Vector3f pos = new Vector3f(0,0,0);
    public Vector3f rot = new Vector3f(0,0,0);
    public Vector3f scale = new Vector3f(1,1,1);
    
    
    public Entity(){
        transform = new Matrix4f();
    }
    
    public void updateTransform(){
       transform.translation(pos).scale(scale).rotateXYZ(rot);
    }
    
    public void delete(){
    }
    
    
    
}
