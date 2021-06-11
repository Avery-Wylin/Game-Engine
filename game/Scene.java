package game;

import shaders.Light;
import shaders.ShaderSettings;
import entities.Entity;
import entities.Terrain;
import math.Vec2;
import math.Vec3;
import meshes.Mesh;
import org.lwjgl.glfw.GLFW;
import shaders.LightShader;
import shaders.GLSLShader;
import textures.TextureManager;

/**
 * Class to contain all graphical and technical elements.
 */
public class Scene {
    
    //create camera
    public static Camera view = new Camera();
    
    //initialize all entities
    static Player player;
    Entity dragon;
    
    //initialize all shaders
    LightShader shader;
    
    //initialize sky settings
    SkySettings skySettings;
    
    //initialize all lights
    Light[] lights;
    
    //total time
    float totaltime = 0;

    public Scene() {
        init();
    }
    
    private void init(){
        //load textures
        TextureManager.loadTexture("uvTest");
        TextureManager.loadTexture("grass");
        
        //load meshes
        Mesh dragon_mesh = new Mesh("dragon");
        Mesh player_test_mesh = new Mesh("player");
        Mesh tree_mesh = new Mesh("tree");
        Mesh plane_mesh = new Mesh("plane");
        
        //load sky settings
        skySettings = new SkySettings();
        skySettings.zenith.set(0.0f, 0.0f, 0.2f);
        skySettings.horizon.set(0.7f, 0.3f, 0.1f);
        skySettings.albedo.set(0.5f, 0.0f, 0.0f);
        
         
        
        //creates a specular shader
        new LightShader("specular");
        
        //create lights
        lights = new Light[4];
        lights[0] = new Light(new Vec3(0f,100f,00f),new Vec3(1f,.9f,.6f),new Vec3(1f,0f,0f));
        lights[1] = new Light(new Vec3(5f,20f,-10f),new Vec3(1f,0f,0f),new Vec3(1f,0f,.1f));
        lights[2] = new Light(new Vec3(5f,20f,-10f),new Vec3(0f,1f,0f),new Vec3(1f,0f,.1f));
        lights[3] = new Light(new Vec3(5f,20f,-10f),new Vec3(0f,0f,1f),new Vec3(1f,0f,.1f));
        
        ShaderSettings.lights[0]=lights[0];
        ShaderSettings.lights[1]=lights[1];
        ShaderSettings.lights[2]=lights[2];
        ShaderSettings.lights[3]=lights[3];
        
        //create render settings
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Dragon", 1, dragon_mesh));
        ShaderSettings.loadedShaderSettings.get(0).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(0).diffuseColour= new Vec3(1f,1f,1f);
        ShaderSettings.loadedShaderSettings.get(0).shine=20f;
        ShaderSettings.loadedShaderSettings.get(0).specular=2f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Player", 1, player_test_mesh));
        ShaderSettings.loadedShaderSettings.get(1).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(1).diffuseColour= new Vec3(.86f,.83f,.4f);
        ShaderSettings.loadedShaderSettings.get(1).shine=2f;
        ShaderSettings.loadedShaderSettings.get(1).specular=.05f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Tree", 1, tree_mesh));
        ShaderSettings.loadedShaderSettings.get(2).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(2).diffuseColour= new Vec3(.3f,.7f,.1f);
        ShaderSettings.loadedShaderSettings.get(2).shine=3f;
        ShaderSettings.loadedShaderSettings.get(2).specular=.5f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Plane", 1, plane_mesh));
        ShaderSettings.loadedShaderSettings.get(3).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(3).diffuseColour= new Vec3(.86f,.83f,.4f);
        ShaderSettings.loadedShaderSettings.get(3).shine=2f;
        ShaderSettings.loadedShaderSettings.get(3).specular=.05f;
        
        //create entities
         
        dragon = new Entity();
        dragon.scale.multiply(.2f);
        dragon.updateTransform();
        
        player = new Player();
        player.scale.multiply(.5f);
        player.updateTransform();
        
        
        
        //assign entities to render settings
        ShaderSettings.assignEntity(0, dragon);
        ShaderSettings.assignEntity(1, player);
        //ShaderSettings.assignEntity(3, plane);
        
        
        //reload camera perspective matrix
        view.updatePerspective();
        skySettings.updateAmbientForShaders();
        
    }

    public void update(float delta) {
        totaltime+=delta;
        //set dynamic adjusted time values
        DynamicEntity.setDecayRate(delta);
        
        
        //update dynamic entities
        player.update(delta);
        
        //key inputs
        if(InputManager.isPressed(GLFW.GLFW_KEY_L))
            lights[0].pos.copyFrom(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_R))
            lights[1].pos.copyFrom(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_G))
            lights[2].pos.copyFrom(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_B))
            lights[3].pos.copyFrom(player.pos);
        
        if(InputManager.isPressed(GLFW.GLFW_KEY_3)){
            Vec3 ray = new Vec3();
            ray = view.raycast();
            ray.multiply(10);
            ray.add(view.pos);
            dragon.rot.copyFrom(view.rot);
            dragon.pos.x=ray.x;
            dragon.pos.y=ray.y;
            dragon.pos.z=ray.z;
            dragon.updateTransform();
        }
        
        //update camera
        view.update(delta);
    }

    public void draw() {
        ShaderSettings.renderAll();
        skySettings.render();
    }
    
    public void unloadAssets(){
        GLSLShader.deleteAll();
        Mesh.removeAll();
        TextureManager.deleteAllTextures();
    }


}
