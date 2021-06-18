package game;

import shaders.Light;
import shaders.ShaderSettings;
import entities.Entity;
import entities.Terrain;
import math.Vec3;
import meshes.Mesh;
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
        skySettings.horizon.set(.9f, .85f, .8f);
        //skySettings.albedo.set(0.0f, 0.0f, 0.0f);
        
         
        
        //creates a specular shader
        shader = new LightShader("specular");
        
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
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Dragon", shader, dragon_mesh));
        ShaderSettings.loadedShaderSettings.get(0).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(0).diffuseColour= new Vec3(1f,1f,1f);
        ShaderSettings.loadedShaderSettings.get(0).shine=20f;
        ShaderSettings.loadedShaderSettings.get(0).specular=2f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Player", shader, player_test_mesh));
        ShaderSettings.loadedShaderSettings.get(1).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(1).diffuseColour= new Vec3(0.5f,0.5f,1.0f);
        ShaderSettings.loadedShaderSettings.get(1).shine=3f;
        ShaderSettings.loadedShaderSettings.get(1).specular=.5f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Tree", shader, tree_mesh));
        ShaderSettings.loadedShaderSettings.get(2).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(2).diffuseColour= new Vec3(.3f,.7f,.1f);
        ShaderSettings.loadedShaderSettings.get(2).shine=3f;
        ShaderSettings.loadedShaderSettings.get(2).specular=.5f;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Plane", shader, plane_mesh));
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
            lights[0].pos.setFrom(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_R))
            lights[1].pos.setFrom(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_G))
            lights[2].pos.setFrom(player.pos);
        else if(InputManager.isPressed(GLFW.GLFW_KEY_B))
            lights[3].pos.setFrom(player.pos);
        
        if(InputManager.isPressed(GLFW.GLFW_KEY_3)){
            Vec3 ray = new Vec3();
            ray = view.raycast();
            ray.multiply(10);
            ray.add(view.pos);
            dragon.rot.setFrom(view.rot);
            dragon.pos.x=ray.x;
            dragon.pos.y=ray.y;
            dragon.pos.z=ray.z;
            dragon.updateTransform();
        }
        
        
        terrain.recenter(player.pos.x, player.pos.z,.25f);
        
        float h = terrain.getHeightAt(player.pos.x, player.pos.z);
        float slope = terrain.getHeightAt(player.pos.x+Math.copySign(.1f,player.delta_pos.x), player.pos.z+Math.copySign(.1f,player.delta_pos.z))-h;
        if(slope>.2f){
            System.out.println(slope);
            player.delta_pos.x-=Math.copySign(slope*10*player.delta_pos.x,player.delta_pos.x);
            player.delta_pos.z-=Math.copySign(slope*10*player.delta_pos.z,player.delta_pos.z);
        }
        if(player.pos.y<h){
            player.pos.y=h;
            player.delta_pos.y=0;
        }
        else if(player.pos.y>h+.01f){
            player.delta_pos.y-=.7f;
        }

        //move sun to player
        lights[0].pos.setFrom(player.pos);
        lights[0].pos.y+=100;
        lights[0].pos.x+=100;
        
        
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
