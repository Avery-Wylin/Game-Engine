package entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class Entity {
    
    
    protected Matrix4f transform;
    public Vector3f modPos = new Vector3f(0,0,0);
    public Vector3f pos = new Vector3f(0,0,0);
    public Vector3f rot = new Vector3f(0,0,0);
    public Vector3f scale = new Vector3f(1,1,1);
    public boolean visible = true;
    protected boolean renderUpdate = true;
    
    
    public Entity(){
        transform = new Matrix4f();
    }
    
    protected void updateTransform(){
           transform.translation(pos.x+modPos.x,pos.y+modPos.y,pos.z+modPos.z).scale(scale).rotateXYZ(rot);
    }
    
    public void delete(){
    }
    
    public void markRenderUpdate(){
        renderUpdate=true;
    }
    
    public Matrix4f getTransform(){
        if(renderUpdate){
            updateTransform();
            renderUpdate = false;
        }
        return transform;
    }
}
