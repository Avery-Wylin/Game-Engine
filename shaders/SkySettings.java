package shaders;

import game.Scene;
import meshes.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import shaders.GLSLShader;
import shaders.LightShader;
import shaders.SkyShader;
import shaders.TerrainShader;

public class SkySettings {
    
    public static Mesh skymesh = new Mesh("sky");
    public static SkyShader shader = new SkyShader();
    public static Matrix4f skyTransform = new Matrix4f();
    public static float size;
    
    public Vector3f zenith,horizon,albedo;
    
    public SkySettings(){
        
        zenith = new Vector3f(.3f,.25f,1f);
        horizon = new Vector3f(.8f,.8f,1f);
        albedo = new Vector3f(.2f,.2f,.2f);
        skyTransform.identity();     
        size = Scene.view.farPlane;
    }
    
    public void renderSky(){
        loadShaderSettings();
        shader.loadZenith(zenith);
        shader.loadHorizon(horizon);
        shader.loadAlbedo(albedo);
        skyTransform.translation(Scene.view.pos).scale(size);
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
            temp.start();
            if(temp instanceof LightShader){
                ((LightShader) temp).loadZenith(zenith);
                ((LightShader) temp).loadHorizon(horizon);
                ((LightShader) temp).loadAlbedo(albedo);
            }
            if(temp instanceof TerrainShader){
                ((TerrainShader) temp).loadZenith(zenith);
                ((TerrainShader) temp).loadHorizon(horizon);
                ((TerrainShader) temp).loadAlbedo(albedo);
            }
        }
    }
}
