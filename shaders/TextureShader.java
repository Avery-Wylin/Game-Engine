package shaders;

import shaders.GLSLShader;
import meshes.Mesh;

public class TextureShader extends GLSLShader {
    int resolution,
        depthTexture,
        imageTexture;
    
    public TextureShader(){
        super("textureShader");
        start();
        loadTextures();
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Mesh.ATTRB_POS, "pos");
        super.bindAttribute(Mesh.ATTRB_UV, "uv");
    }

    @Override
    protected void getAllUniformLocations() {
        super.getAllUniformLocations();
        resolution = getUniformLocation("resolution");
        depthTexture = getUniformLocation("depthTexture");
        imageTexture = getUniformLocation("imageTexture");
    }
    
    public void loadResolution(float resolution){
        loadUniformFloat(this.resolution, resolution);
    }
    
    public void loadTextures(){
        loadUniformInt(this.imageTexture, 0);
        loadUniformInt(this.depthTexture, 1);
    }
    
}
