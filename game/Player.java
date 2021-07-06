package game;
import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;
import org.joml.Vector3f;

public class Player extends DynamicEntity{
    
    protected float speed = 1f;
    
    public Player(){
        super();
    }
    
    public void input(int key){
       
    }
    
    @Override
    public void update(float delta){
        float speed = this.speed;
        if(InputManager.isPressed(GLFW_KEY_SPACE)&&onGround){
            delta_pos.y+=speed*10;
            isDynamic=true;
        }
         if(InputManager.isPressed(GLFW_KEY_Q)){
            speed=this.speed*2;
        }
        if(InputManager.isPressed(GLFW_KEY_E)){
            delta_pos.y-=speed*2;
            isDynamic=true;
        }
         if(InputManager.isPressed(GLFW_KEY_W)&&onGround){
            delta_pos.x-=speed*(float)sin(rot.y);
            delta_pos.z-=speed*(float)cos(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_S)&&onGround){
            delta_pos.x+=speed*(float)sin(rot.y);
            delta_pos.z+=speed*(float)cos(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_D)&&onGround){
            delta_pos.x+=speed*(float)cos(rot.y);
            delta_pos.z-=speed*(float)sin(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_A)&&onGround){
            delta_pos.x-=speed*(float)cos(rot.y);
            delta_pos.z+=speed*(float)sin(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_F)){
            Vector3f dest = Scene.view.raycast();
            delta_pos.add(dest);
            isDynamic=true;
        }
        
        
        super.update(delta);
    }

    @Override
    protected void updateTransform() {
       transform
       .translation(pos.x, pos.y, pos.z)
       .scale(scale.x, scale.y, scale.z)
       .rotateAffineXYZ(0, rot.y, rot.z);
    }
    
    
    
}
