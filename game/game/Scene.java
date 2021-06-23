package game;

import shaders.Light;
import shaders.ShaderSettings;
import entities.Entity;
import entities.Terrain;
import meshes.Mesh;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import shaders.FBO;
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
    public SkySettings skySettings;
    
    //initialize all lights
    Light[] lights;
    
    //initialize terrain
    Terrain terrain;
    
    //total time
    float totaltime = 0;

    public Scene() {
        init();
    }
    
    private void init(){
        //load textures
        
        //load meshes
        Mesh dragon_mesh = new Mesh("dragon");
        Mesh player_test_mesh = new Mesh("player test");
        Mesh tree_mesh = new Mesh("tree");
        Mesh plane_mesh = new Mesh("plane");
        
        //load sky settings
        skySettings = new SkySettings();
        //skySettings.zenith.set(0.0f, 0.0f, 0.2f);
        skySettings.horizon.set(.9f, .7f, .9f);
        //skySettings.albedo.set(0.0f, 0.0f, 0.0f);
        
         
        
        //creates a specular shader
        shader = new LightShader("specular");
        
        //create lights
        lights = new Light[4];
        lights[0] = new Light(new Vector3f(0f,100f,00f),new Vector3f(1f,.9f,.6f),new Vector3f(1f,0f,0f));
        lights[1] = new Light(new Vector3f(5f,20f,-10f),new Vector3f(1f,0f,0f),new Vector3f(1f,0f,.1f));
        lights[2] = new Light(new Vector3f(5f,20f,-10f),new Vector3f(0f,1f,0f),new Vector3f(1f,0f,.1f));
        lights[3] = new Light(new Vector3f(5f,20f,-10f),new Vector3f(0f,0f,1f),new Vector3f(1f,0f,.1f));
        
        ShaderSettings.lights[0]=lights[0];
        ShaderSettings.lights[1]=lights[1];
        ShaderSettings.lights[2]=lights[2];
        ShaderSettings.lights[3]=lights[3];
        
        //create render settings
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Dragon", shader, dragon_mesh));
        ShaderSettings.loadedShaderSettings.get(0).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(0).diffuseColour= new Vector3f(1f,1f,1f);
        ShaderSettings.loadedShaderSettings.get(0).shine=20f;
        ShaderSettings.loadedShaderSettings.get(0).specular=2f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Player", shader, player_test_mesh));
        ShaderSettings.loadedShaderSettings.get(1).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(1).diffuseColour= new Vector3f(0.5f,0.5f,1.0f);
        ShaderSettings.loadedShaderSettings.get(1).shine=3f;
        ShaderSettings.loadedShaderSettings.get(1).specular=.5f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Tree", shader, tree_mesh));
        ShaderSettings.loadedShaderSettings.get(2).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(2).diffuseColour= new Vector3f(.3f,.7f,.1f);
        ShaderSettings.loadedShaderSettings.get(2).shine=3f;
        ShaderSettings.loadedShaderSettings.get(2).specular=.5f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Plane", shader, plane_mesh));
        ShaderSettings.loadedShaderSettings.get(3).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(3).diffuseColour= new Vector3f(.86f,.83f,.4f);
        ShaderSettings.loadedShaderSettings.get(3).shine=2f;
        ShaderSettings.loadedShaderSettings.get(3).specular=.05f;
        
        //create entities
         
        dragon = new Entity();
        dragon.scale.mul(.2f);
        dragon.updateTransform();
        
        player = new Player();
        player.scale.mul(.5f);
        player.updateTransform();
        
        
        
        //assign entities to render settings
        ShaderSettings.assignEntity(0, dragon);
        ShaderSettings.assignEntity(1, player);
        
        //create terrain
        terrain = new Terrain();
        
        //reload camera perspective matrix
        view.updatePerspective();
        skySettings.updateAmbientForShaders();
        
    }

    public void update(float delta) {
        totaltime+=delta;
        //set dynamic adjusted time values
        DynamicEntity.setDecayRate(delta);
        
        
        //loop player
        if(player.pos.x>=Terrain.SCALE){
            player.pos.x=0;
        }
        else if(player.pos.x<0){
            player.pos.x=Terrain.SCALE;
        }
        if(player.pos.z>=Terrain.SCALE){
            player.pos.z=0;
        }
        else if(player.pos.z<0){
            player.pos.z=Terrain.SCALE;
        }
        
       
        
        player.update(delta);
        
        //key inputs
        if(InputManager.isPressed(GLFW.GLFW_KEY_L))
            lights[0].pos.set(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_R))
            lights[1].pos.set(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_G))
            lights[2].pos.set(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_B))
            lights[3].pos.set(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_DOWN)){
            Scene.view.FOV-=1;
            Scene.view.updatePerspective();
        }
        else if(InputManager.isPressed(GLFW.GLFW_KEY_UP)){
            Scene.view.FOV+=1;
            Scene.view.updatePerspective();
        }
        
        if(InputManager.isPressed(GLFW.GLFW_KEY_3)){
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.mul(10);
            ray.add(view.pos);
            dragon.rot.set(view.rot);
            dragon.pos.set(ray);
            dragon.updateTransform();
        }
        
        
        terrain.recenter(player.pos.x, player.pos.z,.25f);
        
       

        //move sun to player
        lights[0].pos.set(player.pos);
        lights[0].pos.y+=100;
        lights[0].pos.x+=50;
        
        
        //update camera
        view.update(delta);
    }

    public void draw() {
        ShaderSettings.renderAll();
        Terrain.render();
        skySettings.renderSky();
    }
    
    public void unloadAssets(){
        GLSLShader.deleteAll();
        Mesh.removeAll();
        TextureManager.deleteAllTextures();
        FBO.deleteAllFBOs();
    }


}
