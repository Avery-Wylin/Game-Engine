package shaders;

import shaders.GLSLShader;
import meshes.Mesh;

public class TextureShader extends GLSLShader {
    
    
    public TextureShader(){
        super("textureShader");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Mesh.ATTRB_POS, "pos");
        super.bindAttribute(Mesh.ATTRB_UV, "uv");
    }
    
    
}
