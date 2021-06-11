package game;

import static java.lang.Math.pow;
import math.TransformMatrix;
import math.Vec3;
import meshes.Mesh;
import static org.lwjgl.opengl.GL30.*;
import shaders.GLSLShader;
import shaders.LightShader;
import shaders.SkyShader;

public class SkySettings {
    
    public static Mesh skymesh = new Mesh("sky");
    public static SkyShader shader = new SkyShader();
    public static TransformMatrix skyTransform = new TransformMatrix();
    public static float size;
    
    public Vec3 zenith,horizon,albedo;
    
    public SkySettings(){
        
        zenith = new Vec3(.3f,.3f,1f);
        horizon = new Vec3(.4f,.6f,1f);
        albedo = new Vec3(.2f,.2f,.2f);
        skyTransform.setIdentity();     
        skyTransform.translate(0, 0, 0);
        size = Scene.view.farPlane;
    }
    
    public void render(){
        loadShaderSettings();
        shader.loadZenith(zenith);
        shader.loadHorizon(horizon);
        shader.loadAlbedo(albedo);
        skyTransform.setIdentity();
        skyTransform.scale(size, size, size);
        skyTransform.translate(Scene.view.pos);
        shader.loadTransformationMatrix(skyTransform);
        glDrawElements(GL_TRIANGLES, skymesh.getVertexCount(),GL_UNSIGNED_INT,0);
        skymesh.unbindVAO();
        shader.stop();
    }
    
    public void loadShaderSettings(){
        
        shader.start();
        
        skymesh.bindVAO();
    }
    
    public void updateAmbientForShaders(){
        for(GLSLShader temp:GLSLShader.loadedShaders){
            if(temp instanceof LightShader){
                ((LightShader) temp).loadZenith(zenith);
                ((LightShader) temp).loadHorizon(horizon);
                ((LightShader) temp).loadAlbedo(albedo);
            }
        }
    }
}
