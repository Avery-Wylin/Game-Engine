package shaders;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import textures.TextureManager;

public class FBO {
    private static ArrayList<FBO> loadedFBOs = new ArrayList<>();
    private static int w=256,h=256;
    
    int FBOid;
    int depthId;
    
    public FBO(){
        
    }
    
    public void loadFBO(){
        FBOid = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBOid);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
    }
    
    
    public void start(){
        //unbind any used textures
        glBindTexture(GL_TEXTURE_2D, 0);
        //bind the FBO, all rendering afterwards will be done to this FBO
        glBindFramebuffer(GL_FRAMEBUFFER, FBOid);
        //change viewport size to that of FBO
        glViewport(0, 0, w, h);
        
    }
    
    public void createTexture(){
        TextureManager.loadColourFromFBO("FBOColour"+FBOid, this, w, h);
    }
    
    private int createDepthAttachment(int w,int h){
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
}
