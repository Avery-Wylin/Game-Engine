package shaders;
import game.InputManager;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import meshes.Mesh;
import textures.TextureManager;

public class FBO {
    private static ArrayList<FBO> loadedFBOs = new ArrayList<>();
    private static TextureShader shader = new TextureShader();
    private static Mesh windowMesh = new Mesh(new float[]{-1,-1,0, 1,-1,0, 1,1,0, -1,1,0 },new int[]{0,1,2,2,3,0},new float[]{0,0,1,0,1,1,0,1},null);
    
    public int w=512,h=512;
    int FBOid;
    int depthId;
    int depthTextureId;
    int textureId;
    
    public FBO(int w, int h){
        this.w=w;
        this.h=h;
    }
    
    public void loadFBO(){
        FBOid = glGenFramebuffers();
        shader.loadResolution(w);
        glBindFramebuffer(GL_FRAMEBUFFER, FBOid);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        
        glReadBuffer(GL_DEPTH_ATTACHMENT);
    }
    
    
    public void start(){
        //unbind any used textures
        glBindTexture(GL_TEXTURE_2D, 0);
        //bind the FBO, all rendering afterwards will be done to this FBO
        glBindFramebuffer(GL_FRAMEBUFFER, FBOid);
        //change viewport size to that of FBO
        glViewport(0, 0, w, h);
        
    }
    
    public void stop(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public static void renderDefaultBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0,0,InputManager.windowWidth,InputManager.windowHeight);
    }
    
    public void createColourTexture(){
        textureId = TextureManager.loadColourFromFBO("FBOColour"+FBOid, this, w, h);
    }
    
    public void createDepthTexture(){
        depthTextureId = TextureManager.loadDepthFromFBO("FBODepth"+FBOid, this, w, h);
    }
    
    public int createDepthAttachment(){
        int depthId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, w, h);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthId);
        return depthId;
    }
    
    public int getId(){
        return FBOid;
    }
    
    public static void deleteAllFBOs(){
        for(int i=loadedFBOs.size()-1;i>=0;i--){
            FBO temp = loadedFBOs.get(i);
            glDeleteFramebuffers(temp.FBOid);
            glDeleteRenderbuffers(temp.depthId);
        }
    }
    
    public void draw(){
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, depthTextureId);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        shader.start();
        shader.loadResolution(1f/w);
        windowMesh.bindVAO();
        glDrawElements(GL_TRIANGLES, windowMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
        windowMesh.unbindVAO();
    }
}
