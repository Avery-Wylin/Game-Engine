package textures;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.*;
import shaders.FBO;


public class TextureManager {
    
    private static ArrayList<Integer> loadedTextures = new ArrayList<>();
    public static HashMap<String,Integer> loadedTextureNames = new HashMap<>();
        
    public static int loadTexture(String textureName){
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId );
        
        stbi_set_flip_vertically_on_load(true);
    try ( MemoryStack stack = stackPush() ) {
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer comp = stack.mallocInt(1);
        ByteBuffer image = stbi_load("assets/textures/"+textureName+".png", w, h, comp,0);
        if(image == null){
            System.err.println("Failed to load "+textureName+".png" );
        }
        glTexImage2D(GL_TEXTURE_2D,0, GL_RGBA8, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE,image);
    }
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);        
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 1f);        
        
        loadedTextures.add(textureId);
        loadedTextureNames.put(textureName, textureId);
        return textureId;
    }
    
    public static int loadColourFromFBO(String name,FBO fbo,int w, int h){
        glBindFramebuffer(GL_FRAMEBUFFER, fbo.getId());
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,w,h,0,GL_RGB,GL_UNSIGNED_BYTE,(ByteBuffer)null);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,textureId,0);
        loadedTextures.add(textureId);
        loadedTextureNames.put(name,textureId);
        return textureId;
    }
    
    public static int loadDepthFromFBO(String name,FBO fbo,int w, int h){
        fbo.start();
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT32,w,h,0,GL_DEPTH_COMPONENT,GL_FLOAT,(ByteBuffer)null);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,textureId,0);
        loadedTextures.add(textureId);
        loadedTextureNames.put(name,textureId);
        return textureId;
    }
    
    public static void deleteTexture(String name){
        int textureId = loadedTextureNames.get(name);
        glDeleteTextures(textureId);
        loadedTextures.remove(textureId);
        loadedTextureNames.remove(name);
    }
    
    public static void deleteAllTextures(){
        for(int texture:loadedTextures){
            GL30.glDeleteTextures(texture);
        }
        loadedTextures.clear();
        loadedTextureNames.clear();
    }
    
}
