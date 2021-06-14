package game;

import math.TransformMatrix;
import math.Vec3;
import static org.lwjgl.glfw.GLFW.*;
import shaders.GLSLShader;
import static java.lang.Math.*;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.system.MemoryStack.*;

import math.Vec2;
import org.lwjgl.system.MemoryStack;

public class Camera {
    public TransformMatrix perspective;
    public TransformMatrix inversePerspective;
    public TransformMatrix cameraTransform;
    public float FOV = 100f, nearPlane = 0.1f, farPlane = 300f, aspect = 1f;
    public Vec3 pos = new Vec3();
    public Vec3 delta_pos = new Vec3();
    public Vec3 rot = new Vec3();
    
    
    public Camera(){
        perspective = new TransformMatrix();
        cameraTransform = new TransformMatrix();
        inversePerspective = new TransformMatrix();
    }
    
    /**
     * Call this method after changing FOV, nearPlan or farPlane.
     */
    public void updatePerspective(){
        perspective.setIdentity();
        perspective.perspective(FOV, aspect, nearPlane, farPlane);
        inversePerspective.copyFrom(perspective);
        inversePerspective.inverse();
        //update the perspective for all shaders
        for(GLSLShader shader:GLSLShader.loadedShaders){
            shader.start();
            shader.loadProjectionMatrix(this.perspective);
        }
    }
    
    
    /**
     * Call this method after updating rotation or position.
     */
    public void updateCameraTransform(){
       cameraTransform.setIdentity();
       cameraTransform.rotate(rot.x, rot.y, rot.z);
       cameraTransform.translate(pos.x, pos.y, pos.z);
       cameraTransform.inverse();
       for(GLSLShader shader:GLSLShader.loadedShaders){
            shader.start();
            shader.loadCameraMatrix(cameraTransform);
       }
    }
    
    
    public void update(float delta){
        rotateAround(Scene.player.pos, Scene.player.rot, 1.5f);
        updateCameraTransform();
    }
    
    public Vec3 raycast(){
        Vec2 in = new Vec2();
        in = screenToGL();
        Vec3 ray = new Vec3(in.x,in.y,-1f);
       
        inversePerspective.transform(ray);
        cameraTransform.transform(ray);
        ray.normalize();
        return ray;
    }
    
    public Vec2 screenToGL(){
        try( MemoryStack stack = stackPush()){
            DoubleBuffer xin = stack.mallocDouble(1);
            DoubleBuffer yin = stack.mallocDouble(1);
            IntBuffer wx = stack.mallocInt(1);
            IntBuffer wy = stack.mallocInt(1);
            
            glfwGetCursorPos(Main.window, xin, yin);
            glfwGetWindowSize(Main.window, wx, wy);
            Vec2 coord = new Vec2((float)(xin.get(0)),(float)(yin.get(0)));
            coord.x = 2f*coord.x/wx.get(0)-1f;
            coord.y = 1f-(2f*coord.y/wy.get(0)-1f);
            return coord;
        }catch(Exception ex){
            return new Vec2();
        }
        
    }
    
    public void rotateAround(Vec3 pos2, Vec3 rot2, float rad){
        pos.setFrom(pos2);
        rot.setFrom(rot2);
        float angle = (float)cos(rot.x);
        pos.x+=angle*rad*sin(rot.y);
        pos.z+=angle*rad*cos(rot.y);
        pos.y+=rad*(-sin(rot.x))+1f;
    }
    
    
    
}
