package shaders;

import entities.Entity;
import game.Main;
import java.util.ArrayList;
import java.util.LinkedList;
import math.Vec3;
import meshes.Mesh;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GL32;
import shaders.LightShader;
import shaders.GLSLShader;

public class ShaderSettings {
    
    public static ArrayList<ShaderSettings> loadedShaderSettings = new ArrayList<>();
    public static Vec3 ambient = new Vec3(.5f,.5f,.5f);
    public static float fogDensity = .02f;
    public static float fogGradient = 5f;
    public static Light[] lights = new Light[LightShader.MAX_LIGHTS];
    
    public LinkedList<Entity> assignedEntities = new LinkedList<>();
    private int shaderId;
    private GLSLShader shader;
    private int meshId;
    private Mesh mesh;
    public boolean backfaceCulling;
    public boolean isTextured;
    public int textureId;
    public float specular,shine;
    public Vec3 diffuseColour;
    public String name;
    
    public ShaderSettings(String name, LightShader shader, Mesh mesh){
        this.shader = shader; 
        this.mesh=mesh;
        backfaceCulling = true;
        textureId = 0;
        specular = 2f;
        shine = 30f;
        diffuseColour = new Vec3(.8f,.8f,.8f);
        this.name = name;
    }
    
    public void setShader(int shaderId){
        this.shaderId = shaderId;
        shader = GLSLShader.loadedShaders.get(shaderId);
    }
    
    public int getShaderId(){
        return shaderId;
    }
    
    public int getMeshId(){
        return meshId;
    }
    
    public static void loadGlobalShaderSettings() {
        //set sky *temporary
        glClearColor(0,0,0, 1f);
        
        
        for (GLSLShader temp : GLSLShader.loadedShaders) {
            temp.start();
            if (temp instanceof LightShader) {
                //load ambient and fog
                ((LightShader) temp).loadAmbient(ambient);
                ((LightShader) temp).loadFogSettings(fogDensity, fogGradient);

                //load lights *currently only 4 lights per scene, not per shader or mesh*
                for (int i = 0; i < LightShader.MAX_LIGHTS; i++) {
                    if (lights[i] != null) {
                        ((LightShader) temp).loadLight(lights[i], i);
                    }
                }
            }
            //apply global values to terrain
            if(temp instanceof TerrainShader){
               //load and fog
                ((TerrainShader) temp).loadFogSettings(fogDensity, fogGradient);

                //load lights, loads all outside lights that are shared globally by all terrains
                for (int i = 0; i < TerrainShader.MAX_LIGHTS; i++) {
                    if (lights[i] != null) {
                        ((TerrainShader) temp).loadLight(lights[i], i);
                    }
                } 
            }
        }

    }

    public void loadShaderSettings(){
        
        shader.start();
        //set culling
        if(backfaceCulling){
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        else{
            GL11.glDisable(GL_CULL_FACE);
        }
        
        
        //set texture
        if(textureId>0){
            glEnable(GL_TEXTURE_2D);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
        }
        else{
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        
        if(shader instanceof LightShader){
        //set specular & ambient
            ((LightShader)shader).loadShine(shine);
            ((LightShader)shader).loadSpecular(specular);
            ((LightShader)shader).loadDiffuseColour(diffuseColour);
        }
        
        
        //bind the mesh VAO
        mesh.bindVAO();
    }
    
    public void unloadShaderSettings(){
        //unbind mesh VAO
        mesh.unbindVAO();
    }
    
    /**
     * Assigns an entity to render under this shader setting. 
     */
    public void assignEntity(Entity e){
        assignedEntities.add(e);
    }
    
    public static void assignEntity(int renderSettingId, Entity e){
        loadedShaderSettings.get(renderSettingId).assignedEntities.add(e);
    }
    
    
    /**
     * Removes an entity under this shader setting; the entity is no longer rendered.
     */
    public void removeEntity(Entity e){
        assignedEntities.remove(e);
    }
    
    public static void removeEntity(int renderSettingId, Entity e){
        loadedShaderSettings.get(renderSettingId).assignedEntities.remove(e);
    }
    
    /**
     * Renders all entities of this Render Setting.
     */
    public void render(){
        loadShaderSettings();
        for(Entity e:assignedEntities){
            //load all variable properties
            shader.loadTransformationMatrix(e.transform);
            //draw 
            glDrawElements(GL_TRIANGLES, mesh.getVertexCount(),GL_UNSIGNED_INT,0);
        }
        unloadShaderSettings();
    }
    
    /**
     * Renders all entities of all shader settings.
     */
    public static void renderAll(){
        loadGlobalShaderSettings();
        for(ShaderSettings temp: loadedShaderSettings){
            temp.render();
        }
    }
    
}
