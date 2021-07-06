package game;

import shaders.SkySettings;
import com.sun.prism.impl.BufferUtil;
import shaders.Light;
import shaders.ShaderSettings;
import entities.Entity;
import entities.EntityManager;
import entities.Terrain;
import static game.InputManager.cursorDepth;
import static game.InputManager.cursorX;
import static game.InputManager.cursorY;
import static java.lang.Math.random;
import java.nio.FloatBuffer;
import meshes.Mesh;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.opengl.GL30.*;
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
    
    //create FBOs
    public static FBO displayFBO = new FBO(1024,1024);
    
    //initialize Entity Manager
    EntityManager entityManager;
    
    //initialize all entities
    static Player player;
    Entity[] trees;
    static Entity marker;
    
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
        skySettings.zenith.set(1f, 0.0f,0.0f);
        skySettings.horizon.set(0.0f, 1.0f, 0.0f);
        skySettings.albedo.set(0.0f, 0.0f, 1.0f);
        
        //create FBO settings
        displayFBO.loadFBO();
        displayFBO.createDepthAttachment();
        displayFBO.createColourTexture();
        displayFBO.createDepthTexture();
        
        //creates a specular shader
        shader = new LightShader("specular");
        
        //create lights
        lights = new Light[4];
        lights[0] = new Light(new Vector3f(0f,100f,00f),new Vector3f(1f,.95f,.85f),new Vector3f(1f,0f,0f));
        lights[1] = new Light(new Vector3f(5f,20f,-10f),new Vector3f(1f,0f,0f),new Vector3f(0f,.1f,0.0f));
        lights[2] = new Light(new Vector3f(5f,20f,-10f),new Vector3f(0f,1f,0f),new Vector3f(0f,.1f,0.0f));
        lights[3] = new Light(new Vector3f(5f,20f,-10f),new Vector3f(0f,0f,1f),new Vector3f(0f,.1f,0.0f));
        
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
        ShaderSettings.loadedShaderSettings.get(2).diffuseColour= new Vector3f(.3f,.8f,.4f);
        ShaderSettings.loadedShaderSettings.get(2).shine=3f;
        ShaderSettings.loadedShaderSettings.get(2).specular=.5f;
        ShaderSettings.loadedShaderSettings.get(2).backfaceCulling=false;
        
        ShaderSettings.loadedShaderSettings.add(new ShaderSettings("Plane", shader, plane_mesh));
        ShaderSettings.loadedShaderSettings.get(3).textureId=-1;
        ShaderSettings.loadedShaderSettings.get(3).diffuseColour= new Vector3f(.86f,.83f,.4f);
        ShaderSettings.loadedShaderSettings.get(3).shine=2f;
        ShaderSettings.loadedShaderSettings.get(3).specular=.05f;
        
        
        //create terrain
        terrain = new Terrain();
        
        //create Entity Manager
        entityManager = new EntityManager();
        
        //create entities
        player = new Player();
        player.scale.mul(.5f);
        player.markRenderUpdate();
        ShaderSettings.assignEntity(1, player);
        
        marker = new Entity();
        marker.scale.mul(.1f);
        ShaderSettings.assignEntity(1, marker);
        marker.markRenderUpdate();
        entityManager.add(marker);
        
        trees = new Entity[200];
        for(int i=0;i<trees.length;i++){
            trees[i] = new Entity();
            trees[i].pos.x=(float)random()*Terrain.SCALE;
            trees[i].pos.z=(float)random()*Terrain.SCALE;
            trees[i].pos.y=terrain.getHeightAndSlope(trees[i].pos.x, trees[i].pos.z, null);
            trees[i].rot.y=(float)(random()*Math.PI*2);
            trees[i].scale.mul(1.5f*(float)random()+.5f);
            trees[i].markRenderUpdate();
            ShaderSettings.assignEntity(2, trees[i]);
            entityManager.add(trees[i]);
        }
        
        
        
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
        if (InputManager.isPressed(GLFW.GLFW_KEY_L)){
            lights[0].pos.set(player.pos);
        }
        else if (InputManager.isPressed(GLFW.GLFW_KEY_R)) {
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.add(view.pos);
            lights[1].pos.set(ray);
            

        } else if (InputManager.isPressed(GLFW.GLFW_KEY_G)) {
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.add(view.pos);
            lights[2].pos.set(ray);
            

        } else if (InputManager.isPressed(GLFW.GLFW_KEY_B)) {
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.add(view.pos);
            lights[3].pos.set(ray);
            

        } 
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
            ray.add(view.pos);
            terrain.addHeight(ray.x,ray.z,1,5);
        }
        else if(InputManager.isPressed(GLFW.GLFW_KEY_4)){
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.add(view.pos);
            terrain.addHeight(ray.x,ray.z,-1,5);
        }
        else if(InputManager.isPressed(GLFW.GLFW_KEY_5)){
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.add(view.pos);
            terrain.smoothHeight(ray.x,ray.z,5);
        }
        
        
        terrain.recenter(player.pos.x, player.pos.z,.5f);
        entityManager.setVisibleByRadius(player.pos.x, player.pos.z, 50f,.25f);
       

        //move sun to player
        lights[0].pos.set(player.pos);
        lights[0].pos.y+=25;
        lights[0].pos.x+=100;
        
        
        //update camera
        view.update(delta);
    }

    public void draw() {
        //render to display FBO
        displayFBO.start();
        
        //enable clip distance
        glEnable(GL_CLIP_DISTANCE0);
        
        //allow depth testing
        glEnable(GL_DEPTH_TEST);
        
        //clear the FBO Colour
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        //render all used meshes
        ShaderSettings.renderAll();
        Terrain.render();
        
        //disable clip distance, render sky
        glDisable(GL_CLIP_DISTANCE0);
        skySettings.renderSky();
        
        //get height under mouse if required
        if (InputManager.isPressed(GLFW_KEY_2)) {
            FloatBuffer depth = BufferUtil.newFloatBuffer(1);
            glReadPixels((int)(cursorX*displayFBO.w/InputManager.windowWidth), (int)((InputManager.windowHeight-cursorY)*displayFBO.h/InputManager.windowHeight), 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, depth);
            cursorDepth = Scene.view.convertCursorDepthToLinear(depth.get(0));
        }
        
        //swap to default FBO
        FBO.renderDefaultBuffer();
        
        //draw the depth buffer FBO
        displayFBO.draw();
    }
    
    public void unloadAssets(){
        GLSLShader.deleteAll();
        Mesh.deleteAll();
        TextureManager.deleteAllTextures();
        FBO.deleteAllFBOs();
    }


}
