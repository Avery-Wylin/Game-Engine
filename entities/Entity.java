package entities;

import math.TransformMatrix;
import math.Vec3;


public class Entity {
    
    public TransformMatrix transform;
    public Vec3 pos = new Vec3(0,0,0);
    public Vec3 rot = new Vec3(0,0,0);
    public Vec3 scale = new Vec3(1,1,1);
    
    
    public Entity(){
        transform = new TransformMatrix();
    }
    
    public void updateTransform(){
       transform.setIdentity();
       transform.rotate(rot.x, rot.y, rot.z);
       transform.scale(scale.x, scale.y, scale.z);
       transform.translate(pos.x, pos.y, pos.z);
    }
    
    public void delete(){
    }
    
    
    
}
