package game;

import com.sun.javafx.util.Utils;
import entities.Entity;
import math.Vec3;


public class DynamicEntity extends Entity {
    protected static float decayRate;
    
    public boolean isDynamic = false;
    protected Vec3 delta_pos,delta_rot;
    
    public DynamicEntity(){
        super();
        delta_pos = new Vec3();
        delta_rot = new Vec3();
    }
    
    
    public void update(float delta){
        
        //actions must be performed on an entity to set dynamic to true
        if(isDynamic){
            applyDeltas(delta);
            updateTransform();
            checkDynamic();
       }
    }
    
    public void applyDeltas(float delta){
        //apply deltas
        pos.addScaled(delta_pos,delta);
        rot.addScaled(delta_rot,delta);
       
        //decay deltas
        delta_pos.multiply(decayRate);
        delta_rot.multiply(decayRate);
    }
    
    /**
     * Checks whether or not the entity is currently dynamic and sets the dynamic state. 
     */
    public void checkDynamic(){
        isDynamic = delta_pos.isSignificant(.05f)||delta_rot.isSignificant(.005f);
    }
    
    public static void setDecayRate(float delta){
        //multiply delta*1000 to obtain the operations per second
        decayRate = (float)Math.pow(.95,(int)(delta*1000)/8);
    }
}
