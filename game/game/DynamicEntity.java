package game;

import entities.Entity;
import org.joml.Vector3f;


public class DynamicEntity extends Entity {
    protected static float decayRate;
    
    public boolean isDynamic = false;
    public boolean onGround = false;
    
    protected Vector3f delta_pos,delta_rot;
    protected float bounceFactor,bounceThreshold;
    
    public DynamicEntity(){
        super();
        delta_pos = new Vector3f();
        delta_rot = new Vector3f();
        bounceFactor=.2f;
        bounceThreshold=5f;
    }
    
    
    public void update(float delta){
        
        //actions must be performed on an entity to set dynamic to true
        if(isDynamic){
            
            //calculate whether the calculated positions should be modified and prevent clipping
            terrainCollision(delta);
            //apply delta transforms to their actual transforms
            applyDeltas(delta);
            //update the transformation matrix
            updateTransform();
            //check whether or not the entity is still moving
            checkDynamic();
       }
        //apply gravity
        gravity();
    }
    
    public void applyDeltas(float delta){
        //apply deltas
        delta_pos.mulAdd(delta, pos, pos);
        delta_rot.mulAdd(delta, rot, rot);
       
        //decay deltas
        if(onGround){
            delta_pos.x*=decayRate;
            delta_pos.z*=decayRate;
            delta_rot.mul(decayRate);
        }
    }
    
    /**
     * Checks whether or not the entity is currently dynamic and sets the dynamic state. 
     */
    public void checkDynamic(){
        isDynamic = isSignificant(delta_pos,.05f)||isSignificant(delta_rot,.05f);
    }
    
    private boolean isSignificant(Vector3f v,float threshold){
        return Math.abs(v.x)>threshold || Math.abs(v.y)>threshold || Math.abs(v.z)>threshold;
    }
    
    
    public static void setDecayRate(float delta){
        //multiply delta*1000 to obtain the operations per second
        decayRate = (float)Math.pow(.95,(int)(delta*1000)/8);
    }
    
    public void terrainCollision(float delta){
        //find height and slope
        Vector3f slope = new Vector3f();
        float height = Main.mainScene.terrain.getHeightAndSlope(pos.x,pos.z,slope);
        Vector3f newPos = new Vector3f();
        delta_pos.mulAdd(delta,pos, newPos);
        
        //all collisions are counted as the entity being below the mesh
        boolean sideCollision=false;
        if(newPos.y<height){
            pos.y=height;
            if((Math.abs(slope.x)>.4f || Math.abs(slope.z)>.4f)){
                if(((slope.x>=0&&delta_pos.x>=0)||(slope.x<=0&&delta_pos.x<=0))&&((slope.z>=0&&delta_pos.z>=0)||(slope.z<=0&&delta_pos.z<=0))){
                    //going with slope
                    delta_pos.y *=-bounceFactor;
                    delta_pos.x +=delta_pos.y*slope.x;
                    delta_pos.z +=delta_pos.y*slope.z;
                }
                else{
                    //going against slope
                    delta_pos.reflect(slope);
                    delta_pos.mul(bounceFactor);
                }
                sideCollision=true;
                onGround=false;
            }
            //the slope of the terrain is not significant
            if(!sideCollision){
                pos.y=height;
                //if the fall speed is too great, create a bounce
                if(delta_pos.y<-bounceThreshold){
                    delta_pos.y *=-bounceFactor;
                    delta_pos.x +=delta_pos.y*slope.x;
                    delta_pos.z +=delta_pos.y*slope.z;
                }
                else{
                    delta_pos.y=0;
                }
                onGround=true;
            }
            
        }
        else{
            onGround=false;
        }
    }
    
    public void gravity(){
        if(!onGround){
            delta_pos.y-=1.5f;
            isDynamic=true;
        }
    }
}
