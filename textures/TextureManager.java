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


public class TextureManager {
    
    public static ArrayList<Integer> loadedTextures = new ArrayList<>();
    public static HashMap<String,Integer> loadedTextureNames = new HashMap<>();
        
    public static void loadTexture(String textureName){
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId );
        
        stbi_set_flip_vertically_on_load(true);
    try ( MemoryStack stack = stackPush() ) {
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer comp = stack.mallocInt(1);
        ByteBuffer image = stbi_load("assets/"+textureName+".png", w, h, comp,0);
        if(image == null){
            System.err.println("Failed to load "+textureName+".png" );
        }
        glTexImage2D(GL_TEXTURE_2D,0, GL_RGBA8, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE,image);
    }
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);        
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 2);        
        
        loadedTextures.add(textureId);
        loadedTextureNames.put(textureName, textureId);
    }
    
    public static void deleteAllTextures(){
        for(int texture:loadedTextures){
            GL30.glDeleteTextures(texture);
        }
        loadedTextures.clear();
        loadedTextureNames.clear();
    }
}
