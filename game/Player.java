package game;
import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;

public class Player extends DynamicEntity{
    
    protected float speed = 1f;
    
    public Player(){
        super();
    }
    
    public void input(int key){
       
    }
    
    @Override
    public void update(float delta){
         if(InputManager.isPressed(GLFW_KEY_W)){
            delta_pos.x-=speed*(float)sin(rot.y);
            delta_pos.z-=speed*(float)cos(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_S)){
            delta_pos.x+=speed*(float)sin(rot.y);
            delta_pos.z+=speed*(float)cos(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_D)){
            delta_pos.x+=speed*(float)cos(rot.y);
            delta_pos.z-=speed*(float)sin(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_A)){
            delta_pos.x-=speed*(float)cos(rot.y);
            delta_pos.z+=speed*(float)sin(rot.y);
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_SPACE)){
            delta_pos.y+=speed;
            isDynamic=true;
        }
        if(InputManager.isPressed(GLFW_KEY_E)){
            delta_pos.y-=speed;
            isDynamic=true;
        }
        super.update(delta);
        
    }

    @Override
    public void updateTransform() {
       transform.setIdentity();
       transform.rotate(0, rot.y, rot.z);
       transform.scale(scale.x, scale.y, scale.z);
       transform.translate(pos.x, pos.y, pos.z);
    }
    
    
    
}
