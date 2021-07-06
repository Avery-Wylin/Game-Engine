package game;

import shaders.GLSLShader;
import static java.lang.Math.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class Camera {
    public Matrix4f perspective;
    public Matrix4f inversePerspective;
    public Matrix4f cameraTransform;
    public Matrix4f inverseCameraTransform;
    public float FOV = 90f, nearPlane = 0.1f, farPlane = 350f, aspect = 1f;
    public Vector3f pos = new Vector3f();
    public Vector3f delta_pos = new Vector3f();
    public Vector3f rot = new Vector3f();
    
    
    public Camera(){
        perspective = new Matrix4f();
        cameraTransform = new Matrix4f();
        inverseCameraTransform = new Matrix4f();
        inversePerspective = new Matrix4f();
    }
    
    /**
     * Call this method after changing FOV, nearPlan or farPlane.
     */
    public void updatePerspective(){
        perspective.identity();
        perspective.perspective((float)Math.toRadians(FOV), aspect, nearPlane, farPlane);
        perspective.invertPerspective(inversePerspective);
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
       cameraTransform.identity().rotateXYZ(-rot.x,-rot.y,-rot.z).translate(-pos.x,-pos.y,-pos.z);
       cameraTransform.invert(inverseCameraTransform);
       for(GLSLShader shader:GLSLShader.loadedShaders){
            shader.start();
            shader.loadCameraMatrix(cameraTransform);
       }
    }
    
    
    public void update(float delta){
        rotateAround(Scene.player.pos, Scene.player.rot, 2f);
        updateCameraTransform();
    }
    
    public Vector3f raycast(){
        Vector2f in = new Vector2f();
        in = screenToGL();
        Vector4f ray = new Vector4f(in.x,in.y,-1f,1f);
        inversePerspective.transformProject(ray);
        Vector3f projectedRay = new Vector3f(ray.x,ray.y,ray.z);
        projectedRay.mul((InputManager.cursorDepth-.1f)*(1f/-projectedRay.z));
        projectedRay.rotateX(rot.x);
        projectedRay.rotateY(rot.y);
        projectedRay.rotateZ(rot.z);
        return projectedRay;
    }
    
    public Vector2f screenToGL(){
            Vector2f coord = new Vector2f((float)(InputManager.cursorX),(float)(InputManager.cursorY));
            coord.x = (2f*coord.x)/InputManager.windowWidth-1f;
            coord.y = 1f-(2f*coord.y)/InputManager.windowHeight;
            return coord;
        
    }
    
    public float convertCursorDepthToLinear(float depth){
        return (2.0f * nearPlane * farPlane) / (farPlane + nearPlane - (depth * 2.0f - 1.0f) * (farPlane - nearPlane));
    }
    
    public void rotateAround(Vector3f pos2, Vector3f rot2, float rad){
        pos.set(pos2);
        rot.set(rot2);
        float angle = (float)cos(rot2.x);
        pos.x+=angle*rad*sin(rot2.y);
        pos.z+=angle*rad*cos(rot2.y);
        pos.y+=rad*(-sin(rot2.x))+1f;
    }
    
    
    
    
}
